package art.openhe.handler

import art.openhe.brains.LetterSanitizer
import art.openhe.brains.Notifier
import art.openhe.model.request.LetterRequest
import art.openhe.model.response.*
import art.openhe.dao.LetterDao
import art.openhe.dao.UserDao
import art.openhe.dao.criteria.Sort
import art.openhe.dao.criteria.ValueCriteria.Companion.eq
import art.openhe.dao.criteria.ValueCriteria.Companion.isFalse
import art.openhe.dao.criteria.ValueCriteria.Companion.isNotNull
import art.openhe.dao.criteria.ValueCriteria.Companion.isNull
import art.openhe.dao.ext.find
import art.openhe.dao.ext.noResults
import art.openhe.model.Letter
import art.openhe.queue.Queues
import art.openhe.queue.producer.SqsMessageProducer
import art.openhe.util.DbUpdate
import art.openhe.util.ext.eqCriteria
import org.joda.time.DateTimeUtils
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.core.Response

@Singleton
class LetterRequestHandler
@Inject constructor(private val letterDao: LetterDao,
                    private val userDao: UserDao,
                    private val letterSanitizer: LetterSanitizer,
                    private val producer: SqsMessageProducer,
                    private val notifier: Notifier) {


    fun writeLetter(request: LetterRequest, authorId: String): HandlerResponse {
        letterDao.save(letterSanitizer.sanitize(request.applyAsSave(authorId)))?.let {
            // dispatch the letter to the mailman for delivery
            producer.publish(it, Queues.mailman)
            // if the parentId is non-null, dispatch to letterPreviewGenerator
            it.parentId?.let { letter -> producer.publish(letter, Queues.letterPreviewer) }
        }
        // update the author's lastSentLetterTimestamp
        userDao.update(DbUpdate(authorId, "lastSentLetterTimestamp" to DateTimeUtils.currentTimeMillis()))
        return EmptyResponse()
    }

    fun getLetter(id: String): HandlerResponse =
         letterDao.findById(id)?.toLetterResponse()
             ?: LetterErrorResponse(Response.Status.NOT_FOUND, id = "A letter with id $id does not exist")

    fun updateLetter(id: String, request: LetterRequest): HandlerResponse =
        letterDao.update(request.toUpdateQuery(id))?.toLetterResponse()
            ?: LetterErrorResponse(Response.Status.NOT_FOUND, id = "A letter with id $id does not exist")

    fun getSentLetters(authorId: String,
                       page: Int,
                       size: Int,
                       hearted: Boolean? = null,
                       reply: Boolean? = null): HandlerResponse =
        if (page < 1) PageErrorResponse(Response.Status.BAD_REQUEST, page = "Page must be greater than 0")
        else letterDao.find(
            page = page,
            size = size,
            sort = Sort.Letters.byWrittenTimestampDesc(),
            authorId = eq(authorId),
            hearted = hearted?.eqCriteria(),
            parentId = if (reply == true) isNotNull() else isNull()
        )?.toPageResponse()
            ?: noResults<Letter>().toPageResponse()

    fun getReceivedLetters(recipientId: String, page: Int, size: Int): HandlerResponse =
        if (page < 1) PageErrorResponse(Response.Status.BAD_REQUEST, page = "Page must be greater than 0")
        else letterDao.find(
            page = page,
            size = size,
            sort = Sort.Letters.bySentTimestampDesc(),
            recipientId = eq(recipientId),
            deleted = isFalse()
        )?.toPageResponse()
            ?: noResults<Letter>().toPageResponse()

    fun heartLetter(id: String): HandlerResponse =
        letterDao.update(DbUpdate(id, "hearted" to true))?.apply {
            notifier.receivedHeart(id, recipientAvatar, authorId, authorAvatar)
        }?.toEmptyResponse()
            ?: LetterErrorResponse(Response.Status.NOT_FOUND, id = "A letter with id $id does not exist")

    /**
     * Flags and deletes a letter
     */
    fun reportLetter(id: String): HandlerResponse =
        letterDao.update(DbUpdate(id, "flagged" to true, "deleted" to true))?.toEmptyResponse()
            ?: LetterErrorResponse(Response.Status.NOT_FOUND, id = "A letter with id $id does not exist")

    fun markAsRead(id: String): HandlerResponse =
        letterDao.update(DbUpdate(id, "readTimestamp" to DateTimeUtils.currentTimeMillis()))?.toEmptyResponse()
            ?: LetterErrorResponse(Response.Status.NOT_FOUND, id = "A letter with id $id does not exist")

    fun deleteLetter(id: String): HandlerResponse =
        letterDao.update(DbUpdate(id, "deleted" to true))?.toEmptyResponse()
            ?: LetterErrorResponse(Response.Status.NOT_FOUND, id = "A letter with id $id does not exist")

}
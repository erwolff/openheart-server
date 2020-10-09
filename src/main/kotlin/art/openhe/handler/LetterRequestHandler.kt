package art.openhe.handler

import art.openhe.brains.LetterSanitizer
import art.openhe.brains.Notifier
import art.openhe.dao.LetterDao
import art.openhe.dao.UserDao
import art.openhe.dao.criteria.Sort
import art.openhe.dao.criteria.ValueCriteria.Companion.eq
import art.openhe.dao.criteria.ValueCriteria.Companion.isFalse
import art.openhe.dao.criteria.ValueCriteria.Companion.isNotNull
import art.openhe.dao.criteria.ValueCriteria.Companion.isNull
import art.openhe.dao.ext.find
import art.openhe.model.request.LetterRequest
import art.openhe.model.response.*
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
@Inject constructor(
    private val letterDao: LetterDao,
    private val userDao: UserDao,
    private val letterSanitizer: LetterSanitizer,
    private val producer: SqsMessageProducer,
    private val notifier: Notifier
) {


    fun writeLetter(request: LetterRequest, authorId: String): HandlerResult<EmptyResponse, LetterErrorResponse> {
        letterDao.save(letterSanitizer.sanitize(request.applyAsSave(authorId)))?.let {
            // dispatch the letter to the mailman for delivery
            producer.publish(it, Queues.mailman)
            // if the parentId is non-null, dispatch to letterPreviewGenerator
            it.parentId?.let { letter -> producer.publish(letter, Queues.letterPreviewer) }
        }
        // update the author's lastSentLetterTimestamp
        userDao.update(DbUpdate(authorId, "lastSentLetterTimestamp" to DateTimeUtils.currentTimeMillis()))
        return EmptyResponse().asSuccess()
    }


    fun getLetter(id: String): HandlerResult<LetterResponse, LetterErrorResponse> =
         letterDao.findById(id)?.asLetterResponse()?.asSuccess()
             ?: LetterErrorResponse(Response.Status.NOT_FOUND, id = "A letter with id $id does not exist").asFailure()


    fun updateLetter(id: String, request: LetterRequest): HandlerResult<LetterResponse, LetterErrorResponse> =
        letterDao.update(request.toUpdateQuery(id))?.asLetterResponse()?.asSuccess()
            ?: LetterErrorResponse(Response.Status.NOT_FOUND, id = "A letter with id $id does not exist").asFailure()


    fun getSentLetters(authorId: String,
                       page: Int,
                       size: Int,
                       hearted: Boolean? = null,
                       reply: Boolean? = null
    ): HandlerPageResult<LetterResponse, PageErrorResponse> =
        if (page < 1) PageErrorResponse(Response.Status.BAD_REQUEST, page = "Supplied page must be greater than 0").asPageFailure()
        else letterDao.find(
            page = page,
            size = size,
            sort = Sort.Letters.byWrittenTimestampDesc(),
            authorId = eq(authorId),
            hearted = hearted?.eqCriteria(),
            parentId = if (reply == true) isNotNull() else isNull()
        ).asPageResponse { it.asLetterResponse() }.asSuccess()


    fun getReceivedLetters(recipientId: String, page: Int, size: Int): HandlerPageResult<LetterResponse, PageErrorResponse> =
        if (page < 1) PageErrorResponse(Response.Status.BAD_REQUEST, page = "Supplied page must be greater than 0").asPageFailure()
        else letterDao.find(
            page = page,
            size = size,
            sort = Sort.Letters.bySentTimestampDesc(),
            recipientId = eq(recipientId),
            deleted = isFalse()
        ).asPageResponse { it.asLetterResponse() }.asSuccess()


    fun heartLetter(id: String): HandlerResult<EmptyResponse, LetterErrorResponse> =
        letterDao.update(DbUpdate(id, "hearted" to true))?.apply {
            notifier.receivedHeart(id, recipientAvatar, authorId, authorAvatar)
        }?.toEmptyResponse()?.asSuccess()
            ?: LetterErrorResponse(Response.Status.NOT_FOUND, id = "A letter with id $id does not exist").asFailure()

    /**
     * Flags and deletes a letter
     */
    fun reportLetter(id: String): HandlerResult<EmptyResponse, LetterErrorResponse> =
        letterDao.update(DbUpdate(id, "flagged" to true, "deleted" to true))?.toEmptyResponse()?.asSuccess()
            ?: LetterErrorResponse(Response.Status.NOT_FOUND, id = "A letter with id $id does not exist").asFailure()


    fun markAsRead(id: String): HandlerResult<EmptyResponse, LetterErrorResponse> =
        letterDao.update(DbUpdate(id, "readTimestamp" to DateTimeUtils.currentTimeMillis()))?.toEmptyResponse()?.asSuccess()
            ?: LetterErrorResponse(Response.Status.NOT_FOUND, id = "A letter with id $id does not exist").asFailure()


    fun deleteLetter(id: String): HandlerResult<EmptyResponse, LetterErrorResponse> =
        letterDao.update(DbUpdate(id, "deleted" to true))?.toEmptyResponse()?.asSuccess()
            ?: LetterErrorResponse(Response.Status.NOT_FOUND, id = "A letter with id $id does not exist").asFailure()

}
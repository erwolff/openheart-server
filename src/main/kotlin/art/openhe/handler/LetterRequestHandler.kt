package art.openhe.handler

import art.openhe.brains.LetterSanitizer
import art.openhe.model.request.LetterRequest
import art.openhe.model.response.*
import art.openhe.dao.LetterDao
import art.openhe.dao.ext.find
import art.openhe.model.Letter
import art.openhe.queue.Queues
import art.openhe.queue.producer.SqsMessageProducer
import art.openhe.util.MongoQuery.Sort
import art.openhe.util.UpdateQuery
import org.joda.time.DateTimeUtils
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.core.Response

@Singleton
class LetterRequestHandler
@Inject constructor(private val letterDao: LetterDao,
                    private val letterSanitizer: LetterSanitizer,
                    private val producer: SqsMessageProducer) {


    fun receiveLetter(request: LetterRequest): HandlerResponse {
        //TODO: Validation
        letterDao.save(letterSanitizer.sanitize(request.applyAsSave()))?.let {
            producer.publish(it, Queues.letterProcessor)
        }
        return EmptyResponse()
    }

    fun getLetter(id: String): HandlerResponse =
         letterDao.findById(id)?.toLetterResponse()
             ?: LetterErrorResponse(Response.Status.NOT_FOUND, id = "A letter with id $id does not exist")

    fun updateLetter(id: String, request: LetterRequest): HandlerResponse =
        //TODO: Validation
        letterDao.update(id, request.toUpdateQuery())?.toLetterResponse()
            ?: LetterErrorResponse(Response.Status.NOT_FOUND, id = "A letter with id $id does not exist")

    fun getSentLetters(authorId: String,
                       page: Int,
                       size: Int,
                       hearted: Boolean? = null,
                       reply: Boolean? = null): HandlerResponse =
        if (page < 1) PageErrorResponse(Response.Status.BAD_REQUEST, page = "Page must be greater than 0")
        else letterDao.find(page, size, Sort.byWrittenTimestampDesc, authorId, hearted = hearted, reply = reply)?.toPageResponse()
            ?: PageResponse(listOf<Letter>(), page, size, 0, 0)

    fun getReceivedLetters(recipientId: String, page: Int, size: Int): HandlerResponse =
        if (page < 1) PageErrorResponse(Response.Status.BAD_REQUEST, page = "Page must be greater than 0")
        else letterDao.find(page, size, Sort.bySentTimestampDesc, recipientId = recipientId)?.toPageResponse()
            ?: PageResponse(listOf<Letter>(), page, size, 0, 0)

    fun heartLetter(id: String): HandlerResponse =
        // TODO: we should notify the author of this letter
        letterDao.update(id, UpdateQuery(
            "hearted" to true)
        )?.toLetterResponse()
            ?: LetterErrorResponse(Response.Status.NOT_FOUND, id = "A letter with id $id does not exist")

    fun markAsRead(id: String): HandlerResponse =
        // TODO: we could potentially notify the author of this letter, but for now I think we shouldn't
        letterDao.update(id, UpdateQuery(
            "readTimestamp" to DateTimeUtils.currentTimeMillis())
        )?.toLetterResponse()
            ?: LetterErrorResponse(Response.Status.NOT_FOUND, id = "A letter with id $id does not exist")

}
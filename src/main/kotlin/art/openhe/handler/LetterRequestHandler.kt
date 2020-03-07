package art.openhe.handler

import art.openhe.model.request.LetterRequest
import art.openhe.model.response.*
import art.openhe.dao.LetterDao
import art.openhe.dao.ext.findPageByAuthorId
import art.openhe.model.Letter
import art.openhe.queue.Queues
import art.openhe.queue.producer.SqsMessageProducer
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.core.Response

@Singleton
class LetterRequestHandler
@Inject constructor(private val letterDao: LetterDao,
                    private val producer: SqsMessageProducer) {


    fun receiveLetter(request: LetterRequest): HandlerResponse {
        //TODO: Validation
        letterDao.save(request.applyAsSave())?.let { producer.publish(it, Queues.letterProcessor) }
        return EmptyResponse()
    }


    fun getLetter(id: String): HandlerResponse =
         letterDao.findById(id)?.toLetterResponse()
             ?: LetterErrorResponse(Response.Status.NOT_FOUND, id = "A letter with id $id does not exist")

    fun getLetters(authorId: String, page: Int, size: Int): HandlerResponse =
        if (page < 1) PageErrorResponse(Response.Status.BAD_REQUEST, page = "Page must be greater than 0")
        else letterDao.findPageByAuthorId(authorId, page, size)?.toPageResponse()
            ?: PageResponse(listOf<Letter>(), page, size, 0, 0)


}
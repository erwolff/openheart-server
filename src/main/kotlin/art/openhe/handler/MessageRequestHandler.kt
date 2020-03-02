package art.openhe.handler

import art.openhe.model.request.MessageRequest
import art.openhe.model.response.*
import art.openhe.dao.MessageDao
import art.openhe.queue.Queues
import art.openhe.queue.producer.SqsMessageProducer
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.core.Response

@Singleton
class MessageRequestHandler
@Inject constructor(private val messageDao: MessageDao,
                    private val producer: SqsMessageProducer) {


    fun receiveMessage(request: MessageRequest): HandlerResponse {
        //TODO: Validation
        messageDao.save(request.applyAsSave())?.let { producer.publish(it, Queues.messageProcessor) }
        return EmptyResponse()
    }


    fun getMessage(id: String): HandlerResponse =
         messageDao.findById(id)?.toMessageResponse()
             ?: MessageErrorResponse(Response.Status.NOT_FOUND, id = "A message with id $id does not exist")

}
package art.openhe.handler

import art.openhe.brains.MessageProcessor
import art.openhe.model.request.MessageRequest
import art.openhe.model.response.*
import art.openhe.dao.MessageDao
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.core.Response

@Singleton
class MessageRequestHandler
@Inject constructor(private val messageDao: MessageDao,
                    private val messageProcessor: MessageProcessor) {


    fun receiveMessage(request: MessageRequest): ApiResponse<EmptyResponse, MessageErrorResponse> {
        // TODO: validation
        // TODO: put message on queue - next line is processing post-queue
        messageDao.save(request.applyAsSave())?.let { messageProcessor.process(it) }
        return EmptyResponse().toApiResponse()
    }


    fun getMessage(id: String): ApiResponse<MessageResponse, MessageErrorResponse> =
         messageDao.findById(id)?.toMessageResponse()?.toApiResponse()
             ?: MessageErrorResponse(Response.Status.NOT_FOUND, id = "A message with id $id does not exist").toApiResponse()

}
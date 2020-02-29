package art.openhe.handler

import art.openhe.model.request.MessageRequest
import art.openhe.model.response.*
import art.openhe.service.MessageService
import art.openhe.service.UserService
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.core.Response

@Singleton
class MessageRequestHandler
@Inject constructor(private val messageService: MessageService) {

    fun getMessage(id: String): ApiResponse<MessageResponse, MessageErrorResponse> =
         messageService.findById(id)?.toMessageResponse()?.toApiResponse()
             ?: MessageErrorResponse(Response.Status.NOT_FOUND, id = "A message with id $id does not exist").toApiResponse()

    fun receiveMessage(request: MessageRequest): ApiResponse<EmptyResponse, MessageErrorResponse> =
        // TODO: put the message on a queue for processing
        EmptyResponse().toApiResponse()
}
package art.openhe.handler

import art.openhe.dao.UserDao
import art.openhe.model.request.UserRequest
import art.openhe.model.response.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.core.Response

@Singleton
class UserRequestHandler
@Inject constructor(private val userDao: UserDao) {

    fun updateUser(id: String, request: UserRequest): HandlerResponse =
        //TODO: Validation
        userDao.update(request.toUpdateQuery(id))?.toUserResponse()
            ?: UserErrorResponse(Response.Status.NOT_FOUND, id = "A user with id $id does not exist")


    fun deleteUser(id: String): HandlerResponse =
        userDao.delete(id)?.let { EmptyResponse() }
            ?: UserErrorResponse(Response.Status.NOT_FOUND, id = "A user with id $id does not exist")


    fun getUser(id: String): HandlerResponse =
        userDao.findById(id)?.toUserResponse()
            ?: UserErrorResponse(Response.Status.NOT_FOUND, id = "A user with id $id does not exist")
}
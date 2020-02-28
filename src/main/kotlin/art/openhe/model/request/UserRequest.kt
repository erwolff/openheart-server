package art.openhe.model.request

import art.openhe.model.User
import art.openhe.util.AuthUtil
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.bson.types.ObjectId

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserRequest (

    val email: String? = null,
    val password: String? = null,
    val avatar: String? = null

) {

    fun applyAsSave() =
        User(id = ObjectId().toHexString(),
            email = email!!,
            password = AuthUtil.saltAndHash(password!!),
            //TODO: Generate a random avatar (from the list of animals) if null
            avatar = avatar!!)

    fun applyAsUpdate(user: User) =
        User(user.id,
            email ?: user.email,
            password?.let{ AuthUtil.saltAndHash(it) } ?: user.password,
            avatar ?: user.avatar)

}
package art.openhe.model.request

import art.openhe.model.User
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserRequest (

    val email: String? = null,
    val avatar: String? = null

) {

    fun toUser() =
        User(email = email, avatar = avatar)

    fun applyAsUpdate(user: User) =
        User(user.id,
            email ?: user.email,
            avatar ?: user.avatar)
}
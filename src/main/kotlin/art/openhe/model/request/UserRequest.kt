package art.openhe.model.request

import art.openhe.util.DbUpdate
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
data class UserRequest (

    val avatar: String? = null

) {

    fun toUpdateQuery(userId: String) =
        DbUpdate(
            userId,
            avatar?.let { "avatar" to it }
        )

}
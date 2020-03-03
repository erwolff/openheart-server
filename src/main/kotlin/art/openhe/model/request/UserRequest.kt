package art.openhe.model.request

import art.openhe.util.UpdateQuery
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserRequest (

    val avatar: String? = null

) {

    fun toUpdateQuery() =
        UpdateQuery(avatar?.let { "avatar" to it } )

}
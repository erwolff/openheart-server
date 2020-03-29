package art.openhe.model.response


data class LoginResponse (

    val user: UserResponse? = null,
    val token: SessionTokenResponse? = null,
    val firstLogin: Boolean? = false

) : EntityResponse()
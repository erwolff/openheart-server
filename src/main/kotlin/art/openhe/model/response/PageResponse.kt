package art.openhe.model.response

import art.openhe.model.Page
import javax.ws.rs.core.Response


data class PageResponse<T> (

    val content: List<T>,
    val page: Int,
    val size: Int,
    val total: Long,
    val totalPages: Int

) : HandlerResponse() {

    override
    fun toResponse(): Response = Response.ok().entity(this).build()

}

fun <T> Page<T>.toPageResponse() =
    PageResponse(content,
        page,
        size,
        total,
        totalPages
    )
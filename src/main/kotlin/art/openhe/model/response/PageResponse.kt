package art.openhe.model.response

import art.openhe.model.DbObject
import art.openhe.model.Page
import javax.ws.rs.core.Response


data class PageResponse<T: EntityResponse> (

    val content: List<T>,
    val page: Int,
    val size: Int,
    val total: Long,
    val totalPages: Int

) {

    fun toResponse(): Response = Response.ok().entity(this).build()
}

fun <DB_OBJECT: DbObject, ENTITY_RESPONSE: EntityResponse> Page<DB_OBJECT>.asPageResponse(mappingFunction: (DB_OBJECT) -> ENTITY_RESPONSE) =
    PageResponse(content.map { mappingFunction(it) },
        page,
        size,
        total,
        totalPages
    )
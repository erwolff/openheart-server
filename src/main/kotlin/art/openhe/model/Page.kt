package art.openhe.model


data class Page<T> (

    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalResults: Long,
    val totalPages: Int

)
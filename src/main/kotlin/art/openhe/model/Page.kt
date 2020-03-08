package art.openhe.model


data class Page<T> (

    val content: List<T>,
    val page: Int,
    val size: Int,
    val total: Long,
    val totalPages: Int

)
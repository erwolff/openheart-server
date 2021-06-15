package art.openhe.storage.dao.criteria


data class Pageable(
    val page: Int,
    val size: Int,
    val sort: Sort
) {
    val skip: Int
        get() = if (page == 0) 0 else page.minus(1) * size

}

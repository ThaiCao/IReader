package ir.kazemcodes.infinity.core.domain.models


data class SourceResponse(
    val success: Boolean,
    val message:String? = null,
    val prevPage:Int?=null,
    val nextPage:Int?=null,
    val books: List<Book> = emptyList(),
    val lastUpdate: Long? =null
)

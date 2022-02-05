package by.lutyjj.photkey.models

data class Photo(
    val id: String,
    val date: String?,
    val name: String,
    val location: String?,
    var imgUrl: String?
)

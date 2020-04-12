package model

data class Title(
    val id: String,
    /* editable */
    var artist: String,
    var name: String,
    var rating: String = "5",
    /* song data */
    val date: String = "",
    val songId: String? = null,
    val format: String? = null)
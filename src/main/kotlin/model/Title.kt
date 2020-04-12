package model

data class Title(
    val id: String,
    val artist: String,
    val name: String,
    /* additional stuff */
    val date: String = "",
    val rating: String = "5",
    val songId: String? = null,
    val songFormat: String? = null)
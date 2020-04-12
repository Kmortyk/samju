package model

data class Title(
    val id: Int,
    val artist: String,
    val name: String,
    /* additional stuff */
    val t: Long = System.currentTimeMillis(),
    val rating: Int = 5,
    val songId: Int? = null)
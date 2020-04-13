package storage

import model.Title
import java.io.File

enum class SortType {
    BY_ID, BY_NAME, BY_ARTIST, BY_DATE
}

interface Storage {
    /* Title */
    fun insertNewTitle() : Title
    fun updateTitle(title: Title)
    fun removeTitle(title: Title)
    fun titles(sortType: SortType = SortType.BY_ID) : List<Title>

    /* Music file */
    fun insertFile(title: Title, file: File)
    fun getFile(title: Title) : ByteArray
}
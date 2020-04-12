package storage

import model.Title

enum class SortType {
    BY_NAME, BY_ARTIST, BY_DATE
}

interface Storage {
    /* Title */
    fun addTitle(title: Title)
    fun removeTitle(title: Title)
    fun titles(sortType: SortType = SortType.BY_NAME)

    /* Music file */
    fun loadFile(path: String)
    // fun removeFile(id: Int)
}
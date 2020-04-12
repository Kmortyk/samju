package storage

import model.Title

enum class SortType {
    BY_ID, BY_NAME, BY_ARTIST, BY_DATE
}

interface Storage {
    /* Title */
    fun addTitle(title: Title)
    fun updateTitle(title: Title)
    fun removeTitle(title: Title)
    fun titles(sortType: SortType = SortType.BY_ID) : List<Title>
    fun insertNewTitle() : Title

    /* Music file */
    fun loadFile(path: String)
    // fun removeFile(id: Int)
}
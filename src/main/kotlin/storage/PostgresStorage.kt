package storage

import model.Title
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet





class PostgresStorage : Storage {

    companion object {
        private const val HOST = "127.0.0.1"
        private const val PORT = "5432"
        private const val DB_NAME = "postgres_java_app"

        private const val USER = "kmortyk2"
        private const val PASSWORD = "rNN8gEfMfQ"

        private const val TABLE_TITLES = "titles"

        const val URL = "jdbc:postgresql://$HOST:$PORT/$DB_NAME"
    }

    private val db: Connection

    init {
        // register driver
        Class.forName("org.postgresql.Driver")
        // connect to the database
        db = DriverManager.getConnection(URL, USER, PASSWORD)
    }

    /* --- Implementation ------------------------------------------------------------------------------------------- */

    override fun titles(sortType: SortType) : ArrayList<Title> {
        val query = "SELECT * from $TABLE_TITLES"
        val statement = db.createStatement()
        val rs: ResultSet = statement.executeQuery(query)
        val titles = ArrayList<Title>()

        while (rs.next()) {
            titles.add(Title(
                rs.getString("id"),
                rs.getString("artist"),
                rs.getString("name"),
                rs.getString("t"),
                rs.getString("rating"),
                rs.getString("song_id"),
                rs.getString("song_format")
            ))
        }
        return titles
    }

    override fun addTitle(title: Title) {
        TODO("Not yet implemented")
    }

    override fun removeTitle(title: Title) {
        TODO("Not yet implemented")
    }

    override fun loadFile(path: String) {
        TODO("Not yet implemented")
    }


}
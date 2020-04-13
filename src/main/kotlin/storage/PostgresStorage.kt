package storage

import model.Title
import java.io.File
import java.nio.charset.Charset
import java.sql.*
import java.util.*


class PostgresStorage : Storage {

    companion object {
        private const val HOST = "127.0.0.1"
        private const val PORT = "5432"
        private const val DB_NAME = "postgres_java_app"

        private const val USER = "kmortyk2"
        private const val PASSWORD = "rNN8gEfMfQ"

        private const val TABLE_TITLES = "titles"
        private const val TABLE_DATA = "song_data"

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

    override fun titles(sortType: SortType) : List<Title> {
        var query = "SELECT * from $TABLE_TITLES"

        when(sortType) {
            SortType.BY_ID -> query += " ORDER BY id;"
            SortType.BY_NAME -> query += " ORDER BY name;"
            SortType.BY_DATE -> query += " ORDER BY t;"
            SortType.BY_ARTIST -> query += " ORDER BY artist;"
        }

        val statement = db.createStatement()
        val rs: ResultSet = statement.executeQuery(query)
        val titles = mutableListOf<Title>()

        while (rs.next()) {
            titles.add(Title(
                id=rs.getString("id"),
                artist=rs.getString("artist"),
                name=rs.getString("name"),
                date=rs.getString("t"),
                rating=rs.getString("rating"),
                songId=rs.getString("song_id"),
                format=rs.getString("song_format")
            ))
        }
        return titles
    }

    override fun updateTitle(title: Title) {
        val query = "UPDATE titles SET artist=?, name=?, rating=?, song_id=?, song_format=? WHERE id=?;"
        val stmt = db.prepareStatement(query)

        stmt.setString(1, title.artist)
        stmt.setString(2, title.name)
        stmt.setInt(3, title.rating?.toInt() ?: 4)
        stmt.setInt(4, title.songId?.toInt() ?: 0)
        stmt.setString(5, title.format ?: "")
        stmt.setInt(6, title.id.toInt())

        println(stmt.toString())
        stmt.executeUpdate()
    }

    override fun insertNewTitle() : Title { // insert new empty row
        val stmt = db.createStatement()
        val query = "INSERT INTO titles(artist,rating) VALUES('',4);"
        stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS)
        if(stmt.generatedKeys.next()) {
            val num = stmt.generatedKeys.getLong(1)
            val query2 = "SELECT * from $TABLE_TITLES WHERE id=$num;"
            val statement = db.createStatement()
            val rs: ResultSet = statement.executeQuery(query2)

            if (rs.next()) {
                return Title(
                    id=rs.getString("id"),
                    artist=rs.getString("artist"),
                    name=rs.getString("name"),
                    date=rs.getString("t"),
                    rating=rs.getString("rating"),
                    songId=rs.getString("song_id"),
                    format=rs.getString("song_format")
                )
            }
        }
        return Title.EMPTY
    }

    override fun removeTitle(title: Title) {
        val stmt = db.createStatement()
        val query = "DELETE FROM titles WHERE id=${title.id};"
        stmt.executeUpdate(query)
    }

    override fun insertFile(title: Title, file: File) {
        val stmt = db.createStatement()
        val bts = file.readBytes()
        val query = "INSERT INTO song_data(data) VALUES ('${Base64.getEncoder().encodeToString(bts)}');";
        stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS)

        if(stmt.generatedKeys.next()) {
            val num = stmt.generatedKeys.getLong(1)
            title.songId = "$num"
            title.format = file.extension
            updateTitle(title)
        }
    }

    override fun getFile(title: Title): ByteArray {
        val stmt = db.createStatement()
        val query = "SELECT data from $TABLE_DATA WHERE id=${title.songId};"
        val rs: ResultSet = stmt.executeQuery(query)

        if(rs.next()) {
            val str = rs.getString("data")
            return Base64.getDecoder().decode(str)
        }
        return ByteArray(0)
    }
}
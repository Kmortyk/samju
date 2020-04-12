import controller.AppViewController
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.ArcType
import javafx.scene.shape.Ellipse
import javafx.stage.Stage
import model.Title
import storage.PostgresStorage
import tornadofx.*
import java.io.File
import java.net.URL


class PostgresDbView : View() {

    // storage instance
    private val storage = PostgresStorage()

    // controller for actions
    private val controller = AppViewController(storage)

    override val root = hbox {

        val titles = listOf(
            Title(1,"Samantha Stuart", "My love"),
            Title(2,"Tom Marks", "Hello, friend"),
            Title(3,"Stuart Gills", "Last dance"),
            Title(3,"Nicole Williams", "First day")
        ).observable()

        // table
        tableview(titles) {
            readonlyColumn("ID", Title::id)
            readonlyColumn("Artist", Title::artist)
            readonlyColumn("Name", Title::name)
            readonlyColumn("DataId", Title::songId)
        }

        // player
        fxml<Node>("player.fxml")
        //val node = FXMLLoader.load<AnchorPane>(URL("file:"))
        //add(node)
    }

    /* --- Common --------------------------------------------------------------------------------------------------- */

    private fun triangle(offX: Double, offY: Double) : Array<Double> {
        return arrayOf(
            40.0+offX, 0.0+offY,
            80.0+offX, 30.0+offY,
            40.0+offX, 60.0+offY,
            40.0+offX, 0.0+offY
        )
    }
}

// options: --module-path /usr/share/openjfx/lib --add-modules=javafx.controls
class SamjuApp : App(PostgresDbView::class) {
    override fun start(stage: Stage) {
        stage.isResizable = false
        super.start(stage)
    }
}
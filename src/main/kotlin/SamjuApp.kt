import controller.AppViewController
import javafx.scene.layout.*
import javafx.stage.Stage
import model.Title
import storage.PostgresStorage
import tornadofx.*


class PostgresDbView : View() {

    /* --- Model ---------------------------------------------------------------------------------------------------- */

    // storage instance
    private val storage = PostgresStorage()

    // controller for actions
    private val controller = AppViewController(storage)

    /* --- View ----------------------------------------------------------------------------------------------------- */

    private val pane: AnchorPane by fxml("layout/player.fxml")

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

        add(pane)
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
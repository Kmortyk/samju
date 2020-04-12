import controller.AppViewController
import javafx.event.EventHandler
import javafx.scene.control.Slider
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.shape.Circle
import javafx.scene.shape.Polyline
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

    // main
    private val player: AnchorPane by fxml("layout/player.fxml")
    // actions
    private val playButton: Circle by fxid("play_button")
    private val playSlider: Slider by fxid("slider")
    // stickers
    private val playView: Polyline by fxid("play")
    private val stopView: Pane by fxid("stop")

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

        changePlayState(false)
        val handler = EventHandler<MouseEvent> {
            changePlayState(!isPlayState())
        }

        playButton.onMouseClicked = handler
        playView.onMouseClicked = handler
        stopView.onMouseClicked = handler

        add(player)
    }

    /* --- Common --------------------------------------------------------------------------------------------------- */

    private fun isPlayState() : Boolean {
        return playView.isVisible
    }

    private fun changePlayState(isPlayState: Boolean) {
        playView.isVisible = isPlayState
        stopView.isVisible = !isPlayState
    }
}

// options: --module-path /usr/share/openjfx/lib --add-modules=javafx.controls
class SamjuApp : App(PostgresDbView::class) {
    override fun start(stage: Stage) {
        stage.isResizable = false
        super.start(stage)
    }
}
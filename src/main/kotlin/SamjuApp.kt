import controller.AppViewController
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.Slider
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.shape.Circle
import javafx.scene.shape.Polyline
import javafx.stage.Stage
import model.Title
import storage.PostgresStorage
import tornadofx.*

/**
 * TODO
 * 1. Подсоединить базу данных postgres
 * 2. Добавление нового тайтла в базу данных
 * 3. Удаление тайтла из базы данных
 * 4. Имзенение данных в тайтле с изменением в базе данных
 * 5. Выбор файла музки и загрузка в память
 * 6. Загрузка формата в базу данных
 * 7. Загрузка музыкального файла в базу данных
 * 8. Проигрывание музыкального файла из базы данных
 * */

class PostgresDbView : View() {

    companion object {
        const val ICON_SIZE = 16.0
        const val ICON_SMOOTH = true
    }

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
            Title(1, "Samantha Stuart", "My love"),
            Title(2, "Tom Marks", "Hello, friend"),
            Title(3, "Stuart Gills", "Last dance"),
            Title(4, "Nicole Williams", "First day")
        ).observable()

        // table
        tableview(titles) {
            prefWidth = 325.0
            readonlyColumn("id", Title::id)
            readonlyColumn("artist", Title::artist)
            readonlyColumn("name", Title::name)
            readonlyColumn("format", Title::songId)
        }

        changePlayState(false)
        val handler = EventHandler<MouseEvent> {
            changePlayState(!isPlayState())
        }

        playButton.onMouseClicked = handler
        playView.onMouseClicked = handler
        stopView.onMouseClicked = handler

        vbox {

            val ima = ImageView(Image("img/add2.png", ICON_SIZE, ICON_SIZE, false, ICON_SMOOTH))
            val imr = ImageView(Image("img/remove2.png", ICON_SIZE, ICON_SIZE, false, ICON_SMOOTH))
            val imf = ImageView(Image("img/file2.png", ICON_SIZE, ICON_SIZE, false, ICON_SMOOTH))

            menubar {
                actionable(menu(null, ima), EventHandler {

                })
                actionable(menu(null, imr), EventHandler {

                })
                actionable(menu(null, imf), EventHandler {

                })
            }
            add(player)
        }
    }

    /* --- Common --------------------------------------------------------------------------------------------------- */

    private fun isPlayState() : Boolean {
        return playView.isVisible
    }

    private fun changePlayState(isPlayState: Boolean) {
        playView.isVisible = isPlayState
        stopView.isVisible = !isPlayState
    }

    private fun actionable(menu: Menu, ev: EventHandler<ActionEvent>) : Menu {
        val menuItem = MenuItem()
        menu.items.add(menuItem)
        menu.onAction = ev
        menu.addEventHandler(Menu.ON_SHOWN) { menu.hide() }
        menu.addEventHandler(Menu.ON_SHOWING) { menu.fire() }
        return menu
    }
}

// options: --module-path /usr/share/openjfx/lib --add-modules=javafx.controls
class SamjuApp : App(PostgresDbView::class) {
    override fun start(stage: Stage) {
        stage.isResizable = false
        super.start(stage)
    }
}
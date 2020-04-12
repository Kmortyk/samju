import controller.AppViewController
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.scene.shape.Circle
import javafx.scene.shape.Polyline
import javafx.stage.Stage
import javafx.util.Callback
import model.Title
import storage.PostgresStorage
import tornadofx.*
import view.EditingCell


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
        val titles = storage.titles().observable()
        val cellFactory = Callback<TableColumn<Title, String?>, TableCell<Title, String?>> {
            EditingCell()
        }

        // table
        tableview(titles) {
            columnResizePolicy = TableView.UNCONSTRAINED_RESIZE_POLICY
            prefWidth = 600.0

            val cells = listOf("id", "artist", "name", "format")
            for(name in cells) {
                val cell = TableColumn<Title, String>(name)
                cell.cellValueFactory = PropertyValueFactory(name)
                cell.cellFactory = cellFactory
                cell.isEditable = true
                addColumnInternal(cell)
            }
        }

        changePlayState(false)
        val handler = EventHandler<MouseEvent> {
            changePlayState(!isPlayState())
        }

        playButton.onMouseClicked = handler
        playView.onMouseClicked = handler
        stopView.onMouseClicked = handler

        vbox {

            val ima = ImageView(Image("img/add.png", ICON_SIZE, ICON_SIZE, false, ICON_SMOOTH))
            val imr = ImageView(Image("img/remove.png", ICON_SIZE, ICON_SIZE, false, ICON_SMOOTH))
            val imf = ImageView(Image("img/file.png", ICON_SIZE, ICON_SIZE, false, ICON_SMOOTH))

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
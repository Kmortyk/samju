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
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.scene.shape.Circle
import javafx.scene.shape.Polyline
import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.util.Callback
import io.SimplePlayer
import io.TmpFile
import model.Title
import storage.PostgresStorage
import tornadofx.*
import view.EditingCell
import java.nio.file.Paths


/**
 * TODO
 * 1. проигрывание музыкального файла из базы данных
 * 2. экспорт дампа названий
 * */

class PostgresDbView : View() {

    companion object {
        const val ICON_SIZE = 16.0
        const val ICON_SMOOTH = true

        val FORMATS = arrayListOf("mp3", "wav", "wma", "flac", "aiff", "ogg")
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

    private lateinit var tableView: TableView<Title>
    private var songData: ByteArray? = null

    override val root = hbox {
        val titles = storage.titles().observable()
        val cellFactory = Callback<TableColumn<Title, String?>, TableCell<Title, String?>> {
            EditingCell()
        }

        // table
        tableView = tableview(titles) {
            isEditable = true
            columnResizePolicy = TableView.UNCONSTRAINED_RESIZE_POLICY
            prefWidth = 600.0

            val cols = listOf("id", "artist", "name", "rating", "format")
            for(name in cols) {
                val col = TableColumn<Title, String>(name)
                col.cellValueFactory = PropertyValueFactory(name)
                col.cellFactory = cellFactory
                col.setOnEditCommit {
                    val title = it.tableView.items[it.tablePosition.row] as Title
                    when(name) {
                        "artist" -> title.artist = it.newValue
                        "rating" -> title.rating = it.newValue
                        "name" -> title.name = it.newValue
                        "format" -> title.format = it.newValue
                    }
                    storage.updateTitle(title)
                }
                columns.add(col)
            }
        }
        add(tableView)

        tableView.selectionModel.selectedItemProperty().addListener { _, _, _ ->
            val row = cur()
            if (row != null)
                changePlayState(row.songId != null)
        }


        changePlayState(false)
        val handler = EventHandler<MouseEvent> {
            val row = cur()
            // if selected row not null
            if (row != null) {
                // if can - change state
                if(row.songId != null) {
                    val playState = !isPlayState()
                    changePlayState(!isPlayState())
                    if(!playState) {
                        if(songData == null) // load data if null
                            songData = storage.getFile(row)

                        val musicFile = TmpFile.put("samju", row.format ?: "", songData ?: ByteArray(0))
                        // fixme javafx media interface is so awesome!!!
                        val hit = Media(Paths.get(musicFile.absolutePath).toUri().toString())
                        val mediaPlayer = MediaPlayer(hit)
                        mediaPlayer.play()
                    }
                }
                // otherwise - always false
                else changePlayState(false)
            }
        }

        playButton.onMouseClicked = handler
        playView.onMouseClicked = handler
        stopView.onMouseClicked = handler

        vbox {

            val ima = ImageView(Image("img/add.png", ICON_SIZE, ICON_SIZE, false, ICON_SMOOTH))
            val imr = ImageView(Image("img/remove.png", ICON_SIZE, ICON_SIZE, false, ICON_SMOOTH))
            val imf = ImageView(Image("img/file.png", ICON_SIZE, ICON_SIZE, false, ICON_SMOOTH))
            val ime = ImageView(Image("img/export.png", ICON_SIZE, ICON_SIZE, false, ICON_SMOOTH))

            menubar {
                actionable(menu(null, ima), EventHandler {
                    val title = storage.insertNewTitle()
                    titles.add(title)
                })
                actionable(menu(null, imr), EventHandler {
                    val row = cur()
                    if (row != null) {
                        titles.remove(row)
                        storage.removeTitle(row)
                    }
                })
                actionable(menu(null, imf), EventHandler {
                    val row = tableView.selectionModel.selectedItem
                    if (row != null) { // if row selected
                        val filters = mutableListOf<FileChooser.ExtensionFilter>()
                        for (f in FORMATS)
                            filters.add(FileChooser.ExtensionFilter(f, "*.$f"))
                        filters.add(FileChooser.ExtensionFilter("all formats", "*.*"))
                        // choose file path
                        val file = chooseFile("Load music file", filters.toTypedArray())[0]
                        storage.insertFile(row, file)
                        tableView.refresh()
                    }
                })
                actionable(menu(null, ime), EventHandler {
                    /* export */
                })
            }
            add(player)
        }
    }

    /* --- Common --------------------------------------------------------------------------------------------------- */

    private fun cur() = tableView.selectionModel.selectedItem

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
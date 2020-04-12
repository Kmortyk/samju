package view

import javafx.event.Event
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import model.Title

class EditingCell : TableCell<Title, String?>() {
    private var textField = TextField()

    override fun startEdit() {
        super.startEdit()
        createTextField()
        updateCell(textField, text)
        textField.selectAll()
    }

    override fun cancelEdit() {
        super.cancelEdit()
        updateCell(null, "$item")
    }

    override fun updateItem(item: String?, empty: Boolean) {
        super.updateItem(item, empty)
        if (empty) {
            updateCell(null, null)
        } else {
            if (isEditing) {
                updateCell(textField, null)
                textField.text = string
            } else {
                updateCell(null, string)
            }
        }
    }

    private fun createTextField() {
        textField.text = string
        textField.minWidth = this.width - this.graphicTextGap * 2
        textField.onKeyReleased = EventHandler {
            when(it.code) {
                KeyCode.ENTER -> commitEdit(textField.text)
                KeyCode.ESCAPE -> cancelEdit()
                else -> {}
            }
        }

        textField.focusedProperty().addListener { _, _, newValue ->
            if (!newValue!!) {
                commitEdit(textField.text)
            }
        }
    }

    override fun commitEdit(newValue: String?) {
        if (!isEditing && !newValue.equals(item)) {
            val table: TableView<Title> = tableView
            val column: TableColumn<Title, String?> = tableColumn
            val event: TableColumn.CellEditEvent<Title, String?> = TableColumn.CellEditEvent(
                table, TablePosition<Title, String?>(table, index, column),
                TableColumn.editCommitEvent(), item
            )
            Event.fireEvent(column, event)
            updateItem(item,false)
        }
        super.commitEdit(item)
    }

    private fun updateCell(graphic: Node?, text: String?) {
        this.graphic = graphic
        this.text = text
    }

    private val string: String
        get() = if (item == null) "" else item.toString()
}
package view

import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.Event
import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import model.Title
import java.util.*


class EditingCell : TableCell<Title, String?>() {
    private var textField: TextField? = null
    override fun startEdit() {
        super.startEdit()
        if (textField == null) {
            createTextField()
        }
        graphic = textField
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        Platform.runLater {
            textField!!.requestFocus()
            textField!!.selectAll()
        }
    }

    override fun cancelEdit() {
        super.cancelEdit()
        text = item
        contentDisplay = ContentDisplay.TEXT_ONLY
    }

    override fun updateItem(item: String?, empty: Boolean) {
        super.updateItem(item, empty)
        if (empty) {
            text = null
            graphic = null
        } else {
            if (isEditing) {
                if (textField != null) {
                    textField!!.text = string
                }
                graphic = textField
                contentDisplay = ContentDisplay.GRAPHIC_ONLY
            } else {
                text = string
                contentDisplay = ContentDisplay.TEXT_ONLY
            }
        }
    }

    private fun createTextField() {
        textField = TextField(string)
        textField!!.minWidth = width - graphicTextGap * 2
        textField!!.onKeyPressed = EventHandler {
            when(it.code) {
                KeyCode.ENTER -> commitEdit(textField!!.text)
                KeyCode.ESCAPE -> cancelEdit()
                KeyCode.TAB -> {
                    commitEdit(textField!!.text)
                    val nextColumn = getNextColumn(!it.isShiftDown)
                    if (nextColumn != null) {
                        tableView.edit(tableRow.index, nextColumn)
                    }
                }
                else -> { /* none */ }
            }
        }
        textField!!.focusedProperty().addListener { _, _, newValue ->
            if (!newValue!! && textField != null) {
                commitEdit(textField!!.text)
            }
        }
    }

    override fun commitEdit(newValue: String?) {
        if (!newValue.equals(item)) {
            val table: TableView<Title> = tableView
            val column: TableColumn<Title, String?> = tableColumn
            val event: TableColumn.CellEditEvent<Title, String?> = TableColumn.CellEditEvent(
                table, TablePosition<Title, String?>(table, index, column),
                TableColumn.editCommitEvent(), newValue
            )
            Event.fireEvent(column, event)
        }
        updateItem(newValue,false)
        super.commitEdit(item)
    }

    private val string: String
        get() = if (item == null) "" else item.toString()

    /**
     *
     * @param forward true gets the column to the right, false the column to the left of the current column
     * @return
     */
    private fun getNextColumn(forward: Boolean): TableColumn<Title, *>? {
        val columns: MutableList<TableColumn<Title, *>> =
            ArrayList<TableColumn<Title, *>>()
        for (column in tableView.columns) {
            columns.addAll(getLeaves(column))
        }
        //There is no other column that supports editing.
        if (columns.size < 2) {
            return null
        }
        val currentIndex = columns.indexOf(tableColumn)
        var nextIndex = currentIndex
        if (forward) {
            nextIndex++
            if (nextIndex > columns.size - 1) {
                nextIndex = 0
            }
        } else {
            nextIndex--
            if (nextIndex < 0) {
                nextIndex = columns.size - 1
            }
        }
        return columns[nextIndex]
    }

    private fun getLeaves(root: TableColumn<Title, *>): List<TableColumn<Title, *>> {
        val columns: MutableList<TableColumn<Title, *>> =
            ArrayList<TableColumn<Title, *>>()
        return if (root.columns.isEmpty()) {
            //We only want the leaves that are editable.
            if (root.isEditable) {
                columns.add(root)
            }
            columns
        } else {
            for (column in root.columns) {
                columns.addAll(getLeaves(column))
            }
            columns
        }
    }
}
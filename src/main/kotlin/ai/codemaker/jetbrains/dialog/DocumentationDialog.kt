/*
 * Copyright 2023 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.jetbrains.dialog

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*


class DocumentationDialog : DialogWrapper(true) {

    private val overrideIndent = JSpinner(SpinnerNumberModel(4, 1, 20, 1))
    private val minimalLinesLength = JSpinner(SpinnerNumberModel(0, 0, 1000, 1))
    private val visibility = ComboBox(arrayOf("All", "Public"))

    init {
        title = "Customize Documentation"
        super.init()
    }

    fun getOverrideIndent(): Int {
        return overrideIndent.value as Int
    }

    fun getMinimalLinesLength(): Int {
        return minimalLinesLength.value as Int
    }

    fun getVisibility(): String {
        return visibility.selectedItem as String
    }

    override fun createCenterPanel(): JComponent? {
        val dialogPanel = JPanel(GridLayout(3, 2))
        dialogPanel.preferredSize = Dimension(200, 100)

        dialogPanel.add(JLabel("Override indent: "))
        dialogPanel.add(overrideIndent)

        dialogPanel.add(JLabel("Minimal lines complexity: "))
        dialogPanel.add(minimalLinesLength)

        dialogPanel.add(JLabel("Member visibility: "))
        dialogPanel.add(visibility)

        return dialogPanel
    }
}
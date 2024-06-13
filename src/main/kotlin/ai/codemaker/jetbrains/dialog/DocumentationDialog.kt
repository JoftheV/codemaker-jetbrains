/*
 * Copyright 2023 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.jetbrains.dialog

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel


class DocumentationDialog : DialogWrapper(true) {

    val indentation = JSpinner(SpinnerNumberModel(4, 1, 20, 1))
    val minimalComplexity = JSpinner(SpinnerNumberModel(0, 0, 1000, 1))
    val members = ComboBox(arrayOf("All", "Public"))

    init {
        title = "Customize Documentation"
        super.init()
    }

    override fun createCenterPanel(): JComponent? {
        val dialogPanel = JPanel(GridLayout(3, 2))
        dialogPanel.preferredSize = Dimension(200, 100)

        dialogPanel.add(JLabel("Override Indent: "))
        dialogPanel.add(indentation)

        dialogPanel.add(JLabel("Minimal line complexity: "))
        dialogPanel.add(minimalComplexity)

        dialogPanel.add(JLabel("Match members: "))
        dialogPanel.add(members)

        return dialogPanel
    }
}
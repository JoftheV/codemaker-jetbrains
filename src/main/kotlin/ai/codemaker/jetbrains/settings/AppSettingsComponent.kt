/*
 * Copyright 2023 CodeMaker AI Inc. All rights reserved.
 */
package ai.codemaker.jetbrains.settings

import ai.codemaker.jetbrains.service.CodeMakerService
import ai.codemaker.sdkv2.client.model.LanguageCode
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.ActionLink
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import java.awt.FlowLayout
import javax.swing.DefaultComboBoxModel
import javax.swing.JButton
import javax.swing.JPanel

class AppSettingsComponent(project: Project) {
    private val defaultModel = "DEFAULT"
    private val systemLanguage = "SYSTEM"
    private val defaultLanguage = "DEFAULT"

    val panel: JPanel
    private val apiKeyText = JBTextField()
    private val languageCombo =
        ComboBox(arrayOf(systemLanguage, defaultLanguage) + LanguageCode.entries.map { it.name })
    private val modelCombo = ComboBox<String>()
    private val updateModelsButton = JButton("Update")
    private val codeActionsEnabledCheck = JBCheckBox()
    private val autocompletionEnabledCheck = JBCheckBox()
    private val multilineAutocompletionEnabledCheck = JBCheckBox()
    private val predictiveGenerationEnabledCheck = JBCheckBox()
    private val extendedSourceContextEnabledCheck = JBCheckBox()
    private val extendedSourceContextDepthCombo = ComboBox(arrayOf(1, 2, 3))
    private val assistantActionsEnabledCheck = JBCheckBox()
    private val assistantCodeVisionEnabledCheck = JBCheckBox()
    private val syntaxAutocorrectionEnabledCheck = JBCheckBox()
    private val endpointText = JBTextField()

    init {
        val createAccountLabel = ActionLink("Create account for free.") {
            BrowserUtil.browse("https://portal.codemaker.ai/#/register");
        }
        createAccountLabel.setExternalLinkIcon()

        updateModels()
        updateModelsButton.addActionListener {
            val service: CodeMakerService = project.getService(CodeMakerService::class.java)
            models = service.listModels().map { it.name }

            updateModels()
        }

        val modelPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        modelPanel.add(modelCombo)
        modelPanel.add(updateModelsButton)

        panel = FormBuilder.createFormBuilder()
            .addComponent(createAccountLabel)
            .addSeparator()
            .addLabeledComponent(JBLabel("API Key: "), apiKeyText, 1, false)
            .addLabeledComponent(JBLabel("Language: "), languageCombo, 1, false)
            .addLabeledComponent(JBLabel("Model: "), modelPanel, 1, false)
            .addSeparator()
            .addLabeledComponent(JBLabel("Enable autocompletion: "), autocompletionEnabledCheck, 1, false)
            .addLabeledComponent(
                JBLabel("Enable multiline autocompletion: "),
                multilineAutocompletionEnabledCheck,
                1,
                false
            )
            .addSeparator()
            .addLabeledComponent(JBLabel("Enable code actions: "), codeActionsEnabledCheck, 1, false)
            .addLabeledComponent(JBLabel("Enable predictive generation: "), predictiveGenerationEnabledCheck, 1, false)
            .addSeparator()
            .addLabeledComponent(
                JBLabel("Enable extended source context: "),
                extendedSourceContextEnabledCheck,
                1,
                false
            )
            .addLabeledComponent(JBLabel("Extended source context depth: "), extendedSourceContextDepthCombo, 1, false)
            .addSeparator()
            .addLabeledComponent(
                JBLabel("Enable assistant contextual operations: "),
                assistantActionsEnabledCheck,
                1,
                false
            )
            .addLabeledComponent(JBLabel("Enable assistant code vision: "), assistantCodeVisionEnabledCheck, 1, false)
            .addSeparator()
            .addLabeledComponent(JBLabel("Enable syntax autocorrection: "), syntaxAutocorrectionEnabledCheck, 1, false)
            .addSeparator()
            .addLabeledComponent(JBLabel("Endpoint: "), endpointText, 1, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    private fun updateModels() {
        val userModels = models ?: ArrayList()

        val allModels = mutableListOf<String>()
        allModels.add(defaultModel)
        allModels.addAll(userModels)
        modelCombo.model = DefaultComboBoxModel(allModels.toTypedArray())
    }

    var models: List<String>? = ArrayList()
        set(value) {
            field = value ?: ArrayList()
            updateModels()
        }

    var apiKey: String?
        get() = apiKeyText.text.trim()
        set(apiKey) {
            apiKeyText.text = apiKey
        }

    var model: String?
        get() {
            if (modelCombo.item == defaultModel) {
                return null
            }
            return modelCombo.item
        }
        set(item) {
            modelCombo.item = item ?: defaultModel
        }

    var codeActionsEnabled: Boolean
        get() = codeActionsEnabledCheck.isSelected
        set(enabled) {
            codeActionsEnabledCheck.isSelected = enabled
        }

    var autocompletionEnabled: Boolean
        get() = autocompletionEnabledCheck.isSelected
        set(enabled) {
            autocompletionEnabledCheck.isSelected = enabled
        }

    var multilineAutocompletionEnabled: Boolean
        get() = multilineAutocompletionEnabledCheck.isSelected
        set(enabled) {
            multilineAutocompletionEnabledCheck.isSelected = enabled
        }

    var predictiveGenerationEnabled: Boolean
        get() = predictiveGenerationEnabledCheck.isSelected
        set(enabled) {
            predictiveGenerationEnabledCheck.isSelected = enabled
        }

    var extendedSourceContextEnabled: Boolean
        get() = extendedSourceContextEnabledCheck.isSelected
        set(enabled) {
            extendedSourceContextEnabledCheck.isSelected = enabled
        }

    var extendedSourceContextDepth: Int
        get() = extendedSourceContextDepthCombo.item
        set(item) {
            extendedSourceContextDepthCombo.item = item
        }

    var language: String?
        get() {
            return languageCombo.item
        }
        set(language) {
            languageCombo.item = language
        }

    var assistantActionsEnabled: Boolean
        get() = assistantActionsEnabledCheck.isSelected
        set(enabled) {
            assistantActionsEnabledCheck.isSelected = enabled
        }

    var assistantCodeVisionEnabled: Boolean
        get() = assistantCodeVisionEnabledCheck.isSelected
        set(enabled) {
            assistantCodeVisionEnabledCheck.isSelected = enabled
        }

    var syntaxAutocorrectionEnabled: Boolean
        get() = syntaxAutocorrectionEnabledCheck.isSelected
        set(enabled) {
            syntaxAutocorrectionEnabledCheck.isSelected = enabled
        }

    var endpoint: String?
        get() = endpointText.text.trim()
        set(endpoint) {
            endpointText.text = endpoint
        }
}
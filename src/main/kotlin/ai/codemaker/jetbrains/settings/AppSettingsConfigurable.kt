/*
 * Copyright 2023 CodeMaker AI Inc. All rights reserved.
 */
package ai.codemaker.jetbrains.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import javax.swing.JComponent

class AppSettingsConfigurable(private val project: Project) : Configurable {

    private var settingsComponent: AppSettingsComponent? = null

    override fun getDisplayName(): String {
        return "CodeMaker AI"
    }

    override fun createComponent(): JComponent? {
        settingsComponent = AppSettingsComponent(project)
        return settingsComponent!!.panel
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }

    override fun isModified(): Boolean {
        val settings = AppSettingsState.instance
        return settingsComponent!!.apiKey != settings.apiKey
                || settingsComponent!!.models != settings.models
                || settingsComponent!!.model != settings.model
                || settingsComponent!!.codeActionsEnabled != settings.codeActionsEnabled
                || settingsComponent!!.autocompletionEnabled != settings.autocompletionEnabled
                || settingsComponent!!.multilineAutocompletionEnabled != settings.multilineAutocompletionEnabled
                || settingsComponent!!.predictiveGenerationEnabled != settings.predictiveGenerationEnabled
                || settingsComponent!!.extendedSourceContextEnabled != settings.extendedSourceContextEnabled
                || settingsComponent!!.extendedSourceContextDepth != settings.extendedSourceContextDepth
                || settingsComponent!!.assistantLanguage != settings.assistantLanguage
                || settingsComponent!!.assistantActionsEnabled != settings.assistantActionsEnabled
                || settingsComponent!!.assistantCodeVisionEnabled != settings.assistantCodeVisionEnabled
                || settingsComponent!!.syntaxAutocorrectionEnabled != settings.syntaxAutocorrectionEnabled
                || settingsComponent!!.endpoint != settings.endpoint
    }

    override fun apply() {
        val settings = AppSettingsState.instance
        settings.apiKey = settingsComponent!!.apiKey
        settings.models = settingsComponent!!.models
        settings.model = settingsComponent!!.model
        settings.codeActionsEnabled = settingsComponent!!.codeActionsEnabled
        settings.autocompletionEnabled = settingsComponent!!.autocompletionEnabled
        settings.multilineAutocompletionEnabled = settingsComponent!!.multilineAutocompletionEnabled
        settings.predictiveGenerationEnabled = settingsComponent!!.predictiveGenerationEnabled
        settings.extendedSourceContextEnabled = settingsComponent!!.extendedSourceContextEnabled
        settings.extendedSourceContextDepth = settingsComponent!!.extendedSourceContextDepth
        settings.assistantLanguage = settingsComponent!!.assistantLanguage
        settings.assistantActionsEnabled = settingsComponent!!.assistantActionsEnabled
        settings.assistantCodeVisionEnabled = settingsComponent!!.assistantCodeVisionEnabled
        settings.syntaxAutocorrectionEnabled = settingsComponent!!.syntaxAutocorrectionEnabled
        settings.endpoint = settingsComponent!!.endpoint
    }

    override fun reset() {
        val settings = AppSettingsState.instance
        settingsComponent!!.apiKey = settings.apiKey
        settingsComponent!!.models = settings.models
        settingsComponent!!.model = settings.model
        settingsComponent!!.codeActionsEnabled = settings.codeActionsEnabled
        settingsComponent!!.autocompletionEnabled = settings.autocompletionEnabled
        settingsComponent!!.multilineAutocompletionEnabled = settings.multilineAutocompletionEnabled
        settingsComponent!!.predictiveGenerationEnabled = settings.predictiveGenerationEnabled
        settingsComponent!!.extendedSourceContextEnabled = settings.extendedSourceContextEnabled
        settingsComponent!!.extendedSourceContextDepth = settings.extendedSourceContextDepth
        settingsComponent!!.assistantLanguage = settings.assistantLanguage
        settingsComponent!!.assistantActionsEnabled = settings.assistantActionsEnabled
        settingsComponent!!.assistantCodeVisionEnabled = settings.assistantCodeVisionEnabled
        settingsComponent!!.syntaxAutocorrectionEnabled = settings.syntaxAutocorrectionEnabled
        settingsComponent!!.endpoint = settings.endpoint
    }
}
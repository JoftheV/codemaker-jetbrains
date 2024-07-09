/*
 * Copyright 2024 CodeMaker AI Inc. All rights reserved.
 */

package ai.codemaker.jetbrains.assistant.view

import ai.codemaker.jetbrains.assistant.Message
import ai.codemaker.jetbrains.assistant.Role
import ai.codemaker.jetbrains.assistant.handler.FileResourceProvider
import ai.codemaker.jetbrains.assistant.handler.SpeechResourceHandler
import ai.codemaker.jetbrains.assistant.handler.StreamResourceHandler
import ai.codemaker.jetbrains.assistant.notification.AssistantNotifier
import ai.codemaker.jetbrains.file.FileExtensions
import ai.codemaker.jetbrains.service.CodeMakerService
import ai.codemaker.jetbrains.settings.AppSettingsState
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.ui.jcef.JBCefBrowserBase
import com.intellij.ui.jcef.JBCefBrowserBuilder
import com.intellij.ui.jcef.JBCefJSQuery
import com.intellij.util.ui.JBUI
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.cef.browser.CefBrowser
import org.cef.handler.CefLoadHandlerAdapter
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import java.awt.BorderLayout
import java.awt.Component
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.nio.charset.StandardCharsets
import java.util.*
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTextField

@Serializable
data class AssistantFeedback(val sessionId: String, val messageId: String, val vote: String)

class AssistantWindowFactory : ToolWindowFactory, DumbAware {

    companion object AssistantWindowFactory {
        const val TOOL_WINDOW_ID = "CodeMaker AI"
        const val ASSISTANT_HOME_VIEW = "file:///assistant.html"
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val assistantWindow = AssistantWindow(project)
        val content: Content = ContentFactory.getInstance().createContent(assistantWindow.contentPanel, "", false)
        toolWindow.contentManager.addContent(content)
    }

    class AssistantWindow(val project: Project) : Disposable {

        val contentPanel = JPanel()
        private val chatScreen = JBCefBrowser.create(JBCefBrowserBuilder().setCreateImmediately(false))
        private val recordAssistantFeedbackJsQuery = JBCefJSQuery.create(chatScreen as JBCefBrowserBase)
        private val messageTextField = JTextField()

        private val service: CodeMakerService = project.getService(CodeMakerService::class.java)

        init {
            contentPanel.setLayout(BorderLayout(0, 10))
            contentPanel.border = JBUI.Borders.empty(5)
            contentPanel.add(createChatPanel(), BorderLayout.CENTER)
            contentPanel.add(createMessagePanel(), BorderLayout.SOUTH)

            val connection = project.messageBus.connect()
            connection.subscribe(AssistantNotifier.ASSISTANT_TOPIC, object : AssistantNotifier {
                override fun onMessage(message: String) {
                    sendMessage(message)
                }
            })

            Disposer.register(this, recordAssistantFeedbackJsQuery)
            Disposer.register(this, connection)
        }

        override fun dispose() {
            Disposer.dispose(this)
        }

        private fun createChatPanel(): Component {
            chatScreen.setProperty(JBCefBrowserBase.Properties.NO_CONTEXT_MENU, true)
            chatScreen.loadURL(AssistantWindowFactory.ASSISTANT_HOME_VIEW)
            val resourceHandler = FileResourceProvider()
            resourceHandler.addResource("/speech") { SpeechResourceHandler(service, this) }
            resourceHandler.addResource("/") { StreamResourceHandler("webview", this) }
            chatScreen.jbCefClient.addRequestHandler(resourceHandler, chatScreen.cefBrowser)
            chatScreen.jbCefClient.addLoadHandler(LoadHandler(), chatScreen.cefBrowser)
            return chatScreen.component
        }

        private fun createMessagePanel(): Component {
            val messagePanel = JPanel()
            messagePanel.layout = BorderLayout()
            messagePanel.border = JBUI.Borders.empty(10)
            messageTextField.addKeyListener(MessageTextKeyListener())
            messagePanel.add(messageTextField, BorderLayout.CENTER)

            val sendButton = JButton("Send")
            sendButton.addActionListener {
                sendMessage()
            }
            messagePanel.add(sendButton, BorderLayout.EAST)

            return messagePanel
        }

        private fun addMessage(input: String, role: Role) {
            addMessage(UUID.randomUUID().toString(), UUID.randomUUID().toString(), input, role)
        }

        private fun addMessage(sessionId: String, messageId: String, input: String, role: Role) {
            hidePendingMessage()
            appendMessage(Message(sessionId, messageId, input, role, Date()))
        }

        private fun showPendingMessage() {
            chatScreen.cefBrowser.executeJavaScript("window.showPendingMessage()", "", 0)
        }

        private fun hidePendingMessage() {
            chatScreen.cefBrowser.executeJavaScript("window.hidePendingMessage()", "", 0)
        }

        private fun sendMessage() {
            val input = messageTextField.text.trim()
            messageTextField.text = ""
            sendMessage(input)
        }

        private fun sendMessage(input: String) {
            if (input.isNotEmpty()) {
                addMessage(input, Role.User)

                if (AppSettingsState.instance.apiKey.isNullOrEmpty()) {
                    addMessage(
                        "To use Assistant features, please first set the API Key in the Extension Settings." +
                                "\nYou can create free account [here](https://portal.codemaker.ai/#/register).",
                        Role.Assistant
                    )
                    return
                }

                showPendingMessage()

                ApplicationManager.getApplication().executeOnPooledThread {
                    try {
                        val fileEditorManager = FileEditorManager.getInstance(project)
                        val file = fileEditorManager.getSelectedEditor()?.file

                        val isAssistantActionsEnabled = AppSettingsState.instance.assistantActionsEnabled

                        if (isAssistantActionsEnabled && file != null && FileExtensions.isSupported(file.extension)) {
                            val response = service.assistantCodeCompletion(input, file)
                            addMessage(response.sessionId, response.messageId, response.message, Role.Assistant)
                        } else {
                            val response = service.assistantCompletion(input)
                            addMessage(response.sessionId, response.messageId, response.message, Role.Assistant)
                        }
                    } catch (e: Exception) {
                        addMessage("Assistant could not complete this request. Please try again.", Role.Assistant)
                    }
                }
            }
        }

        private fun appendMessage(message: Message) {
            val content = escape(message.content)
            val html = escape(renderMarkdown(message.content))
            val assistant = (message.role == Role.Assistant).toString()

            chatScreen.cefBrowser.executeJavaScript("window.appendMessage($assistant, \"${message.sessionId}\", \"${message.messageId}\", \"$content\", \"$html\")", "", 0)
        }

        private fun escape(input: String) = input.replace("\n", "\\n").replace("\"", "\\\"")

        private fun renderMarkdown(source: String): String {
            val flavour = CommonMarkFlavourDescriptor()
            val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(source)
            return HtmlGenerator(source, parsedTree, flavour).generateHtml()
        }

        private fun registerJavaScriptCallback() {
            recordAssistantFeedbackJsQuery.addHandler {
                ApplicationManager.getApplication().executeOnPooledThread {
                    val feedback = Json.decodeFromString<AssistantFeedback>(it)
                    service.assistantFeedback(feedback.sessionId, feedback.messageId, feedback.vote)
                }

                return@addHandler null
            }

            chatScreen.cefBrowser.executeJavaScript(
                """window.recordAssistantFeedback = function(sessionId, messageId, vote) {
                        const request = JSON.stringify({
                            sessionId,
                            messageId,
                            vote,
                        });
                        ${recordAssistantFeedbackJsQuery.inject("request")}
                    };""".trimIndent(),
                chatScreen.cefBrowser.url, 0)
        }

        inner class MessageTextKeyListener : KeyListener {
            override fun keyTyped(e: KeyEvent?) {
            }

            override fun keyPressed(e: KeyEvent?) {
                if (e?.keyCode == KeyEvent.VK_ENTER) {
                    sendMessage()
                }
            }

            override fun keyReleased(e: KeyEvent?) {
            }
        }

        inner class LoadHandler : CefLoadHandlerAdapter() {
            override fun onLoadingStateChange(browser: CefBrowser?, isLoading: Boolean, canGoBack: Boolean, canGoForward: Boolean) {
                registerJavaScriptCallback()
            }
        }
    }
}
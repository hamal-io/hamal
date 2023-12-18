package io.hamal.plugin.net.smtp

import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessageHelper

data class SenderConfig(
    val host: String,
    val port: Int,
    val username: String?,
    val password: String?,
    val defaultEncoding: String,
    val protocol: String,
    val debug: Boolean,
    val testConnection: Boolean,
    val auth: Boolean,
    val enableStarttls: Boolean,
    val connectionTimeout: Long,
    val timeout: Long,
    val writeTimeout: Long
)

data class Message(
    val from: String,
    val to: String,
    val subject: String,
    val content: String,
    val contentType: String,
    val priority: Int
)

interface Sender {
    fun send(config: SenderConfig, message: Message)
}

internal object SenderDefaultImpl : Sender {

    override fun send(config: SenderConfig, message: Message) {
        sender.defaultEncoding = config.defaultEncoding
        sender.host = config.host
        sender.port = config.port
        sender.username = config.username
        sender.password = config.password

        val props = sender.javaMailProperties
        props["mail.transport.protocol"] = config.protocol
        props["mail.debug"] = config.debug
        props["mail.test-connection"] = config.testConnection

        props["mail.smtp.auth"] = config.auth
        props["mail.smtp.starttls.enable"] = config.enableStarttls
        props["mail.smtp.connectiontimeout"] = config.connectionTimeout
        props["mail.smtp.timeout"] = config.timeout
        props["mail.smtp.writetimeout"] = config.writeTimeout

        sender.send(sender.createMimeMessage().also {
            MimeMessageHelper(it, true).also { helper ->
                helper.setFrom(message.from)
                helper.setTo(message.to)
                helper.setPriority(message.priority)
                helper.setSubject(message.subject)
            }

            it.setContent(message.content, message.contentType)
        })
    }

    private val sender = JavaMailSenderImpl()
}

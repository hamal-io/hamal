package io.hamal.plugin.net.smtp

import io.hamal.lib.kua.type.KuaBoolean
import io.hamal.lib.kua.type.KuaNumber
import io.hamal.lib.kua.type.KuaString
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessageHelper

data class SenderConfig(
    val host: KuaString,
    val port: KuaNumber,
    val username: KuaString?,
    val password: KuaString?,
    val defaultEncoding: KuaString,
    val protocol: KuaString,
    val debug: KuaBoolean,
    val testConnection: KuaBoolean,
    val auth: KuaBoolean,
    val enableStarttls: KuaBoolean,
    val connectionTimeout: KuaNumber,
    val timeout: KuaNumber,
    val writeTimeout: KuaNumber
)

data class Message(
    val from: KuaString,
    val to: KuaString,
    val subject: KuaString,
    val content: KuaString,
    val contentType: KuaString,
    val priority: KuaNumber
)

interface Sender {
    fun send(config: SenderConfig, message: Message)
}

internal object SenderDefaultImpl : Sender {

    override fun send(config: SenderConfig, message: Message) {
        sender.defaultEncoding = config.defaultEncoding.toString()
        sender.host = config.host.toString()
        sender.port = config.port.intValue
        sender.username = config.username.toString()
        sender.password = config.password.toString()

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
                helper.setFrom(message.from.stringValue)
                helper.setTo(message.to.stringValue)
                helper.setPriority(message.priority.intValue)
                helper.setSubject(message.subject.stringValue)
            }

            it.setContent(message.content, message.contentType.stringValue)
        })
    }

    private val sender = JavaMailSenderImpl()
}

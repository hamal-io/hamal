package io.hamal.plugin.net.smtp.function

import io.hamal.lib.kua.function.Function1In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessageHelper

class SmtpSendFunction(
    private val sender: JavaMailSenderImpl = JavaMailSenderImpl()
) : Function1In1Out<MapType, ErrorType>(
    FunctionInput1Schema(MapType::class),
    FunctionOutput1Schema(ErrorType::class)
) {

    override fun invoke(ctx: FunctionContext, arg1: MapType): ErrorType? {
        sender.defaultEncoding = arg1.getString("default_encoding")
        sender.host = arg1.getString("host")
        sender.port = arg1.getInt("port")
        sender.username = arg1.findString("username")
        sender.password = arg1.findString("password")

        val props = sender.javaMailProperties
        props["mail.transport.protocol"] = arg1.getString("protocol")
        props["mail.debug"] = arg1.getBoolean("debug")
        props["mail.test-connection"] = arg1.getBoolean("test_connection")

        props["mail.smtp.auth"] = sender.username != null || sender.password != null
        props["mail.smtp.starttls.enable"] = arg1.getBoolean("enable_starttls")
        props["mail.smtp.connectiontimeout"] = arg1.getInt("connection_timeout")
        props["mail.smtp.timeout"] = arg1.getInt("timeout")
        props["mail.smtp.writetimeout"] = arg1.getInt("write_timeout")

        val message: MimeMessage = sender.createMimeMessage()
        MimeMessageHelper(message, true).also {
            it.setFrom(arg1.getString("from"))
            it.setTo(arg1.getString("to"))
            it.setPriority(arg1.getInt("priority"))

            it.setSubject(arg1.getString("subject"))
        }

        message.setContent(
            arg1.getString("content"),
            arg1.getString("content_type")
        )

        sender.send(message)

        return null
    }

}
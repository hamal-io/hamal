package io.hamal.plugin.net.smtp

import AbstractRunnerTest
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.plugin.net.smtp.function.SmtpSendFunction
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.mail.javamail.JavaMailSenderImpl


internal object Test : AbstractRunnerTest() {

    @Test
    fun test() {
        val testSender = TestJavaMailSenderImpl()
        createTestRunner(
            pluginFactories = listOf(TestSmtpPluginFactory(testSender))
        ).run(
            unitOfWork(
                """
            smtp = require_plugin('net.smtp')
            
            smtp.send({
               host = 'host',
               port = 587,
               username = 'username',
               password = 'password',
               from = 'noreply@hamal.io',
               to = 'dominique@hamal.io',
               subject = 'subject',
               content = 'content',
               content_type = 'content_type'
            })

        """.trimIndent()
            )
        )

        println(testSender.host)
    }
}

internal class TestJavaMailSenderImpl : JavaMailSenderImpl() {
    override fun send(mimeMessage: MimeMessage) {
        mimeMessage.from.toList().also {
            assertThat(it, hasSize(1))
            assertThat(it.first(), equalTo(InternetAddress("noreply@hamal.io")))
        }

        assertThat(mimeMessage.subject, equalTo("subject"))
        assertThat(mimeMessage.content, equalTo("content"))
        assertThat(mimeMessage.contentType, equalTo("content_type"))
    }
}

internal class TestSmtpPluginFactory(
    private val senderImpl: JavaMailSenderImpl = JavaMailSenderImpl()
) : RunnerPluginFactory {
    override fun create(sandbox: Sandbox): RunnerPlugin {
        return RunnerPlugin(
            name = "net.smtp",
            internals = mapOf(
                "send" to SmtpSendFunction(senderImpl)
            )
        )
    }
}
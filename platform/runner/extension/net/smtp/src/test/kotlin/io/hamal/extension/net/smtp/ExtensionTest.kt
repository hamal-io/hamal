package io.hamal.extension.net.smtp

import io.hamal.lib.kua.type.KuaNumber
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.value.ValueFalse
import io.hamal.lib.value.ValueTrue
import io.hamal.plugin.net.smtp.Message
import io.hamal.plugin.net.smtp.PluginSmtpFactory
import io.hamal.plugin.net.smtp.Sender
import io.hamal.plugin.net.smtp.SenderConfig
import io.hamal.runner.test.AbstractRunnerTest
import io.hamal.runner.test.TestFailConnector
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal object CreateAndSendTest : AbstractRunnerTest() {

    @Test
    fun `Creates instance and send email`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            extensionFactories = listOf(ExtensionSmtpFactory)
        ).run(
            unitOfWork(
                """
            test_instance = require('net.smtp').create({
                default_encoding = 'default_encoding',
                host =  'host',
                port =  123,
                username =  'username',
                password =  'password',
                protocol =  'protocol',
                debug =  true,
                enable_starttls =  true,
                test_connection =  true,
                
                connection_timeout =   1000,
                timeout =  2000,
                write_timeout =  3000
            })
            
            test_instance.send({
                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
                content_type =  'content_type',
                priority =  42,
            })

        """.trimIndent()
            )
        )

        val config = fakeSender.config
        assertThat(config.defaultEncoding, equalTo(KuaString("default_encoding")))
        assertThat(config.host, equalTo(KuaString("host")))
        assertThat(config.port, equalTo(KuaNumber(123)))
        assertThat(config.username, equalTo(KuaString("username")))
        assertThat(config.password, equalTo(KuaString("password")))
        assertThat(config.protocol, equalTo(KuaString("protocol")))
        assertThat(config.debug, equalTo(ValueTrue))
        assertThat(config.enableStarttls, equalTo(ValueTrue))
        assertThat(config.testConnection, equalTo(ValueTrue))

        assertThat(config.connectionTimeout, equalTo(KuaNumber(1000)))
        assertThat(config.timeout, equalTo(KuaNumber(2000)))
        assertThat(config.writeTimeout, equalTo(KuaNumber(3000)))

        val msg = fakeSender.message
        assertThat(msg.from, equalTo(KuaString("from")))
        assertThat(msg.to, equalTo(KuaString("to")))
        assertThat(msg.subject, equalTo(KuaString("subject")))
        assertThat(msg.content, equalTo(KuaString("content")))
        assertThat(msg.contentType, equalTo(KuaString("content_type")))
        assertThat(msg.priority, equalTo(KuaNumber(42)))

    }

    @Test
    fun `default default_encoding is UTF-8`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            extensionFactories = listOf(ExtensionSmtpFactory)
        ).run(
            unitOfWork(
                """
            test_instance = require('net.smtp').create({
                default_encoding = nil,
                host =  'host',
            })

            test_instance.send({
                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
            })

        """.trimIndent()
            )
        )

        val config = fakeSender.config
        assertThat(config.defaultEncoding, equalTo(KuaString("UTF-8")))
    }

    @Test
    fun `host is required`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            extensionFactories = listOf(ExtensionSmtpFactory),
            connector = TestFailConnector { _, result ->
                assertThat(result.value.stringValue("message"), containsString("host not set"))
            }
        ).run(
            unitOfWork(
                """
            test_instance = require('net.smtp').create({
                host =  nil,
            })

            test_instance.send({
                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
            })

        """.trimIndent()
            )
        )
    }

    @Test
    fun `default port is 25`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            extensionFactories = listOf(ExtensionSmtpFactory)
        ).run(
            unitOfWork(
                """
            test_instance = require('net.smtp').create({
                port = nil,
                host =  'host',
            })

            test_instance.send({
                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
            })

        """.trimIndent()
            )
        )

        val config = fakeSender.config
        assertThat(config.port, equalTo(KuaNumber(25)))
    }

    @Test
    fun `username is optional`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            extensionFactories = listOf(ExtensionSmtpFactory)
        ).run(
            unitOfWork(
                """
            test_instance = require('net.smtp').create({
                username = nil,
                host =  'host',
            })

            test_instance.send({
                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
            })

        """.trimIndent()
            )
        )

        val config = fakeSender.config
        assertThat(config.username, Matchers.nullValue())
    }

    @Test
    fun `password is optional`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            extensionFactories = listOf(ExtensionSmtpFactory)
        ).run(
            unitOfWork(
                """
            test_instance = require('net.smtp').create({
                password = nil,
                host =  'host',
            })

            test_instance.send({
                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
            })

        """.trimIndent()
            )
        )

        val config = fakeSender.config
        assertThat(config.password, Matchers.nullValue())
    }

    @Test
    fun `default protocol is smtp`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            extensionFactories = listOf(ExtensionSmtpFactory)
        ).run(
            unitOfWork(
                """
            test_instance = require('net.smtp').create({
                protocol = nil,
                host =  'host',
            })

            test_instance.send({
                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
            })

        """.trimIndent()
            )
        )

        val config = fakeSender.config
        assertThat(config.protocol, equalTo(KuaString("smtp")))
    }

    @Test
    fun `default debug is false`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            extensionFactories = listOf(ExtensionSmtpFactory)
        ).run(
            unitOfWork(
                """
            test_instance = require('net.smtp').create({
                debug = nil,
                host =  'host',
            })

            test_instance.send({
                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
            })

        """.trimIndent()
            )
        )

        val config = fakeSender.config
        assertThat(config.debug, equalTo(ValueFalse))
    }

    @Test
    fun `default enable_starttls is false`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            extensionFactories = listOf(ExtensionSmtpFactory)
        ).run(
            unitOfWork(
                """
            test_instance = require('net.smtp').create({
                enable_starttls = nil,
                host =  'host',
            })

            test_instance.send({
                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
            })

        """.trimIndent()
            )
        )

        val config = fakeSender.config
        assertThat(config.enableStarttls, equalTo(ValueFalse))
    }

    @Test
    fun `default test connection is false`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            extensionFactories = listOf(ExtensionSmtpFactory)
        ).run(
            unitOfWork(
                """
            test_instance = require('net.smtp').create({
                test_connection = nil,
                host =  'host',
            })

            test_instance.send({
                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
            })

        """.trimIndent()
            )
        )

        val config = fakeSender.config
        assertThat(config.testConnection, equalTo(ValueFalse))
    }

    @Test
    fun `from is required`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            connector = TestFailConnector { _, result ->
                assertThat(result.value.stringValue("message"), containsString("from not set"))
            }
        ).run(
            unitOfWork(
                """
           test_instance = require('net.smtp').create({
                host =  'host'
            })

            test_instance.send({
                from =  nil,
                to =  'to',
                subject =  'subject',
                content =  'content',
            })

        """.trimIndent()
            )
        )
    }

    @Test
    fun `to is required`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            connector = TestFailConnector { _, result ->
                assertThat(result.value.stringValue("message"), containsString("to not set"))
            }
        ).run(
            unitOfWork(
                """
           test_instance = require('net.smtp').create({
                host =  'host'
            })

            test_instance.send({
                from =  'from',
                to = nil,
                subject =  'subject',
                content =  'content',
            })

        """.trimIndent()
            )
        )
    }

    @Test
    fun `subject is required`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            connector = TestFailConnector { _, result ->
                assertThat(result.value.stringValue("message"), containsString("subject not set"))
            }
        ).run(
            unitOfWork(
                """
          test_instance = require('net.smtp').create({
                host =  'host'
            })

            test_instance.send({
                from =  'from',
                to = 'to',
                subject =  nil,
                content =  'content',
            })

        """.trimIndent()
            )
        )
    }

    @Test
    fun `content is required`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            connector = TestFailConnector { _, result ->
                assertThat(result.value.stringValue("message"), containsString("content not set"))
            }
        ).run(
            unitOfWork(
                """
            test_instance = require('net.smtp').create({
                host =  'host'
            })

            test_instance.send({
                from =  'from',
                to = 'to',
                subject =  'subject',
                content =  nil
            })
        """.trimIndent()
            )
        )
    }

    @Test
    fun `default content_type is text_plain`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            extensionFactories = listOf(ExtensionSmtpFactory)
        ).run(
            unitOfWork(
                """
            test_instance = require('net.smtp').create({
                host =  'host'
            })

            test_instance.send({
                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
                content_type = nil
            })

        """.trimIndent()
            )
        )

        val message = fakeSender.message
        assertThat(message.contentType, equalTo(KuaString("text/plain")))
    }

    @Test
    fun `default priority is 1`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            extensionFactories = listOf(ExtensionSmtpFactory)
        ).run(
            unitOfWork(
                """
            test_instance = require('net.smtp').create({
                host =  'host'
            })

            test_instance.send({
                priority = nil,

                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
            })

        """.trimIndent()
            )
        )

        val message = fakeSender.message
        assertThat(message.priority, equalTo(KuaNumber(1)))
    }

    @Test
    fun `default connection_timeout is 5000`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            extensionFactories = listOf(ExtensionSmtpFactory)
        ).run(
            unitOfWork(
                """
            test_instance = require('net.smtp').create({
                connection_timeout = nil,            
                host =  'host'
            })

            test_instance.send({
                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
            })

        """.trimIndent()
            )
        )

        val config = fakeSender.config
        assertThat(config.connectionTimeout, equalTo(KuaNumber(5000)))
    }

    @Test
    fun `default timeout is 5000`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            extensionFactories = listOf(ExtensionSmtpFactory)
        ).run(
            unitOfWork(
                """
            test_instance = require('net.smtp').create({
                timeout = nil,            
                host =  'host'
            })

            test_instance.send({
                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
            })

        """.trimIndent()
            )
        )

        val config = fakeSender.config
        assertThat(config.timeout, equalTo(KuaNumber(5000)))
    }

    @Test
    fun `default write_timeout is 3000`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            extensionFactories = listOf(ExtensionSmtpFactory)
        ).run(
            unitOfWork(
                """
            test_instance = require('net.smtp').create({
                write_timeout = nil,            
                host =  'host'
            })

            test_instance.send({
                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
            })

        """.trimIndent()
            )
        )

        val config = fakeSender.config
        assertThat(config.writeTimeout, equalTo(KuaNumber(3000)))
    }
}

internal class FakeSender : Sender {
    override fun send(config: SenderConfig, message: Message) {
        this.config = config
        this.message = message
    }

    lateinit var config: SenderConfig
    lateinit var message: Message
}
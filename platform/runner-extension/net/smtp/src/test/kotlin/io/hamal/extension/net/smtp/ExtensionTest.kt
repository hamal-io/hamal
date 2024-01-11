package io.hamal.extension.net.smtp

import AbstractRunnerTest
import TestFailConnector
import io.hamal.plugin.net.smtp.Message
import io.hamal.plugin.net.smtp.PluginSmtpFactory
import io.hamal.plugin.net.smtp.Sender
import io.hamal.plugin.net.smtp.SenderConfig
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
        assertThat(config.defaultEncoding, equalTo("default_encoding"))
        assertThat(config.host, equalTo("host"))
        assertThat(config.port, equalTo(123))
        assertThat(config.username, equalTo("username"))
        assertThat(config.password, equalTo("password"))
        assertThat(config.protocol, equalTo("protocol"))
        assertThat(config.debug, equalTo(true))
        assertThat(config.enableStarttls, equalTo(true))
        assertThat(config.testConnection, equalTo(true))

        assertThat(config.connectionTimeout, equalTo(1000))
        assertThat(config.timeout, equalTo(2000))
        assertThat(config.writeTimeout, equalTo(3000))

        val msg = fakeSender.message
        assertThat(msg.from, equalTo("from"))
        assertThat(msg.to, equalTo("to"))
        assertThat(msg.subject, equalTo("subject"))
        assertThat(msg.content, equalTo("content"))
        assertThat(msg.contentType, equalTo("content_type"))
        assertThat(msg.priority, equalTo(42))

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
        assertThat(config.defaultEncoding, equalTo("UTF-8"))
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
        assertThat(config.port, equalTo(25))
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
        assertThat(config.protocol, equalTo("smtp"))
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
        assertThat(config.debug, equalTo(false))
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
        assertThat(config.enableStarttls, equalTo(false))
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
        assertThat(config.testConnection, equalTo(false))
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
        assertThat(message.contentType, equalTo("text/plain"))
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
        assertThat(message.priority, equalTo(1))
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
        assertThat(config.connectionTimeout, equalTo(5000))
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
        assertThat(config.timeout, equalTo(5000))
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
        assertThat(config.writeTimeout, equalTo(3000))
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
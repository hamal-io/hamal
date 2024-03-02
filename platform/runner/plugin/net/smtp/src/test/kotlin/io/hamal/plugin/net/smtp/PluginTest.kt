package io.hamal.plugin.net.smtp

import io.hamal.lib.kua.type.KuaNumber
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTrue
import io.hamal.runner.test.AbstractRunnerTest
import io.hamal.runner.test.TestFailConnector
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test


internal object PluginSmtpTest : AbstractRunnerTest() {

    @Test
    fun `Invokes sender`() {
        val fakeSender = FakeSender()

        createTestRunner(pluginFactories = listOf(PluginSmtpFactory(fakeSender))).run(
            unitOfWork(
                """
            test_instance = require_plugin('net.smtp')
            
            test_instance.send({
                default_encoding = 'default_encoding',
                host =  'host',
                port =  123,
                username =  'username',
                password =  'password',
                protocol =  'protocol',
                debug =  true,
                enable_starttls =  true,
                test_connection =  true,

                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
                content_type =  'content_type',
                priority =  42,

                connection_timeout =   1000,
                timeout =  2000,
                write_timeout =  3000
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
        assertThat(config.debug, equalTo(KuaTrue))
        assertThat(config.enableStarttls, equalTo(KuaTrue))
        assertThat(config.testConnection, equalTo(KuaTrue))

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
    fun `default_encoding is required`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            connector = TestFailConnector { _, result ->
                assertThat(result.value.stringValue("message"), containsString("default_encoding not set"))
            }
        ).run(
            unitOfWork(
                """
            test_instance = require_plugin('net.smtp')
            
            test_instance.send({
                default_encoding = nil,
                host =  'host',
                port =  123,
                username =  'username',
                password =  'password',
                protocol =  'protocol',
                debug =  true,
                enable_starttls =  true,
                test_connection =  true,

                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
                content_type =  'content_type',
                priority =  42,

                connection_timeout =   1000,
                timeout =  2000,
                write_timeout =  3000
            })

        """.trimIndent()
            )
        )
    }

    @Test
    fun `host is required`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            connector = TestFailConnector { _, result ->
                assertThat(result.value.stringValue("message"), containsString("host not set"))
            }
        ).run(
            unitOfWork(
                """
            test_instance = require_plugin('net.smtp')
            
            test_instance.send({
               default_encoding = 'default_encoding',
                host =  nil,
                port =  123,
                username =  'username',
                password =  'password',
                protocol =  'protocol',
                debug =  true,
                enable_starttls =  true,
                test_connection =  true,

                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
                content_type =  'content_type',
                priority =  42,

                connection_timeout =   1000,
                timeout =  2000,
                write_timeout =  3000
            })

        """.trimIndent()
            )
        )
    }

    @Test
    fun `port is required`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            connector = TestFailConnector { _, result ->
                assertThat(result.value.stringValue("message"), containsString("port not set"))
            }
        ).run(
            unitOfWork(
                """
            test_instance = require_plugin('net.smtp')
            
            test_instance.send({
                default_encoding = 'default_encoding',
                host =  'host',
                port =  nil,
                username =  'username',
                password =  'password',
                protocol =  'protocol',
                debug =  true,
                enable_starttls =  true,
                test_connection =  true,

                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
                content_type =  'content_type',
                priority =  42,

                connection_timeout =   1000,
                timeout =  2000,
                write_timeout =  3000
            })

        """.trimIndent()
            )
        )
    }

    @Test
    fun `username is optional`() {
        val fakeSender = FakeSender()

        createTestRunner(pluginFactories = listOf(PluginSmtpFactory(fakeSender))).run(
            unitOfWork(
                """
            test_instance = require_plugin('net.smtp')
            
            test_instance.send({
               default_encoding = 'default_encoding',
                host =  'host',
                port =  123,
                username =  nil,
                password =  'password',
                protocol =  'protocol',
                debug =  true,
                enable_starttls =  true,
                test_connection =  true,

                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
                content_type =  'content_type',
                priority =  42,

                connection_timeout =   1000,
                timeout =  2000,
                write_timeout =  3000
            })

        """.trimIndent()
            )
        )
    }

    @Test
    fun `password is optional`() {
        val fakeSender = FakeSender()

        createTestRunner(pluginFactories = listOf(PluginSmtpFactory(fakeSender))).run(
            unitOfWork(
                """
            test_instance = require_plugin('net.smtp')
            
            test_instance.send({
                default_encoding = 'default_encoding',
                host =  'host',
                port =  123,
                username =  'username',
                password =  nil,
                protocol =  'protocol',
                debug =  true,
                enable_starttls =  true,
                test_connection =  true,

                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
                content_type =  'content_type',
                priority =  42,

                connection_timeout =   1000,
                timeout =  2000,
                write_timeout =  3000
            })

        """.trimIndent()
            )
        )
    }

    @Test
    fun `protocol is required`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            connector = TestFailConnector { _, result ->
                assertThat(result.value.stringValue("message"), containsString("protocol not set"))
            }
        ).run(
            unitOfWork(
                """
            test_instance = require_plugin('net.smtp')
            
            test_instance.send({
                default_encoding = 'default_encoding',
                host =  'host',
                port =  123,
                username =  'username',
                password =  'password',
                protocol =  nil,
                debug =  true,
                enable_starttls =  true,
                test_connection =  true,

                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
                content_type =  'content_type',
                priority =  42,

                connection_timeout =   1000,
                timeout =  2000,
                write_timeout =  3000
            })

        """.trimIndent()
            )
        )
    }

    @Test
    fun `debug is required`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            connector = TestFailConnector { _, result ->
                assertThat(result.value.stringValue("message"), containsString("debug not set"))
            }
        ).run(
            unitOfWork(
                """
            test_instance = require_plugin('net.smtp')
            
            test_instance.send({
                default_encoding = 'default_encoding',
                host =  'host',
                port =  123,
                username =  'username',
                password =  'password',
                protocol =  'protocol',
                debug =  nil,
                enable_starttls =  true,
                test_connection =  true,

                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
                content_type =  'content_type',
                priority =  42,

                connection_timeout =   1000,
                timeout =  2000,
                write_timeout =  3000
            })

        """.trimIndent()
            )
        )
    }

    @Test
    fun `enable_starttls is required`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            connector = TestFailConnector { _, result ->
                assertThat(result.value.stringValue("message"), containsString("enable_starttls not set"))
            }
        ).run(
            unitOfWork(
                """
            test_instance = require_plugin('net.smtp')
            
            test_instance.send({
                default_encoding = 'default_encoding',
                host =  'host',
                port =  123,
                username =  'username',
                password =  'password',
                protocol =  'protocol',
                debug =  true,
                enable_starttls =  nil,
                test_connection =  true,

                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
                content_type =  'content_type',
                priority =  42,

                connection_timeout =   1000,
                timeout =  2000,
                write_timeout =  3000
            })

        """.trimIndent()
            )
        )
    }

    @Test
    fun `test_connection is required`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            connector = TestFailConnector { _, result ->
                assertThat(result.value.stringValue("message"), containsString("test_connection not set"))
            }).run(
            unitOfWork(
                """
            test_instance = require_plugin('net.smtp')
            
            test_instance.send({
                default_encoding = 'default_encoding',
                host =  'host',
                port =  123,
                username =  'username',
                password =  'password',
                protocol =  'protocol',
                debug =  true,
                enable_starttls =  true,
                test_connection =  nil,

                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
                content_type =  'content_type',
                priority =  42,

                connection_timeout =   1000,
                timeout =  2000,
                write_timeout =  3000
            })

        """.trimIndent()
            )
        )
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
            test_instance = require_plugin('net.smtp')
            
            test_instance.send({
                default_encoding = 'default_encoding',
                host =  'host',
                port =  123,
                username =  'username',
                password =  'password',
                protocol =  'protocol',
                debug =  true,
                enable_starttls =  true,
                test_connection =  true,

                from =  nil,
                to =  'to',
                subject =  'subject',
                content =  'content',
                content_type =  'content_type',
                priority =  42,

                connection_timeout =   1000,
                timeout =  2000,
                write_timeout =  3000
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
            test_instance = require_plugin('net.smtp')
            
            test_instance.send({
                default_encoding = 'default_encoding',
                host =  'host',
                port =  123,
                username =  'username',
                password =  'password',
                protocol =  'protocol',
                debug =  true,
                enable_starttls =  true,
                test_connection =  true,

                from =  'from',
                to =  nil,
                subject =  'subject',
                content =  'content',
                content_type =  'content_type',
                priority =  42,

                connection_timeout =   1000,
                timeout =  2000,
                write_timeout =  3000
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
            test_instance = require_plugin('net.smtp')
            
            test_instance.send({
                default_encoding = 'default_encoding',
                host =  'host',
                port =  123,
                username =  'username',
                password =  'password',
                protocol =  'protocol',
                debug =  true,
                enable_starttls =  true,
                test_connection =  true,

                from =  'from',
                to =  'to',
                subject =  nil,
                content =  'content',
                content_type =  'content_type',
                priority =  42,

                connection_timeout =   1000,
                timeout =  2000,
                write_timeout =  3000
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
            test_instance = require_plugin('net.smtp')
            
            test_instance.send({
                default_encoding = 'default_encoding',
                host =  'host',
                port =  123,
                username =  'username',
                password =  'password',
                protocol =  'protocol',
                debug =  true,
                enable_starttls =  true,
                test_connection =  true,

                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  nil,
                content_type =  'content_type',
                priority =  42,

                connection_timeout =   1000,
                timeout =  2000,
                write_timeout =  3000
            })

        """.trimIndent()
            )
        )
    }

    @Test
    fun `content_type is required`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            connector = TestFailConnector { _, result ->
                assertThat(result.value.stringValue("message"), containsString("content_type not set"))
            }
        ).run(
            unitOfWork(
                """
            test_instance = require_plugin('net.smtp')
            
            test_instance.send({
                default_encoding = 'default_encoding',
                host =  'host',
                port =  123,
                username =  'username',
                password =  'password',
                protocol =  'protocol',
                debug =  true,
                enable_starttls =  true,
                test_connection =  true,

                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
                content_type =  nil,
                priority =  42,

                connection_timeout =   1000,
                timeout =  2000,
                write_timeout =  3000
            })

        """.trimIndent()
            )
        )
    }

    @Test
    fun `priority is required`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            connector = TestFailConnector { _, result ->
                assertThat(result.value.stringValue("message"), containsString("priority not set"))
            }
        ).run(
            unitOfWork(
                """
            test_instance = require_plugin('net.smtp')
            
            test_instance.send({
                default_encoding = 'default_encoding',
                host =  'host',
                port =  123,
                username =  'username',
                password =  'password',
                protocol =  'protocol',
                debug =  true,
                enable_starttls =  true,
                test_connection =  true,

                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
                content_type =  'content_type',
                priority =  nil,

                connection_timeout =   1000,
                timeout =  2000,
                write_timeout =  3000
            })

        """.trimIndent()
            )
        )
    }

    @Test
    fun `connection_timeout is required`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            connector = TestFailConnector { _, result ->
                assertThat(result.value.stringValue("message"), containsString("connection_timeout not set"))
            }
        ).run(
            unitOfWork(
                """
            test_instance = require_plugin('net.smtp')
            
            test_instance.send({
                default_encoding = 'default_encoding',
                host =  'host',
                port =  123,
                username =  'username',
                password =  'password',
                protocol =  'protocol',
                debug =  true,
                enable_starttls =  true,
                test_connection =  true,

                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
                content_type =  'content_type',
                priority =  42,

                connection_timeout =   nil,
                timeout =  2000,
                write_timeout =  3000
            })

        """.trimIndent()
            )
        )
    }

    @Test
    fun `timeout is required`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            connector = TestFailConnector { _, result ->
                assertThat(result.value.stringValue("message"), containsString("timeout not set"))
            }
        ).run(
            unitOfWork(
                """
            test_instance = require_plugin('net.smtp')
            
            test_instance.send({
                default_encoding = 'default_encoding',
                host =  'host',
                port =  123,
                username =  'username',
                password =  'password',
                protocol =  'protocol',
                debug =  true,
                enable_starttls =  true,
                test_connection =  true,

                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
                content_type =  'content_type',
                priority =  42,

                connection_timeout =   1000,
                timeout =  nil,
                write_timeout =  3000
            })

        """.trimIndent()
            )
        )
    }

    @Test
    fun `write_timeout is required`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            connector = TestFailConnector { _, result ->
                assertThat(result.value.stringValue("message"), containsString("write_timeout not set"))
            }
        ).run(
            unitOfWork(
                """
            test_instance = require_plugin('net.smtp')
            
            test_instance.send({
                default_encoding = 'default_encoding',
                host =  'host',
                port =  123,
                username =  'username',
                password =  'password',
                protocol =  'protocol',
                debug =  true,
                enable_starttls =  true,
                test_connection =  true,

                from =  'from',
                to =  'to',
                subject =  'subject',
                content =  'content',
                content_type =  'content_type',
                priority =  42,

                connection_timeout =   1000,
                timeout =  2000,
                write_timeout =  nil
            })

        """.trimIndent()
            )
        )
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
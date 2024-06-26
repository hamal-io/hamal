package io.hamal.plugin.net.smtp

import io.hamal.lib.common.value.ValueNumber
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.common.value.ValueTrue
import io.hamal.lib.domain.vo.ExecStatusCode.Companion.ExecStatusCode
import io.hamal.runner.test.RunnerFixture.createTestRunner
import io.hamal.runner.test.RunnerFixture.unitOfWork
import io.hamal.runner.test.TestFailConnector
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test


internal object PluginSmtpTest {

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
        assertThat(config.defaultEncoding, equalTo(ValueString("default_encoding")))
        assertThat(config.host, equalTo(ValueString("host")))
        assertThat(config.port, equalTo(ValueNumber(123)))
        assertThat(config.username, equalTo(ValueString("username")))
        assertThat(config.password, equalTo(ValueString("password")))
        assertThat(config.protocol, equalTo(ValueString("protocol")))
        assertThat(config.debug, equalTo(ValueTrue))
        assertThat(config.enableStarttls, equalTo(ValueTrue))
        assertThat(config.testConnection, equalTo(ValueTrue))

        assertThat(config.connectionTimeout, equalTo(ValueNumber(1000)))
        assertThat(config.timeout, equalTo(ValueNumber(2000)))
        assertThat(config.writeTimeout, equalTo(ValueNumber(3000)))

        val msg = fakeSender.message
        assertThat(msg.from, equalTo(ValueString("from")))
        assertThat(msg.to, equalTo(ValueString("to")))
        assertThat(msg.subject, equalTo(ValueString("subject")))
        assertThat(msg.content, equalTo(ValueString("content")))
        assertThat(msg.contentType, equalTo(ValueString("content_type")))
        assertThat(msg.priority, equalTo(ValueNumber(42)))

    }

    @Test
    fun `default_encoding is required`() {
        val fakeSender = FakeSender()

        createTestRunner(
            pluginFactories = listOf(PluginSmtpFactory(fakeSender)),
            connector = TestFailConnector { _, statusCode, result ->
                assertThat(statusCode, equalTo(ExecStatusCode(400)))
                assertThat(result.getString("message").stringValue, containsString("default_encoding not set"))
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
            connector = TestFailConnector { _, statusCode, result ->
                assertThat(statusCode, equalTo(ExecStatusCode(400)))
                assertThat(result.getString("message").stringValue, containsString("host not set"))
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
            connector = TestFailConnector { _, statusCode, result ->
                assertThat(statusCode, equalTo(ExecStatusCode(400)))
                assertThat(result.getString("message").stringValue, containsString("port not set"))
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
            connector = TestFailConnector { _, statusCode, result ->
                assertThat(statusCode, equalTo(ExecStatusCode(400)))
                assertThat(result.getString("message").stringValue, containsString("protocol not set"))
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
            connector = TestFailConnector { _, statusCode, result ->
                assertThat(statusCode, equalTo(ExecStatusCode(400)))
                assertThat(result.getString("message").stringValue, containsString("debug not set"))
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
            connector = TestFailConnector { _, statusCode, result ->
                assertThat(statusCode, equalTo(ExecStatusCode(400)))
                assertThat(result.getString("message").stringValue, containsString("enable_starttls not set"))
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
            connector = TestFailConnector { _, statusCode, result ->
                assertThat(statusCode, equalTo(ExecStatusCode(400)))
                assertThat(result.getString("message").stringValue, containsString("test_connection not set"))
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
            connector = TestFailConnector { _, statusCode, result ->
                assertThat(statusCode, equalTo(ExecStatusCode(400)))
                assertThat(result.getString("message").stringValue, containsString("from not set"))
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
            connector = TestFailConnector { _, statusCode, result ->
                assertThat(statusCode, equalTo(ExecStatusCode(400)))
                assertThat(result.getString("message").stringValue, containsString("to not set"))
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
            connector = TestFailConnector { _, statusCode, result ->
                assertThat(statusCode, equalTo(ExecStatusCode(400)))
                assertThat(result.getString("message").stringValue, containsString("subject not set"))
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
            connector = TestFailConnector { _, statusCode, result ->
                assertThat(statusCode, equalTo(ExecStatusCode(400)))
                assertThat(result.getString("message").stringValue, containsString("content not set"))
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
            connector = TestFailConnector { _, statusCode, result ->
                assertThat(statusCode, equalTo(ExecStatusCode(400)))
                assertThat(result.getString("message").stringValue, containsString("content_type not set"))
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
            connector = TestFailConnector { _, statusCode, result ->
                assertThat(statusCode, equalTo(ExecStatusCode(400)))
                assertThat(result.getString("message").stringValue, containsString("priority not set"))
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
            connector = TestFailConnector { _, statusCode, result ->
                assertThat(statusCode, equalTo(ExecStatusCode(400)))
                assertThat(result.getString("message").stringValue, containsString("connection_timeout not set"))
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
            connector = TestFailConnector { _, statusCode, result ->
                assertThat(statusCode, equalTo(ExecStatusCode(400)))
                assertThat(result.getString("message").stringValue, containsString("timeout not set"))
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
            connector = TestFailConnector { _, statusCode, result ->
                assertThat(statusCode, equalTo(ExecStatusCode(400)))
                assertThat(result.getString("message").stringValue, containsString("write_timeout not set"))
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
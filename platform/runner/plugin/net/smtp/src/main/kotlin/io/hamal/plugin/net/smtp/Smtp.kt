package io.hamal.plugin.net.smtp

import io.hamal.lib.kua.function.Function1In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaTableMap

class SmtpSendFunction(
    private val sender: Sender
) : Function1In1Out<KuaTableMap, KuaError>(
    FunctionInput1Schema(KuaTableMap::class),
    FunctionOutput1Schema(KuaError::class)
) {

    override fun invoke(ctx: FunctionContext, arg1: KuaTableMap): KuaError? {

        sender.send(
            SenderConfig(
                host = arg1.getString("host"),
                port = arg1.getInt("port"),
                username = arg1.findString("username"),
                password = arg1.findString("password"),
                defaultEncoding = arg1.getString("default_encoding"),
                protocol = arg1.getString("protocol"),
                debug = arg1.getBoolean("debug"),
                testConnection = arg1.getBoolean("test_connection"),
                auth = arg1.findString("username") != null || arg1.findString("password") != null,
                enableStarttls = arg1.getBoolean("enable_starttls"),
                connectionTimeout = arg1.getLong("connection_timeout"),
                timeout = arg1.getLong("timeout"),
                writeTimeout = arg1.getLong("write_timeout")
            ),
            Message(
                from = arg1.getString("from"),
                to = arg1.getString("to"),
                subject = arg1.getString("subject"),
                content = arg1.getString("content"),
                contentType = arg1.getString("content_type"),
                priority = arg1.getInt("priority")
            )
        )

        return null
    }

}
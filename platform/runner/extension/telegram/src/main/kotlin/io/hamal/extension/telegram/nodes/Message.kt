package io.hamal.extension.telegram.nodes

import io.hamal.lib.nodes.*
import io.hamal.lib.nodes.control.ControlExtensionInputString
import io.hamal.lib.nodes.generator.Generator
import io.hamal.lib.typesystem.TypeNew
import io.hamal.lib.typesystem.TypeString
import io.hamal.lib.typesystem.value.ValueString

val SendMessageNode = NodeExtension(
    type = NodeType("TELEGRAM_SEND_MESSAGE"),
    title = NodeTitle("Telegram - Send Message"),
    size = Size(200, 200),
    controls = listOf(
        ControlExtensionInputString(
            port = PortInputExtension(TypeString),
            defaultValue = ValueString("Hello from hamal - nodes")
        )
    ),
    outputs = listOf()
)

val SendMessageNodeGenerator = object : Generator {
    override val type: NodeType get() = NodeType("TELEGRAM_SEND_MESSAGE")
    override val inputTypes: List<TypeNew> get() = listOf()
    override val outputTypes: List<TypeNew> get() = listOf()

    override fun toCode(node: Node): String {
        return """
tg = require('telegram').create({
    bot_token = '7084966112:AAElNk5M1t0hTXTQPruNszHVD0SB0OCJjKY'
})

for k,v in pairs(tg) do print(k,v) end


print(tg)

err, result = tg.send_message({
    chat_id = '-4171903484',
    text = 'This is a test message'
})

print(err)
print(result)

for k,v in pairs(result) do print(k,v) end
        """.trimIndent()
    }

}
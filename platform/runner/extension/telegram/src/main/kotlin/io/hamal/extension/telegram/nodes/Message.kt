package io.hamal.extension.telegram.nodes

import io.hamal.lib.common.value.TypeString
import io.hamal.lib.common.value.ValueType
import io.hamal.lib.nodes.*
import io.hamal.lib.nodes.NodeTitle.Companion.NodeTitle
import io.hamal.lib.nodes.NodeType.Companion.NodeType
import io.hamal.lib.nodes.compiler.node.NodeCompiler

val SendMessageNode = TemplateNode(
    type = NodeType("Telegram_Send_Message"),
    title = NodeTitle("Telegram - Send Message"),
    size = Size(200, 200),
    controls = listOf(
//        ControlExtensionTextArea(
//            port = TemplatePortInput(TypeString),
//            defaultValue = ValueString("Hello from hamal - nodes")
//        )
    ),
    outputs = listOf()
)

val SendMessageNodeCompiler = object : NodeCompiler {
    override val type: NodeType get() = NodeType("Telegram_Send_Message")
    override val inputTypes: List<ValueType> get() = listOf(TypeString, TypeString)
    override val outputTypes: List<ValueType> get() = listOf()

    override fun toCode(node: Node, controls: List<Control>): String {
        return """
           -- print('Sending message ' .. arg_2 .. ' to chat ' .. arg_1)
           
           tg = require('telegram').create({
               bot_token = '7084966112:AAElNk5M1t0hTXTQPruNszHVD0SB0OCJjKY'
           })

           for k,v in pairs(tg) do print(k,v) end


           print(tg)

           err, result = tg.send_message({
               chat_id = arg_1,
               text = arg_2
           })

           print(err)
           print(result)

           for k,v in pairs(result) do print(k,v) end
           
        """.trimIndent()
    }

}

/*

 */
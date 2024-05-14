package io.hamal.extension.social.telegram.nodes

import io.hamal.lib.common.value.TypeString
import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.common.value.ValueType
import io.hamal.lib.nodes.NodeTitle.Companion.NodeTitle
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.nodes.NodeType.Companion.NodeType
import io.hamal.lib.nodes.Size
import io.hamal.lib.nodes.TemplateNode
import io.hamal.lib.nodes.compiler.node.NodeCompiler
import io.hamal.lib.nodes.compiler.node.NodeCompiler.Context

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

internal object SendMessage : NodeCompiler() {
    override val type: NodeType get() = NodeType("Telegram_Send_Message")
    override val inputTypes: List<ValueType> get() = listOf(TypeString, TypeString)
    override val outputTypes: List<ValueType> get() = listOf()

    override fun toCode(ctx: Context): ValueCode {
        return ValueCode(
            """
           print('Sending message ' .. arg_2 .. ' to chat ' .. arg_1)
           
           tg = require('social.telegram').create({
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
        )
    }

}

/*

 */
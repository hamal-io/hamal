//package io.hamal.extension.social.telegram.nodes
//
//import io.hamal.lib.common.value.TypeUnit
//import io.hamal.lib.common.value.ValueCode
//import io.hamal.lib.common.value.ValueType
//import io.hamal.lib.nodes.*
//import io.hamal.lib.nodes.ControlKey.Companion.ControlKey
//import io.hamal.lib.nodes.NodeTitle.Companion.NodeTitle
//import io.hamal.lib.nodes.NodeType.Companion.NodeType
//import io.hamal.lib.nodes.compiler.node.AbstractNode
//
//val SendMessageNode = TemplateNode(
//    type = NodeType("Telegram_Send_Message"),
//    title = NodeTitle("Telegram - Send Message"),
//    size = Size(200, 200),
//    controls = listOf(
////        ControlExtensionTextArea(
////            port = TemplatePortInput(TypeString),
////            defaultValue = ValueString("Hello from hamal - nodes")
////        )
//    ),
//    outputs = listOf()
//)
//
//internal object SendMessage : AbstractNode() {
//    override val type: NodeType get() = NodeType("Telegram_Send_Message")
//    override val inputTypes: List<ValueType> get() = listOf(TypeUnit)
//    override val outputTypes: List<ValueType> get() = listOf()
//
//    override fun toCode(ctx: Context): ValueCode {
//        val chatId = ctx.controls
//            .filterIsInstance<ControlInputString>()
//            .find { it.key == ControlKey("chat_id") }!!.value
//
//        val message = ctx.controls
//            .filterIsInstance<ControlInputString>()
//            .find { it.key == ControlKey("message") }!!.value
//
//
//        return ValueCode(
//            """
//           -- chat_id =  controls['chat_id'].value or default_value or error
//           -- args['xyz'].value()
//
//
//
//           chat_id =  controls['chat_id'].value()
//           message =  controls['message'].value() -- retrieves value - either from input or from connected node
//
//           print(chat_id)
//           print(message)
//
//           tg = require('social.telegram').create({
//               bot_token = '7084966112:AAElNk5M1t0hTXTQPruNszHVD0SB0OCJjKY'
//           })
//
//           for k,v in pairs(tg) do print(k,v) end
//
//
//           print(tg)
//
//           err, result = tg.send_message({
//               chat_id = chat_id,
//               text = message
//           })
//
//           print(err)
//           print(result)
//
//           for k,v in pairs(result) do print(k,v) end
//
//        """.trimIndent()
//        )
//    }
//
//}
//
///*
//
// */
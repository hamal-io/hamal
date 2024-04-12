package io.hamal.extension.telegram.nodes

import io.hamal.lib.nodes.*
import io.hamal.lib.nodes.control.ControlExtensionInputString
import io.hamal.lib.typesystem.TypeString
import io.hamal.lib.typesystem.value.ValueString

val SendMessageNode = NodeExtension(
    type = NodeType("TELEGRAM_SEND_MESSAGE"),
    title = NodeTitle("Telegram - Send Message"),
    size = Size(200, 200),
    controls = listOf(
        ControlExtensionInputString(
            port = PortInputExtension(TypeString),
            defaultValue = ValueString("Hello from hamal")
        )
    ),
    outputs = listOf()
)
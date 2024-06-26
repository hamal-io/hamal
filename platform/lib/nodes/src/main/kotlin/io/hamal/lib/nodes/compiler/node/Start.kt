package io.hamal.lib.nodes.compiler.node

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.nodes.ControlInputBoolean
import io.hamal.lib.nodes.ControlInputString
import io.hamal.lib.nodes.NodeType.Companion.NodeType
import io.hamal.lib.nodes.NodeVersion

sealed class Start : AbstractNode() {
    override val type = NodeType("Start")

    data object V_0_0_1 : Start() {
        override val version = NodeVersion.v_0_0_1

        override fun toCode(ctx: Context): ValueCode {
            // FIXME only one control
            val control = ctx.controlsOfNode(ctx.node.index).first()

            return when (control) {
                is ControlInputBoolean -> ValueCode(
                    """
                    |return{
                    |['${ctx.node.outputs.first().key}'] = ${control.value.booleanValue}
                    |}
                    """.trimMargin()
                )

                is ControlInputString -> ValueCode(
                    """
                    |return { 
                    |['${ctx.node.outputs.first().key}'] = '${control.value.stringValue}'
                    |}
                    """.trimMargin()
                )

                else -> TODO()
            }
        }
    }
}
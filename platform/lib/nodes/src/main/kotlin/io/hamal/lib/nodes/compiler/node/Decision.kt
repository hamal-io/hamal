package io.hamal.lib.nodes.compiler.node

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.common.value.ValueTrue
import io.hamal.lib.nodes.ControlInputBoolean
import io.hamal.lib.nodes.NodeType.Companion.NodeType
import io.hamal.lib.nodes.NodeVersion

sealed class Decision : AbstractNode() {
    override val type = NodeType("Decision")

    data object V_0_0_1 : Decision() {
        override val version = NodeVersion.v_0_0_1

        override fun toCode(ctx: Context): ValueCode {
            val control = ctx.controlsOfNode(ctx.node.index).filterIsInstance<ControlInputBoolean>().first()
            val expectedValue = control.value

            val connection = ctx.getConnection(control.port!!.index)

            return ValueCode(
                """
                |fn = __F__[${connection.outputNode.index}]
                |value = fn()['value']
                |print(value)
                |if value == ${if (expectedValue == ValueTrue) "true" else "false"} then
                |prune_node(4)
                |print('happy')
                |return{
                |   ['${ctx.node.outputs.first().key}'] = true
                |}
                |else
                |prune_node(4)
                |print('sad')
                |return{
                |   ['${ctx.node.outputs.last().key}'] = true
                |}
                |end
                """.trimMargin()
            )
        }
    }
}
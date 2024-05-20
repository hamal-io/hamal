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
                |__F__[4] = nil
                |print('happy')
                |print('prune node 4')
                |return{
                |   ['${ctx.node.outputs.first().key}'] = true
                |}
                |else
                |__F__[3] = nil
                |print('sad')
                |print('prune node 3')
                |return{
                |   ['${ctx.node.outputs.last().key}'] = true
                |}
                |end
                """.trimMargin()
            )
        }
    }
}

//
//import io.hamal.lib.common.value.TypeBoolean
//import io.hamal.lib.common.value.ValueCode
//import io.hamal.lib.common.value.ValueTrue
//import io.hamal.lib.common.value.ValueType
//import io.hamal.lib.nodes.ControlInputBoolean
//import io.hamal.lib.nodes.NodeType
//import io.hamal.lib.nodes.NodeType.Companion.NodeType
//
//sealed class Decision : AbstractNode() {
//    override val type: NodeType get() = NodeType("Decision")
//
//    data object Boolean : Decision() {
//        override val inputTypes: List<ValueType> get() = listOf(TypeBoolean)
//        override val outputTypes: List<ValueType> get() = listOf(TypeBoolean, TypeBoolean)
//
//        override fun toCode(ctx: Context): ValueCode {
//            val checkbox = ctx.controls.filterIsInstance<ControlInputBoolean>().first()
//            val expectedValue = checkbox.value
//            return ValueCode(
//                """
//                if arg_1 == ${if (expectedValue == ValueTrue) "true" else "false"} then
//                    return true, nil
//                else
//                    return nil, false
//                end
//            """.trimIndent()
//            )
//        }
//    }
//}
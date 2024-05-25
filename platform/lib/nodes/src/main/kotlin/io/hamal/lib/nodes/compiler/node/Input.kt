package io.hamal.lib.nodes.compiler.node

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.nodes.ControlInputBoolean
import io.hamal.lib.nodes.ControlInputString
import io.hamal.lib.nodes.NodeType.Companion.NodeType
import io.hamal.lib.nodes.NodeVersion

sealed class Input : AbstractNode() {
    override val type = NodeType("Input")

    data object V_0_0_1 : Input() {
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

//import io.hamal.lib.common.value.TypeBoolean
//import io.hamal.lib.common.value.TypeString
//import io.hamal.lib.common.value.ValueCode
//import io.hamal.lib.common.value.ValueType
//import io.hamal.lib.nodes.ControlInputBoolean
//import io.hamal.lib.nodes.ControlInputString
//import io.hamal.lib.nodes.NodeType
//import io.hamal.lib.nodes.NodeType.Companion.NodeType
//
//sealed class Input : AbstractNode() {
//    override val type: NodeType get() = NodeType("Input")
//
//    data object Boolean : Input() {
//        override val inputTypes: List<ValueType> get() = listOf()
//        override val outputTypes: List<ValueType> get() = listOf(TypeBoolean)
//
//        override fun toCode(ctx: AbstractNode.Context): ValueCode {
//            val control = ctx.controls.filterIsInstance<ControlInputBoolean>().firstOrNull()
//                ?: throw IllegalStateException("Boolean input has no ControlInputBoolean")
//
//            return ValueCode(
//                """
//                return ${control.value.booleanValue}
//            """.trimIndent()
//            )
//        }
//    }
//
//    data object String : Input() {
//        override val inputTypes: List<ValueType> get() = listOf()
//        override val outputTypes: List<ValueType> get() = listOf(TypeString)
//
//        override fun toCode(ctx: AbstractNode.Context): ValueCode {
//            val control = ctx.controls.filterIsInstance<ControlInputString>().firstOrNull()
//                ?: throw IllegalStateException("String input has no ControlInputString")
//
//            return ValueCode(
//                """
//                return '${control.value.stringValue}'
//            """.trimIndent()
//            )
//        }
//    }
//}
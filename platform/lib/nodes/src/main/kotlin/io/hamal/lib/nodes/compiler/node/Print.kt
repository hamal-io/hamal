package io.hamal.lib.nodes.compiler.node

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.nodes.ControlWithPort
import io.hamal.lib.nodes.NodeType.Companion.NodeType
import io.hamal.lib.nodes.NodeVersion

sealed class Print : AbstractNode() {
    override val type = NodeType("Print")

    data object V_0_0_1 : Print() {
        override val version = NodeVersion.v_0_0_1

        override fun toCode(ctx: Context): ValueCode {
            // FIXME make sure only one control present
            val control = ctx.controlsOfNode(ctx.node.index).first() as ControlWithPort

            val outputNode = ctx.nodeOfPort(control.port!!.index)

            val connection = ctx.getConnection(control.port!!.index)

            return ValueCode(
                """
                |fn = _F[${connection.outputNode.index}]
                |value = fn()
                |print(value['value'])
                |return { }
                """.trimMargin()
            )
        }
    }
}

//sealed class Print : AbstractNode() {
//    override val type: NodeType get() = NodeType("Print")
//
//    data object Boolean : Print() {
//        override val inputTypes: List<ValueType> get() = listOf(TypeBoolean)
//        override val outputTypes: List<ValueType> get() = listOf()
//
//
//        override fun toCode(ctx: Context): ValueCode {
//            return ValueCode(
//                """
//            print(arg_1)
//            return
//        """.trimIndent()
//            )
//        }
//
//    }
//
//    data object Number : Print() {
//        override val inputTypes: List<ValueType> get() = listOf(TypeNumber)
//        override val outputTypes: List<ValueType> get() = listOf()
//
//
//        override fun toCode(ctx: Context): ValueCode {
//            return ValueCode(
//                """
//            print(arg_1)
//            return
//        """.trimIndent()
//            )
//        }
//
//    }
//
//    data object Object : Print() {
//        override val inputTypes: List<ValueType> get() = listOf(TypeObject)
//        override val outputTypes: List<ValueType> get() = listOf()
//
//        override fun toCode(ctx: Context): ValueCode {
//            return ValueCode(
//                """
//            print(dump(arg_1))
//            return
//        """.trimIndent()
//            )
//        }
//
//    }
//
//    data object String : Print() {
//        override val inputTypes: List<ValueType> get() = listOf(TypeString)
//        override val outputTypes: List<ValueType> get() = listOf()
//
//        override fun toCode(ctx: Context): ValueCode {
//            return ValueCode(
//                """
//            -- print(args[0].value())
//            print(arg_1)
//            return
//        """.trimIndent()
//            )
//        }
//
//    }
//}

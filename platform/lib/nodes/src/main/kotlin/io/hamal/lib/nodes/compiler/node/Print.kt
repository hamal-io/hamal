package io.hamal.lib.nodes.compiler.node

import io.hamal.lib.common.value.*
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.nodes.NodeType.Companion.NodeType
import io.hamal.lib.nodes.compiler.node.NodeCompiler.Context

sealed interface Print : NodeCompiler {
    override val type: NodeType get() = NodeType("Print")

    data object Number : Print {
        override val inputTypes: List<ValueType> get() = listOf(TypeNumber)
        override val outputTypes: List<ValueType> get() = listOf()


        override fun toCode(ctx: Context): ValueCode {
            return ValueCode(
                """
            print(arg_1)
            return
        """.trimIndent()
            )
        }

    }

    data object Object : Print {
        override val inputTypes: List<ValueType> get() = listOf(TypeObject)
        override val outputTypes: List<ValueType> get() = listOf()

        override fun toCode(ctx: Context): ValueCode {
            return ValueCode(
                """
            print(dump(arg_1))
            return
        """.trimIndent()
            )
        }

    }

    data object String : Print {
        override val inputTypes: List<ValueType> get() = listOf(TypeString)
        override val outputTypes: List<ValueType> get() = listOf()

        override fun toCode(ctx: Context): ValueCode {
            return ValueCode(
                """
            print(arg_1)
            return
        """.trimIndent()
            )
        }

    }
}
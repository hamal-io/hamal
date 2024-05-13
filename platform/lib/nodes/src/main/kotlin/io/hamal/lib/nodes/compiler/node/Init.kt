package io.hamal.lib.nodes.compiler.node

import io.hamal.lib.common.value.*
import io.hamal.lib.nodes.ControlInit
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.nodes.NodeType.Companion.NodeType
import io.hamal.lib.nodes.compiler.node.NodeCompiler.Context


sealed interface Init : NodeCompiler {

    override val type: NodeType get() = NodeType("Init")
    override val inputTypes: List<ValueType> get() = listOf()

    data object Boolean : Init {
        override val outputTypes: List<ValueType> get() = listOf(TypeBoolean)

        override fun toCode(ctx: Context): ValueCode {
            val selector = ctx.controls.filterIsInstance<ControlInit>().firstOrNull()?.config?.findString("selector")?.stringValue ?: "__nodes__init__"
            if (selector == "No_Value") {
                return ValueCode("return nil")
            }
            return ValueCode(
                """
                initial_value = context.exec.inputs.${selector}
                if initial_value == nil then
                    throw.illegal_argument('No initial value was found')
                end
                return initial_value 
            """.trimIndent()
            )
        }
    }

    data object Number : Init {
        override val outputTypes: List<ValueType> get() = listOf(TypeNumber)
        override fun toCode(ctx: Context): ValueCode {
            val selector = ctx.controls.filterIsInstance<ControlInit>().firstOrNull()?.config?.findString("selector")?.stringValue?: "__nodes__init__"
            if (selector == "No_Value") {
                return ValueCode("return nil")
            }
            return ValueCode(
                """
                initial_value = context.exec.inputs.${selector}
                if initial_value == nil then
                    throw.illegal_argument('No initial value was found')
                end
                return initial_value 
            """.trimIndent()
            )
        }
    }

    data object Object : Init {
        override val outputTypes: List<ValueType> get() = listOf(TypeObject)
        override fun toCode(ctx: Context): ValueCode {
            val selector = ctx.controls.filterIsInstance<ControlInit>().firstOrNull()?.config?.findString("selector")?.stringValue ?: "__nodes__init__"
            if (selector == "No_Value") {
                return ValueCode("return nil")
            }
            return ValueCode(
                """
                initial_value = context.exec.inputs.${selector}
                if initial_value == nil then
                    throw.illegal_argument('No initial value was found')
                end
                return initial_value 
            """.trimIndent()
            )
        }
    }


    data object String : Init {
        override val outputTypes: List<ValueType> get() = listOf(TypeString)
        override fun toCode(ctx: Context): ValueCode {
            val selector = ctx.controls.filterIsInstance<ControlInit>().firstOrNull()?.config?.findString("selector")?.stringValue ?: "__nodes__init__"
            if (selector == "No_Value") {
                return ValueCode("return nil")
            }
            return ValueCode(
                """
                initial_value = context.exec.inputs.${selector}
                if initial_value == nil then
                    throw.illegal_argument('No initial value was found')
                end
                return initial_value 
            """.trimIndent()
            )
        }
    }


}

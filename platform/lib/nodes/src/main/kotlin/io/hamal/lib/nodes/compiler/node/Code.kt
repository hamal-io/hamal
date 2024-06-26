//package io.hamal.lib.nodes.compiler.node
//
//import io.hamal.lib.common.value.TypeObject
//import io.hamal.lib.common.value.TypeString
//import io.hamal.lib.common.value.ValueCode
//import io.hamal.lib.common.value.ValueType
//import io.hamal.lib.nodes.ControlCode
//import io.hamal.lib.nodes.NodeType
//import io.hamal.lib.nodes.NodeType.Companion.NodeType
//
//abstract class Code : AbstractNode() {
//    override val type: NodeType get() = NodeType("Code")
//    override val outputTypes: List<ValueType> get() = listOf()
//
//    data object Object : Code() {
//        override val inputTypes: List<ValueType> = listOf(TypeObject)
//
//        override fun toCode(ctx: Context): ValueCode {
//            val code = ctx.controls.filterIsInstance<ControlCode>().first().value
//            return code
//        }
//    }
//
//    data object String : Code() {
//        override val inputTypes: List<ValueType> = listOf(TypeString)
//
//        override fun toCode(ctx: Context): ValueCode {
//            val code = ctx.controls.filterIsInstance<ControlCode>().first().value
//            return code
//        }
//    }
//
//}
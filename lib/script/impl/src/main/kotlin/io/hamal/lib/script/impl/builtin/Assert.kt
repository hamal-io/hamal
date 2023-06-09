package io.hamal.lib.script.impl.builtin

import io.hamal.lib.common.value.FuncValue
import io.hamal.lib.common.value.MetaTable

object AssertFunction : FuncValue {
    //    override val identifier: IdentValue = IdentValue("assert")
//    override val metaTable = DepMetaTableNotImplementedYet
//
//
//    override fun invoke(ctx: Context): Value {
//        val parameters = ctx.parameters
//
//        val assertionMessage = parameters.getOrNull(1)
//            ?.value
//            ?.let { it as StringValue }
//            ?: StringValue("${parameters.first().expression}")
//
//        val result = parameters.firstOrNull()?.value
//        if (result != TrueValue) {
//            if (result != FalseValue) {
//                throw ScriptEvaluationException(ErrorValue("Assertion of non boolean value is always false"))
//            }
//            throw ScriptEvaluationException(ErrorValue("Assertion violated: $assertionMessage"))
//        }
//        return NilValue
//    }
    override val metaTable: MetaTable
        get() = TODO("Not yet implemented")
}
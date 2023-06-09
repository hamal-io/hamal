package io.hamal.lib.script.impl.builtin

import io.hamal.lib.common.value.FuncValue
import io.hamal.lib.common.value.MetaTable

object RequireFunction : FuncValue {
    //    override val identifier: IdentValue = IdentValue("require")
//    override val metaTable = DepMetaTableNotImplementedYet
//
//
//    override fun invoke(ctx: Context): Value {
//        val firstParameter = ctx.parameters.firstOrNull()
//            ?: throw ScriptEvaluationException(ErrorValue("require needs one environment identifier"))
//
//        val identifier = firstParameter.asIdentifier()
//
//        var result: EnvValue = ctx.env
//        val splits = identifier.value.split("/")
//        splits.forEach { envIdent ->
//            result = result.findEnvironmentValue(IdentValue(envIdent))
//                ?: throw ScriptEvaluationException(ErrorValue("Environment '${identifier.value}' not found"))
//        }
//
//
//        return result
//
//    }
    override val metaTable: MetaTable
        get() = TODO("Not yet implemented")
}
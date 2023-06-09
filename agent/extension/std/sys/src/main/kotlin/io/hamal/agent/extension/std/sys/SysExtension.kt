package io.hamal.agent.extension.std.sys

import io.hamal.agent.extension.api.Extension
import io.hamal.lib.script.api.value.EnvValue
import io.hamal.lib.script.api.value.IdentValue

class HamalExtension : Extension {
    override fun create(): EnvValue {
        return EnvValue(
            ident = IdentValue("sys"),
            values = mapOf(
//                IdentValue("_cfg") to TableValue(),
//                IdentValue("exec") to ExecFunc(),
//                IdentValue("create_func") to CreateFunc(),
            )
        )
    }

}

//class ExecFunc : DepFunctionValue {
//    override val identifier = IdentValue("exec")
//    override val metaTable: DepMetaTable get() = TODO("Not yet implemented")
//
//    override fun invoke(ctx: Context): Value {
//        val funcId = (ctx.parameters.first().value as StringValue).toString().replace("'", "")
//        println("DEBUG: ${funcId}")
//
//        HttpTemplate("http://localhost:8084")
//            .post("/v1/funcs/${funcId}/exec")
//            .body(
//                """
//                {
//                }
//            """.trimIndent()
//            )
//            .execute()
//
//
//        return NilValue
//    }
//
//}
//
//class CreateFunc : DepFunctionValue {
//    override val identifier = IdentValue("create_func")
//    override val metaTable: DepMetaTable get() = TODO("Not yet implemented")
//
//    override fun invoke(ctx: Context): Value {
////        val funcId = (ctx.parameters.first().value as StringValue).toString().replace("'", "")
////        println("DEBUG: ${funcId}")
////
////        HttpTemplate("http://localhost:8084")
////            .post("/v1/funcs/${funcId}/exec")
////            .body(
////                """
////                {
////                }
////            """.trimIndent()
////            )
////            .execute()
//
//        try {
//            println("CREATE_FUNC")
//
//            val f = ctx.parameters.first().value as TableValue
//            println(f)
//
//            val r = ApiCreateFuncRequest(
//                name = FuncName((f[IdentValue("name")] as StringValue).value),
//                inputs = FuncInputs(TableValue.empty()),
//                secrets = FuncSecrets(listOf()),
//                code = Code((f[IdentValue("run")] as DepCodeValue).value)
//            )
//
//            val res = HttpTemplate("http://localhost:8084")
//                .post("/v1/funcs")
//                .body(r)
//                .execute()
//
//            println(res)
//
//            return NilValue
//        } catch (t: Throwable) {
//            t.printStackTrace()
//            throw t
//        }
//    }
//
//}
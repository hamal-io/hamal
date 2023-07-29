package io.hamal.extension.std.debug

//class DebugExtension : Extension {
//    override fun create(): EnvValue {
//        return EnvValue(
//            ident = IdentValue("debug"),
//            values = TableValue(
//                "sleep" to Sleep(),
//            )
//        )
//    }
//
//}
//
//class Sleep : FuncValue() {
//    override fun invoke(ctx: FuncContext): Value {
//        val ms = (ctx.params.first().value as Number).toLong()
//        val time = measureTimeMillis {
//            Thread.sleep(ms)
//        }
//
//        println(time)
//        return NilValue
//    }
//
//}
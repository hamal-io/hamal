package io.hamal.script

import io.hamal.script.value.NilValue
import io.hamal.script.value.Value

object TestForeignLogModule : ForeignModule("log") {
    override fun functions(): Set<ForeignFunction> {
        return setOf(
            object : ForeignFunction("trace"){
                override fun invoke(ctx: Context): Value {
                    println("TRACE\t${ctx.parameter[0]}")
                    return NilValue
                }
            }
        )
    }
}
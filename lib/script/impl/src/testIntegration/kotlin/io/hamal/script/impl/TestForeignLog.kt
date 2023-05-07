package io.hamal.script.impl

import io.hamal.script.api.ForeignFunction
import io.hamal.script.api.ForeignModule
import io.hamal.script.api.value.NilValue
import io.hamal.script.api.value.Value

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
package io.hamal.lib.script.impl.builtin

import io.hamal.lib.kua.value.FuncContext
import io.hamal.lib.kua.value.FuncValue
import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.Value
import io.hamal.lib.script.impl.ExitException

object ExitFunction : FuncValue() {

    override fun invoke(ctx: FuncContext): Value {
        val status = ctx.params.firstOrNull()
            ?.value
            ?.let { value -> if (value is NumberValue) value else null }
            ?: NumberValue(0)

        throw ExitException(status)
    }

}
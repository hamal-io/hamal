package io.hamal.lib.script.impl.builtin

import io.hamal.lib.script.api.value.BuiltinFuncValue
import io.hamal.lib.script.api.value.NumberValue
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.ExitException

object ExitFunction : BuiltinFuncValue() {

    override fun invoke(ctx: Context): Value {
        val status = ctx.parameters.firstOrNull()
            ?.value
            ?.let { value -> if (value is NumberValue) value else null }
            ?: NumberValue(0)

        throw ExitException(status)
    }

}
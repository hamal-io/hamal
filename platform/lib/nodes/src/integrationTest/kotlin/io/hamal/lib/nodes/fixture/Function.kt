package io.hamal.lib.nodes.fixture

import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.Function1In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.type.*
import io.hamal.lib.value.value.*

class InvokeFunction : Function0In0Out() {
    override fun invoke(ctx: FunctionContext) {
        invocations++
    }

    var invocations: Int = 0
}

class CaptureFunction : Function1In0Out<KuaType>(FunctionInput1Schema(KuaType::class)) {
    override fun invoke(ctx: FunctionContext, arg1: KuaType) {
        when (arg1) {
            is KuaBoolean -> resultBoolean = if (arg1.booleanValue) ValueTrue else ValueFalse
            is KuaDecimal -> resultDecimal = ValueDecimal(arg1.value)
            is KuaNil -> resultNil = ValueNil
            is KuaNumber -> resultNumber = ValueNumber(arg1.doubleValue)
            is KuaString -> resultString = ValueString((arg1.stringValue))
            else -> TODO("Can not capture $arg1")
        }
    }

    var resultBoolean: ValueBoolean? = null
    var resultDecimal: ValueDecimal? = null
    var resultNumber: ValueNumber? = null
    var resultNil: ValueNil? = null
    var resultString: ValueString? = null
}

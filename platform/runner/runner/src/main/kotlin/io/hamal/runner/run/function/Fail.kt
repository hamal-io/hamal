package io.hamal.runner.run.function

import io.hamal.lib.common.hot.HotNumber
import io.hamal.lib.kua.ExitError
import io.hamal.lib.kua.function.Function1In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.kua.type.KuaType
import io.hamal.lib.kua.type.toHotObject

internal object FailRunFunction : Function1In0Out<KuaType>(
    FunctionInput1Schema(KuaType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaType) {
        when (arg1) {
            is KuaError -> throw ExitError(HotNumber(1), arg1.toHotObject())
            is KuaTable -> throw ExitError(HotNumber(1), arg1.toHotObject())
            else -> TODO()
        }
    }
}
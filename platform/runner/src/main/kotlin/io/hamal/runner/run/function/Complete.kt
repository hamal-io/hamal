package io.hamal.runner.run.function

import io.hamal.lib.kua.ExitError
import io.hamal.lib.kua.function.Function1In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.NumberType

internal object CompleteRunFunction : Function1In0Out<MapType>(FunctionInput1Schema(MapType::class)) {
    override fun invoke(ctx: FunctionContext, arg1: MapType) {
        throw ExitError(NumberType(0), arg1)
    }
}
package io.hamal.worker.extension.web3

import io.hamal.lib.script.api.value.NilValue
import io.hamal.lib.script.api.value.Value
import io.hamal.worker.extension.api.WorkerExtensionFunction
import io.hamal.worker.extension.api.WorkerExtensionFunction.Context

class StartFunction : WorkerExtensionFunction {
    override fun invoke(ctx: Context): Value {
        println("Hello World from Starter")
        return NilValue
    }
}
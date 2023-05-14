package io.hamal.worker.extension.web3.eth

import io.hamal.lib.script.api.value.NilValue
import io.hamal.lib.script.api.value.Value
import io.hamal.worker.extension.api.WorkerExtensionFunction
import io.hamal.worker.extension.api.WorkerExtensionFunction.Context

class EthGetBlock : WorkerExtensionFunction {
    override fun invoke(ctx: Context) : Value {
        println("get block")
        return NilValue
    }
}
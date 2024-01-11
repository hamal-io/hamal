package io.hamal.plugin.std.sys

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.function.Function1In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.sdk.api.ApiRequested

class AwaitFunction(
    private val httpTemplate: HttpTemplate
) : Function1In1Out<KuaString, KuaError>(
    FunctionInput1Schema(KuaString::class),
    FunctionOutput1Schema(KuaError::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaString): KuaError? {
        while (true) {
            httpTemplate.get("/v1/requests/{reqId}")
                .path("reqId", arg1.value)
                .execute(ApiRequested::class)
                .let {
                    when (it.status) {
                        RequestStatus.Completed,
                        RequestStatus.Failed -> {
                            return null
                        }

                        else -> {
                            Thread.sleep(1)
                        }
                    }
                }
        }
    }
}


class AwaitCompletedFunction(
    private val httpTemplate: HttpTemplate
) : Function1In1Out<KuaString, KuaError>(
    FunctionInput1Schema(KuaString::class),
    FunctionOutput1Schema(KuaError::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaString): KuaError? {
        while (true) {
            httpTemplate.get("/v1/requests/{reqId}")
                .path("reqId", arg1.value)
                .execute(ApiRequested::class)
                .let {
                    when (it.status) {
                        RequestStatus.Completed -> {
                            return null
                        }

                        RequestStatus.Failed -> {
                            return KuaError("expected $arg1 to complete but failed")
                        }

                        else -> {
                            Thread.sleep(1)
                        }
                    }
                }
        }
    }
}

class AwaitFailedFunction(
    private val httpTemplate: HttpTemplate
) : Function1In1Out<KuaString, KuaError>(
    FunctionInput1Schema(KuaString::class),
    FunctionOutput1Schema(KuaError::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaString): KuaError? {
        while (true) {
            httpTemplate.get("/v1/requests/{reqId}")
                .path("reqId", arg1.value)
                .execute(ApiRequested::class)
                .let {
                    when (it.status) {
                        RequestStatus.Completed -> {
                            return KuaError("expected $arg1 to fail but completed")
                        }

                        RequestStatus.Failed -> {
                            return null
                        }

                        else -> {
                            Thread.sleep(1)
                        }
                    }
                }
        }
    }
}

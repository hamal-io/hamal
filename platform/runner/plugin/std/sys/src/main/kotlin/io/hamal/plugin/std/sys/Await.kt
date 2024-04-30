package io.hamal.plugin.std.sys

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.function.Function1In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.sdk.api.ApiRequested
import io.hamal.lib.value.ValueString

class AwaitFunction(
    private val httpTemplate: HttpTemplate
) : Function1In1Out<ValueString, KuaError>(
    FunctionInput1Schema(ValueString::class),
    FunctionOutput1Schema(KuaError::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: ValueString): KuaError? {
        while (true) {
            httpTemplate.get("/v1/requests/{request_id}")
                .path("request_id", arg1.stringValue)
                .execute(ApiRequested::class)
                .let {
                    when (it.requestStatus) {
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
) : Function1In1Out<ValueString, KuaError>(
    FunctionInput1Schema(ValueString::class),
    FunctionOutput1Schema(KuaError::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: ValueString): KuaError? {
        while (true) {
            httpTemplate.get("/v1/requests/{request_id}")
                .path("request_id", arg1.stringValue)
                .execute(ApiRequested::class)
                .let {
                    when (it.requestStatus) {
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
) : Function1In1Out<ValueString, KuaError>(
    FunctionInput1Schema(ValueString::class),
    FunctionOutput1Schema(KuaError::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: ValueString): KuaError? {
        while (true) {
            httpTemplate.get("/v1/requests/{request_id}")
                .path("request_id", arg1.stringValue)
                .execute(ApiRequested::class)
                .let {
                    when (it.requestStatus) {
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

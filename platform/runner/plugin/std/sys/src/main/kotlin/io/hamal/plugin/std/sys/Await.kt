package io.hamal.plugin.std.sys

import io.hamal.lib.common.value.ValueError
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain._enum.RequestStatuses.Completed
import io.hamal.lib.domain._enum.RequestStatuses.Failed
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.function.Function1In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.sdk.api.ApiRequested

class AwaitFunction(
    private val httpTemplate: HttpTemplate
) : Function1In1Out<ValueString, ValueError>(
    FunctionInput1Schema(ValueString::class),
    FunctionOutput1Schema(ValueError::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: ValueString): ValueError? {
        while (true) {
            httpTemplate.get("/v1/requests/{request_id}")
                .path("request_id", arg1.stringValue)
                .execute(ApiRequested::class)
                .let {
                    when (it.requestStatus.enumValue) {
                        Completed,
                        Failed -> {
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
) : Function1In1Out<ValueString, ValueError>(
    FunctionInput1Schema(ValueString::class),
    FunctionOutput1Schema(ValueError::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: ValueString): ValueError? {
        while (true) {
            httpTemplate.get("/v1/requests/{request_id}")
                .path("request_id", arg1.stringValue)
                .execute(ApiRequested::class)
                .let {
                    when (it.requestStatus.enumValue) {
                        Completed -> {
                            return null
                        }

                        Failed -> {
                            return ValueError("expected $arg1 to complete but failed")
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
) : Function1In1Out<ValueString, ValueError>(
    FunctionInput1Schema(ValueString::class),
    FunctionOutput1Schema(ValueError::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: ValueString): ValueError? {
        while (true) {
            httpTemplate.get("/v1/requests/{request_id}")
                .path("request_id", arg1.stringValue)
                .execute(ApiRequested::class)
                .let {
                    when (it.requestStatus.enumValue) {
                        Completed -> {
                            return ValueError("expected $arg1 to fail but completed")
                        }

                        Failed -> {
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

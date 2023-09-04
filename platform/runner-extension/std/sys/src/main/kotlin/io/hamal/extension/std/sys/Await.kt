package io.hamal.extension.std.sys

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.function.Function1In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.hub.ApiSubmittedReq

class AwaitFunction(
    private val httpTemplate: HttpTemplate
) : Function1In1Out<StringType, ErrorType>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput1Schema(ErrorType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType): ErrorType? {
        while (true) {
            httpTemplate.get("/v1/reqs/{reqId}")
                .path("reqId", arg1.value)
                .execute(ApiSubmittedReq::class)
                .let {
                    when (it.status) {
                        ReqStatus.Completed,
                        ReqStatus.Failed -> {
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
) : Function1In1Out<StringType, ErrorType>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput1Schema(ErrorType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType): ErrorType? {
        while (true) {
            httpTemplate.get("/v1/reqs/{reqId}")
                .path("reqId", arg1.value)
                .execute(ApiSubmittedReq::class)
                .let {
                    when (it.status) {
                        ReqStatus.Completed -> {
                            return null
                        }

                        ReqStatus.Failed -> {
                            return ErrorType("expected $arg1 to complete but failed")
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
) : Function1In1Out<StringType, ErrorType>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput1Schema(ErrorType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType): ErrorType? {
        while (true) {
            httpTemplate.get("/v1/reqs/{reqId}")
                .path("reqId", arg1.value)
                .execute(ApiSubmittedReq::class)
                .let {
                    when (it.status) {
                        ReqStatus.Completed -> {
                            return ErrorType("expected $arg1 to fail but completed")
                        }

                        ReqStatus.Failed -> {
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

package io.hamal.extension.std.sys.topic

import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.HamalSdk

class ResolveTopicFunction(
    private val sdk: HamalSdk
) : Function1In2Out<StringType, ErrorType, StringType>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput2Schema(ErrorType::class, StringType::class)
) {

    override fun invoke(ctx: FunctionContext, arg1: StringType): Pair<ErrorType?, StringType?> {
        return try {
            null to StringType(
                sdk.topicService.resolve(TopicName(arg1.value))
                    .value
                    .value
                    .toString(16)
            )
        } catch (t: Throwable) {
            ErrorType(t.message ?: "Unknown Error") to null
        }
    }
}
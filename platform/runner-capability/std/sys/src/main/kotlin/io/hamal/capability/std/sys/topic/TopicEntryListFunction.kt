package io.hamal.capability.std.sys.topic

import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ArrayType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.ApiSdk

class TopicEntryListFunction(
    private val sdk: ApiSdk
) : Function1In2Out<StringType, ErrorType, ArrayType>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput2Schema(ErrorType::class, ArrayType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType): Pair<ErrorType?, ArrayType?> {
        return try {
            null to ArrayType(sdk.topic.entries(TopicId(arg1.value)).mapIndexed { index, entry ->
                index to entry.payload.value
            }.toMap().toMutableMap())
        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}
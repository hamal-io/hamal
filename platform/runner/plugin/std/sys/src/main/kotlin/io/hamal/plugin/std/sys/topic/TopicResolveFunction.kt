package io.hamal.plugin.std.sys.topic

import io.hamal.lib.common.value.ValueError
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.sdk.ApiSdk

class TopicResolveFunction(
    private val sdk: ApiSdk
) : Function1In2Out<ValueString, ValueError, ValueString>(
    FunctionInput1Schema(ValueString::class),
    FunctionOutput2Schema(ValueError::class, ValueString::class)
) {

    override fun invoke(ctx: FunctionContext, arg1: ValueString): Pair<ValueError?, ValueString?> {
        return try {
            null to ValueString(
                sdk.topic.resolve(ctx[NamespaceId::class], TopicName(arg1))
                    .value
                    .value
                    .toString(16)
            )
        } catch (t: Throwable) {
            ValueError(t.message ?: "Unknown Error") to null
        }
    }
}
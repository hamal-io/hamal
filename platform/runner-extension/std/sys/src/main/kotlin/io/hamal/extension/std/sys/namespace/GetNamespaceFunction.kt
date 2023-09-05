package io.hamal.extension.std.sys.func

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.HubSdk

class GetNamespaceFunction(
    private val sdk: HubSdk
) : Function1In2Out<StringType, ErrorType, MapType>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType): Pair<ErrorType?, MapType?> {
        return try {
            null to sdk.namespace.get(NamespaceId(arg1.value))
                .let { namespace ->
                    MapType(
                        mutableMapOf(
                            "id" to StringType(namespace.id.value.value.toString(16)),
                            "name" to StringType(namespace.name.value)
                        )
                    )
                }
        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}
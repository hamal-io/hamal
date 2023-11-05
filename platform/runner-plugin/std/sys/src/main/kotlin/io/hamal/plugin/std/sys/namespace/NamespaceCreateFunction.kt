package io.hamal.plugin.std.sys.namespace

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.NamespaceInputs
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiNamespaceCreateReq

class NamespaceCreateFunction(
    private val sdk: ApiSdk
) : Function1In2Out<MapType, ErrorType, MapType>(
    FunctionInput1Schema(MapType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: MapType): Pair<ErrorType?, MapType?> {
        return try {
            val res = sdk.namespace.create(
                ctx[GroupId::class],
                ApiNamespaceCreateReq(
                    name = NamespaceName(arg1.getString("name")),
                    inputs = NamespaceInputs(),
                )
            )

            null to MapType(
                mutableMapOf(
                    "id" to StringType(res.id.value.value.toString(16)),
                    "status" to StringType(res.status.name),
                    "namespace_id" to StringType(res.namespaceId.value.value.toString(16))
                )
            )

        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}
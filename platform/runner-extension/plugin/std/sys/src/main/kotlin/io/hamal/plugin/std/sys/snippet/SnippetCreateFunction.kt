package io.hamal.plugin.std.sys.snippet

import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.SnippetInputs
import io.hamal.lib.domain.vo.SnippetName
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiCreateSnippetReq

class SnippetCreateFunction(
    private val sdk: ApiSdk
) : Function1In2Out<MapType, ErrorType, MapType>(
    FunctionInput1Schema(MapType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: MapType): Pair<ErrorType?, MapType?> {
        return try {
            val res = sdk.snippet.create(
                ctx[GroupId::class],
                ApiCreateSnippetReq(
                    name = SnippetName(arg1.getString("name")),
                    inputs = SnippetInputs(),
                    value = CodeValue(arg1.getString("value"))
                )
            )

            null to MapType(
                mutableMapOf(
                    "id" to StringType(res.id.value.value.toString(16)),
                    "status" to StringType(res.status.name),
                    "snippet_id" to StringType(res.snippetId.value.value.toString(16)),
                    "group_id" to StringType(res.groupId.value.value.toString(16)),
                )
            )

        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }

    }
}
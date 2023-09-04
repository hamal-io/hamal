package io.hamal.extension.std.sys.func

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ArrayType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.hub.HubNamespaceList

class ListNamespaceFunction(
    private val httpTemplate: HttpTemplate
) : Function0In2Out<ErrorType, ArrayType>(
    FunctionOutput2Schema(ErrorType::class, ArrayType::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<ErrorType?, ArrayType?> {
        val namespaces = try {
            httpTemplate
                .get("/v1/namespaces")
                .execute(HubNamespaceList::class)
                .namespaces

        } catch (t: Throwable) {
            t.printStackTrace()
            listOf()
        }

        return null to ArrayType(
            namespaces.mapIndexed { index, namespace ->
                index to MapType(
                    mutableMapOf(
                        "id" to StringType(namespace.id.value.value.toString(16)),
                        "name" to StringType(namespace.name.value),
                    )
                )
            }.toMap().toMutableMap()
        )
    }
}
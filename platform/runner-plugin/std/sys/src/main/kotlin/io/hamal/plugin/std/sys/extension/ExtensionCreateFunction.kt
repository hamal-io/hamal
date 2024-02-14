package io.hamal.plugin.std.sys.extension

import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.ExtensionName
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaMap
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiExtensionCreateRequest

class ExtensionCreateFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaMap, KuaError, KuaMap>(
    FunctionInput1Schema(KuaMap::class),
    FunctionOutput2Schema(KuaError::class, KuaMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaMap): Pair<KuaError?, KuaMap?> {
        return try {
            val res = sdk.extension.create(
                ctx[WorkspaceId::class],
                ApiExtensionCreateRequest(
                    name = ExtensionName(arg1.getString("name")),
                    code = CodeValue(arg1.getString("code"))
                )
            )

            null to KuaMap(
                mutableMapOf(
                    "id" to KuaString(res.id.value.value.toString(16)),
                    "status" to KuaString(res.status.name),
                    "extension_id" to KuaString(res.extensionId.value.value.toString(16)),
                    "workspace_id" to KuaString(res.workspaceId.value.value.toString(16))
                )
            )

        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }

}
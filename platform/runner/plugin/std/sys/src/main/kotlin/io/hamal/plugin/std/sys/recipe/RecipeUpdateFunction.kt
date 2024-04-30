package io.hamal.plugin.std.sys.recipe

import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.RecipeId
import io.hamal.lib.domain.vo.RecipeInputs
import io.hamal.lib.domain.vo.RecipeName
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.kua.type.findString
import io.hamal.lib.kua.type.getString
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiRecipeUpdateRequest
import io.hamal.lib.value.ValueString

class RecipeUpdateFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, KuaError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {

    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<KuaError?, KuaTable?> {
        return try {
            val res = sdk.recipe.update(
                RecipeId(arg1.getString("id").stringValue),
                ApiRecipeUpdateRequest(
                    name = arg1.findString("name")?.let { RecipeName(it.stringValue) },
                    inputs = RecipeInputs(),
                    value = arg1.findString("value")?.let { CodeValue(it.stringValue) }
                )
            )

            null to ctx.tableCreate(
                "request_id" to ValueString(res.requestId.value.value.toString(16)),
                "request_status" to ValueString(res.requestStatus.name),
                "id" to ValueString(res.id.value.value.toString(16))
            )
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}

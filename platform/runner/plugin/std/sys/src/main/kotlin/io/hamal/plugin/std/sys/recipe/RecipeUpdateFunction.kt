package io.hamal.plugin.std.sys.recipe

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.common.value.ValueError
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain.vo.RecipeId.Companion.RecipeId
import io.hamal.lib.domain.vo.RecipeInputs
import io.hamal.lib.domain.vo.RecipeName
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.kua.value.findString
import io.hamal.lib.kua.value.getString
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiRecipeUpdateRequest

class RecipeUpdateFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, ValueError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(ValueError::class, KuaTable::class)
) {

    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<ValueError?, KuaTable?> {
        return try {
            val res = sdk.recipe.update(
                RecipeId(arg1.getString("id").stringValue),
                ApiRecipeUpdateRequest(
                    name = arg1.findString("name")?.let { RecipeName(it) },
                    inputs = RecipeInputs(),
                    value = arg1.findString("value")?.let { ValueCode(it.stringValue) }
                )
            )

            null to ctx.tableCreate(
                "request_id" to ValueString(res.requestId.stringValue),
                "request_status" to ValueString(res.requestStatus.name),
                "id" to ValueString(res.id.stringValue)
            )
        } catch (t: Throwable) {
            ValueError(t.message!!) to null
        }
    }
}

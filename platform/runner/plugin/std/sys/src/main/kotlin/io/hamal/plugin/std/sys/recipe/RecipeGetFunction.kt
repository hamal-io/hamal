package io.hamal.plugin.std.sys.recipe

import io.hamal.lib.common.value.ValueError
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain.vo.RecipeId.Companion.RecipeId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.sdk.ApiSdk

class RecipeGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<ValueString, ValueError, KuaTable>(
    FunctionInput1Schema(ValueString::class),
    FunctionOutput2Schema(ValueError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: ValueString): Pair<ValueError?, KuaTable?> {
        return try {
            null to sdk.recipe.get(RecipeId(arg1.stringValue))
                .let { recipe ->
                    ctx.tableCreate(
                        "id" to ValueString(recipe.id.stringValue),
                        "name" to recipe.name,
                        "value" to recipe.value
                    )
                }
        } catch (t: Throwable) {
            ValueError(t.message!!) to null
        }
    }
}
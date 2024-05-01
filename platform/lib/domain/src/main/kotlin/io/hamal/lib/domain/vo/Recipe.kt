package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectHotObject
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueSnowflakeId
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.common.value.ValueVariableSnowflakeId
import io.hamal.lib.common.value.ValueVariableString


class RecipeId(override val value: ValueSnowflakeId) : ValueVariableSnowflakeId() {
    companion object {
        fun RecipeId(value: SnowflakeId) = RecipeId(ValueSnowflakeId(value))
        fun RecipeId(value: Int) = RecipeId(ValueSnowflakeId(SnowflakeId(value.toLong())))
        fun RecipeId(value: String) = RecipeId(ValueSnowflakeId(SnowflakeId(value.toLong(16))))
    }
}

class RecipeName(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun RecipeName(value: String) = RecipeName(ValueString(value))
    }
}

class RecipeDescription(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun RecipeDescription(value: String) = RecipeDescription(ValueString(value))
        val empty = RecipeDescription("")
    }
}

class RecipeInputs(override val value: HotObject = HotObject.empty) : ValueObjectHotObject()
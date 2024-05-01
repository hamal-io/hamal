package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectHotObject
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.common.value.ValueVariableString


class RecipeId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
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
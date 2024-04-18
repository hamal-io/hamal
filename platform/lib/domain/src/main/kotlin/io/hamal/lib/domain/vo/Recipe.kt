package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectHotObject
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.snowflake.SnowflakeId


class RecipeId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
}

class RecipeName(override val value: String) : ValueObjectString()

class RecipeDescription(override val value: String) : ValueObjectString() {
    companion object {
        val empty = RecipeDescription("")
    }
}

class RecipeInputs(override val value: HotObject = HotObject.empty) : ValueObjectHotObject()
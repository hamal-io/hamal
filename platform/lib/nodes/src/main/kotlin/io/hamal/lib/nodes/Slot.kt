package io.hamal.lib.nodes

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.snowflake.SnowflakeId

class SlotId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
}

class SlotName(override val value: String) : ValueObjectString()


interface Slot {
    val id: SlotId
    val name: SlotName
}

data class SlotInput(
    override val id: SlotId,
    override val name: SlotName
) : Slot

data class SlotOutput(
    override val id: SlotId,
    override val name: SlotName
) : Slot
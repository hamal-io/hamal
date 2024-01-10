package io.hamal.lib.common.domain

import io.hamal.lib.common.util.TimeUtils
import java.time.Instant


interface DomainObject<ID : ValueObjectId> {
    val id: ID
    val partition get() = id.partition()
    val createdAt get() = CreatedAt(Instant.ofEpochMilli(id.value.elapsed().value + 1698451200000L))
    val updatedAt: UpdatedAt
}

class CreatedAt(override val value: Instant) : ValueObjectInstant() {
    companion object {
        @JvmStatic
        fun now(): CreatedAt = CreatedAt(TimeUtils.now())
    }
}

class UpdatedAt(override val value: Instant) : ValueObjectInstant() {
    companion object {
        @JvmStatic
        fun now(): UpdatedAt = UpdatedAt(TimeUtils.now())
    }
}
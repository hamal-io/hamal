package io.hamal.lib.domain

import io.hamal.lib.domain.vo.base.DomainId


abstract class DomainObject<ID : DomainId> {
    abstract val id: ID
    val shard get(): Shard = Shard(id.partition().value.toInt())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as DomainObject<*>
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}
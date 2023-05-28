package io.hamal.lib.domain.value

import kotlinx.serialization.Serializable

@Serializable
sealed interface Value {
    val metaTable: MetaTable
    fun type(): String = metaTable.type

    fun findInfixOperation(type: ValueOperation.Type, otherType: String): InfixValueOperation? {
        return metaTable.operations
            .filterIsInstance<InfixValueOperation>()
            .find { it.operationType == type && it.otherType == otherType }
    }

    fun findPrefixOperation(type: ValueOperation.Type): PrefixValueOperation? {
        return metaTable.operations
            .filterIsInstance<PrefixValueOperation>()
            .find { it.operationType == type }
    }
}

@Serializable
sealed interface KeyValue : Value
package io.hamal.lib.common.value

import kotlinx.serialization.Serializable

@Serializable
sealed interface Value {
    val metaTable: MetaTable
    fun type(): String = metaTable.type

    fun findInfixOperation(type: ValueOperator.Type, otherType: String): InfixValueOperator? {
        return metaTable.operators
            .filterIsInstance<InfixValueOperator>()
            .find { it.operationType == type && it.otherType == otherType }
    }

    fun findPrefixOperation(type: ValueOperator.Type): PrefixValueOperator? {
        return metaTable.operators
            .filterIsInstance<PrefixValueOperator>()
            .find { it.operationType == type }
    }
}

@Serializable
sealed interface KeyValue : Value
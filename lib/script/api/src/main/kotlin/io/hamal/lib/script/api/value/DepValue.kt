package io.hamal.lib.script.api.value

interface DepValue {
    val metaTable: DepMetaTable
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
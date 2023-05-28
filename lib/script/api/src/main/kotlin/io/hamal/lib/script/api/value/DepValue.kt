package io.hamal.lib.script.api.value

interface DepValue {
    val metaTable: DepMetaTable
    fun type(): String = metaTable.type

    fun findInfixOperation(type: DepValueOperation.Type, otherType: String): DepInfixValueOperation? {
        return metaTable.operations
            .filterIsInstance<DepInfixValueOperation>()
            .find { it.operationType == type && it.otherType == otherType }
    }

    fun findPrefixOperation(type: DepValueOperation.Type): DepPrefixValueOperation? {
        return metaTable.operations
            .filterIsInstance<DepPrefixValueOperation>()
            .find { it.operationType == type }
    }
}
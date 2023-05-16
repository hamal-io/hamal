package io.hamal.lib.script.api.value

interface Value {
    val metaTable: MetaTable
    fun type(): String = metaTable.type

    fun findInfixOperation(type: ValueOperation.Type, otherType: String): InfixValueOperation? {
        return metaTable.operations
            .filterIsInstance<InfixValueOperation>()
            .find { it.operationType == type && it.otherType == otherType }
    }
}
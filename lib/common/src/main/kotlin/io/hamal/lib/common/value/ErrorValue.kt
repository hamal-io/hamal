package io.hamal.lib.common.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("ErrorValue")
data class ErrorValue(val value: String) : Value {
    @Transient
    override val metaTable = DefaultErrorMetaTable
}

object DefaultErrorMetaTable : MetaTable {
    override val type = "error"
    override val operators: List<ValueOperator> = listOf()
}


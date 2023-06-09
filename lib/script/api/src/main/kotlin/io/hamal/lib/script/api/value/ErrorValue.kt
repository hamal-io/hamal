package io.hamal.lib.script.api.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("ErrorValue")
data class ErrorValue(val message: String) : Value {
    @Transient
    override val metaTable = DefaultErrorValueMetaTable
}

object DefaultErrorValueMetaTable : MetaTable {
    override val type = "error"
    override val operators: List<ValueOperator> = listOf()
}


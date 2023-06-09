package io.hamal.lib.script.api.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("CodeValue")
data class CodeValue(val value: String) : Value {
    @Transient
    override val metaTable = DefaultCodeValueMetaTable

}

object DefaultCodeValueMetaTable : MetaTable {
    override val type = "code"
    override val operators: List<ValueOperator> = listOf()
}


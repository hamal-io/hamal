package io.hamal.lib.script.api.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("NilValue")
object NilValue : Value {
    @Transient
    override val metaTable = DefaultNilMetaTable
    override fun toString(): String {
        return "nil"
    }
}

object DefaultNilMetaTable : MetaTable {
    override val type = "nil"
    override val operators = listOf<ValueOperator>()
}
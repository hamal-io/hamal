package io.hamal.lib.common.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@SerialName("MetaTable")
sealed interface MetaTable {
    val type: String
    val operators: List<ValueOperator>
}
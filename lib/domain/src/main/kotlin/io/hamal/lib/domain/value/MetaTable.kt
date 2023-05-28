package io.hamal.lib.domain.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@SerialName("MetaTable")
sealed interface MetaTable {
    val type: String
    val operations: List<ValueOperation>
}
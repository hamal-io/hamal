package io.hamal.lib.script.api.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@SerialName("MetaTable")
sealed interface MetaTable<VALUE : Value> {
    val type: String
    val operators: List<ValueOperator>
    val props: Map<IdentValue, ValueProp<VALUE>>
}
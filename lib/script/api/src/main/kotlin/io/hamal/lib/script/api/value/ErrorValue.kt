package io.hamal.lib.script.api.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("ErrorValue")
data class ErrorValue(
    val message: String
) : Value {

    @Transient
    override val metaTable = DefaultErrorValueMetaTable
    operator fun get(key: IdentValue): Value {
        return metaTable.props[key]
            ?.let { it(this) }
            ?: NilValue
    }
}

object DefaultErrorValueMetaTable : MetaTable<ErrorValue> {
    override val type = "error"
    override val operators: List<ValueOperator> = listOf()
    override val props: Map<IdentValue, ValueProp<ErrorValue>> = mapOf(
        IdentValue("message") to ErrorMessageProp
    )
}

object ErrorMessageProp : ValueProp<ErrorValue> {
    override fun invoke(self: ErrorValue): Value {
        return StringValue(self.message)
    }
}
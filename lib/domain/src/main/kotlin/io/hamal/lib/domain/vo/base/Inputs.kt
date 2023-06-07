package io.hamal.lib.domain.vo.base

import io.hamal.lib.common.ddd.ValueObject
import io.hamal.lib.common.value.StringValue
import io.hamal.lib.common.value.TableEntry
import io.hamal.lib.common.value.TableValue
import io.hamal.lib.domain.vo.ExecInputs
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json

@Serializable
abstract class Inputs : ValueObject.BaseImpl<TableValue>() {
    override fun toString(): String {
        return "${this::class.simpleName}(${value})"
    }
}

abstract class InputsSerializer<INPUT : Inputs>(
    val fn: (TableValue) -> INPUT
) : KSerializer<INPUT> {
    private val delegate = TableValue.serializer()
    override val descriptor = delegate.descriptor

    override fun deserialize(decoder: Decoder): INPUT {
        return fn(delegate.deserialize(decoder))
    }

    override fun serialize(encoder: Encoder, value: INPUT) {
        delegate.serialize(encoder, value.value)
    }
}


fun main() {
    println(
        Json {}.encodeToString(
            ExecInputs.serializer(), ExecInputs(
                TableValue(
                    listOf(
                        TableEntry(StringValue("A"), StringValue("B")),
                        TableEntry(StringValue("A"), StringValue("B")),
                    )
                )
            )
        )
    )

    println(
        Json {}.decodeFromString(
            ExecInputs.serializer(),
            "{\"entries\":[{\"key\":{\"type\":\"String\",\"value\":\"A\"},\"value\":{\"type\":\"String\",\"value\":\"B\"}}]}\n"
        )
    )
}

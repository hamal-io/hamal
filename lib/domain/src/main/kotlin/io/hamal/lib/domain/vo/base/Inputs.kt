package io.hamal.lib.domain.vo.base

import io.hamal.lib.common.ddd.ValueObject
import io.hamal.lib.common.value.TableEntry
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
abstract class Inputs : ValueObject.BaseImpl<List<TableEntry>>() {
    override fun toString(): String {
        return "${this::class.simpleName}(${value})"
    }
}

abstract class InputsSerializer<INPUT : Inputs>(
    val fn: (List<TableEntry>) -> INPUT
) : KSerializer<INPUT> {
    private val delegate = ListSerializer(TableEntry.serializer())
    override val descriptor = delegate.descriptor

    override fun deserialize(decoder: Decoder): INPUT {
        return fn(delegate.deserialize(decoder))
    }

    override fun serialize(encoder: Encoder, value: INPUT) {
        delegate.serialize(encoder, value.value)
    }
}



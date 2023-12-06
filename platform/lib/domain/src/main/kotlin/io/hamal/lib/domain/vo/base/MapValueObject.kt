package io.hamal.lib.domain.vo.base

import io.hamal.lib.common.domain.ValueObject
import io.hamal.lib.kua.type.MapType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
abstract class MapValueObject : ValueObject.BaseImpl<MapType>() {
    override fun toString(): String {
        return "${this::class.simpleName}(${value})"
    }
}

abstract class InputsSerializer<INPUT : MapValueObject>(
    val fn: (MapType) -> INPUT
) : KSerializer<INPUT> {
    private val delegate = MapType.serializer()
    override val descriptor = delegate.descriptor

    override fun deserialize(decoder: Decoder): INPUT {
        return fn(delegate.deserialize(decoder))
    }

    override fun serialize(encoder: Encoder, value: INPUT) {
        delegate.serialize(encoder, value.value)
    }
}

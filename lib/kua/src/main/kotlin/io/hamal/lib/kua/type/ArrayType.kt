package io.hamal.lib.kua.type

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.DomainId
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.KClass

@Serializable
data class ArrayType(
    @Serializable(with = Serializer::class)
    val entries: MutableMap<Int, SerializableType> = mutableMapOf(),
) : SerializableType {

    val size get() = entries.size

    fun append(value: ArrayType): Int {
        entries[entries.size + 1] = value
        return size
    }

    fun append(value: SerializableType): Int {
        entries[entries.size + 1] = value
        return size
    }

    operator fun get(idx: Int): SerializableType {
        return entries[idx]!!
    }

    fun getBoolean(idx: Int) = getBooleanValue(idx) == TrueValue
    fun getBooleanValue(idx: Int): BooleanType {
        checkExpectedType(idx, BooleanType::class)
        return entries[idx]!! as BooleanType
    }

    fun append(value: Boolean) = append(booleanOf(value))
    fun append(value: BooleanType): Int {
        entries[entries.size + 1] = value
        return size
    }


    fun getInt(idx: Int) = getNumberType(idx).value.toInt()
    fun getLong(idx: Int) = getNumberType(idx).value.toLong()
    fun getFloat(idx: Int) = getNumberType(idx).value.toFloat()
    fun getDouble(idx: Int) = getNumberType(idx).value.toDouble()
    fun getNumberType(idx: Int): NumberType {
        checkExpectedType(idx, NumberType::class)
        return entries[idx]!! as NumberType
    }


    fun append(value: Int) = append(value.toDouble())
    fun append(value: Long) = append(value.toDouble())
    fun append(value: Float) = append(value.toDouble())
    fun append(value: Double) = append(NumberType(value))
    fun append(value: NumberType): Int {
        entries[entries.size + 1] = value
        return size
    }

    fun append(value: SnowflakeId) = append(value.value.toString(16))
    fun append(value: DomainId) = append(value.value.value)

    //
//    fun getString(idx: Int) = getStringValue(idx).value
//    fun getStringValue(idx: Int): StringType

    fun append(value: MapType): Int {
        entries[entries.size + 1] = value
        return size
    }


    fun append(value: String) = append(StringType(value))
    fun append(value: StringType): Int {
        entries[entries.size + 1] = value
        return size
    }

    fun type(idx: Int): KClass<out Type> {
        return entries[idx]?.let { it::class } ?: NilType::class
    }

    object Serializer : KSerializer<MutableMap<Int, SerializableType>> {
        private val delegate = MapSerializer(Int.serializer(), SerializableType.serializer())

        @OptIn(ExperimentalSerializationApi::class)
        override val descriptor = SerialDescriptor("ArrayType", delegate.descriptor)

        override fun deserialize(decoder: Decoder): MutableMap<Int, SerializableType> {
            return delegate.deserialize(decoder).toMutableMap()
        }

        override fun serialize(encoder: Encoder, value: MutableMap<Int, SerializableType>) {
            return delegate.serialize(encoder, value)
        }
    }
}

internal fun ArrayType.checkExpectedType(idx: Int, expected: KClass<out Type>) {
    check(type(idx) == expected) {
        "Expected type to be $expected but was $this"
    }
}
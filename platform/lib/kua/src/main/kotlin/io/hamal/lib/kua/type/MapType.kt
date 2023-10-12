package io.hamal.lib.kua.type

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.domain.DomainId
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.KClass

@Serializable
@SerialName("MapType")
data class MapType(
    @Serializable(with = Serializer::class)
    val entries: MutableMap<String, SerializableType> = mutableMapOf(),
) : TableType {

    constructor(vararg pairs: Pair<String, SerializableType>) : this(mutableMapOf(*pairs))

    val size get() = entries.size

    operator fun get(key: String): SerializableType = entries[key] ?: NilType
    operator fun set(key: String, value: SerializableType): Int {
        entries[key] = value
        return size
    }

    fun unset(key: StringType) = unset(key.value)
    operator fun set(key: String, value: NilType) = unset(key)
    operator fun set(key: StringType, value: NilType) = unset(key.value)
    fun unset(key: String): Int {
        entries.remove(key)
        return size
    }

    fun getBooleanValue(key: StringType) = getBooleanValue(key.value)
    fun getBoolean(key: String): Boolean = getBooleanValue(key).value
    fun getBoolean(key: StringType): Boolean = getBoolean(key.value)
    fun getBooleanValue(key: String): BooleanType {
        checkExpectedType(key, BooleanType::class)
        return entries[key]!! as BooleanType
    }

    operator fun set(key: StringType, value: BooleanType) = set(key, value.value)
    operator fun set(key: StringType, value: Boolean) = set(key.value, booleanOf(value))
    operator fun set(key: String, value: Boolean) = set(key, booleanOf(value))
    operator fun set(key: String, value: BooleanType): Int {
        entries[key] = value
        return size
    }


    fun getCodeType(key: String) = CodeType(getString(key))
    fun getCodeType(key: StringType) = getCodeType(key.value)
    operator fun set(key: StringType, value: CodeType) = set(key.value, value)
    operator fun set(key: String, value: CodeType): Int {
        entries[key] = StringType(value.value)
        return size
    }

    operator fun set(key: String, value: DomainId) = set(key, value.value.value.toString(16))
    operator fun set(key: StringType, value: DomainId) = set(key.value, value.value.value.toString(16))
    operator fun set(key: String, value: SnowflakeId) = set(key, value.value.toString(16))
    operator fun set(key: StringType, value: SnowflakeId) = set(key.value, value.value.toString(16))

    fun getInt(key: String): Int = getNumberValue(key).value.toInt()
    fun getInt(key: StringType) = getInt(key.value)
    fun getLong(key: String): Long = getNumberValue(key).value.toLong()
    fun getLong(key: StringType): Long = getLong(key.value)
    fun getFloat(key: String): Float = getNumberValue(key).value.toFloat()
    fun getFloat(key: StringType): Float = getFloat(key.value)
    fun getDouble(key: String): Double = getNumberValue(key).value
    fun getDouble(key: StringType): Double = getDouble(key.value)
    fun getNumberValue(key: StringType): NumberType = getNumberValue(key.value)
    fun getNumberValue(key: String): NumberType {
        checkExpectedType(key, NumberType::class)
        return entries[key]!! as NumberType
    }

    operator fun set(key: String, value: Int) = set(key, value.toDouble())
    operator fun set(key: String, value: Long) = set(key, value.toDouble())
    operator fun set(key: String, value: Float) = set(key, value.toDouble())
    operator fun set(key: String, value: Double) = set(key, NumberType(value))
    operator fun set(key: StringType, value: NumberType) = set(key.value, value.value)
    operator fun set(key: String, value: NumberType): Int {
        entries[key] = value
        return size
    }

    operator fun set(key: String, value: MapType): Int {
        entries[key] = value
        return size
    }

    fun getStringType(key: StringType) = getStringType(key.value)
    fun getString(key: String): String = getStringType(key).value
    fun getString(key: StringType): String = getString(key.value)
    fun getStringType(key: String): StringType {
        checkExpectedType(key, StringType::class)
        return entries[key]!! as StringType
    }

    operator fun set(key: String, value: String) = set(key, StringType(value))
    operator fun set(key: StringType, value: StringType) = set(key.value, value.value)
    operator fun set(key: String, value: StringType): Int {
        entries[key] = value
        return size
    }

    fun type(key: String): KClass<out Type> {
        return entries[key]?.let { it::class } ?: NilType::class
    }

    object Serializer : KSerializer<MutableMap<String, SerializableType>> {
        private val delegate = MapSerializer(String.serializer(), SerializableType.serializer())

        @OptIn(ExperimentalSerializationApi::class)
        override val descriptor = SerialDescriptor("MapType", delegate.descriptor)

        override fun deserialize(decoder: Decoder): MutableMap<String, SerializableType> {
            return delegate.deserialize(decoder).toMutableMap()
        }

        override fun serialize(encoder: Encoder, value: MutableMap<String, SerializableType>) {
            return delegate.serialize(encoder, value)
        }
    }
}

internal fun MapType.checkExpectedType(key: String, expected: KClass<out Type>) {
    check(type(key) == expected) {
        "Expected type to be $expected but was $this"
    }
}
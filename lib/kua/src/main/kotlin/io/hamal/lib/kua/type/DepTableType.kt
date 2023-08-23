package io.hamal.lib.kua.type

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.collections.Map.Entry

typealias TableEntry = Entry<StringType, SerializableType>

@SerialName("TableType")
@Serializable
data class DepTableType(
    @Serializable(with = Serializer::class)
    private val entries: MutableMap<StringType, SerializableType> = mutableMapOf(),
) : SerializableType, Collection<TableEntry> {

    constructor(vararg pairs: Pair<Any, SerializableType>) : this() {
        pairs.forEach { pair ->
            when (val key = pair.first) {
                is String -> entries[StringType(key)] = pair.second
                is Number -> entries[StringType(key.toString())] = pair.second
                is StringType -> entries[key] = pair.second
            }
        }
    }

    operator fun set(key: Int, value: SerializableType) {
        this[StringType(key.toString())] = value
    }

    operator fun set(key: StringType, value: SerializableType) {
        entries[key] = value
    }

    fun setAll(tableValue: DepTableType) {
        tableValue.entries.forEach { entry ->
            entries[entry.key] = entry.value
        }
    }

    operator fun get(key: Int): Type = get(StringType(key.toString()))
    operator fun get(key: String): Type = get(StringType(key))
    operator fun get(key: Type): Type = when (key) {
        is StringType -> get(key)
        is NumberType -> get(StringType(key.value.toString()))
        else -> TODO()
    }

    operator fun get(key: StringType): Type {
        return entries[key]!!
    }


    fun getStringType(key: String) = entries[(StringType(key))]!! as StringType

    fun remove(key: Int) {
        remove(NumberType(key))
    }

    fun remove(key: Type) {
        entries.remove(key)
    }

    override val size: Int get() = entries.size

    override fun isEmpty() = entries.isEmpty()

    override fun iterator(): Iterator<TableEntry> {
        return entries.asIterable().iterator()
    }

    override fun containsAll(elements: Collection<TableEntry>) = elements.all(::contains)
    override fun contains(element: TableEntry) = TODO() // store.containsKey(element.first)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DepTableType

        return entries == other.entries
    }

    override fun hashCode(): Int {
        return entries.hashCode()
    }

    override fun toString(): String {
        val valueString = entries.map { "(${it.key},${it.value})" }.joinToString(",")
        return "{$valueString}"
    }

    object Serializer : KSerializer<MutableMap<StringType, SerializableType>> {
        private val delegate = MapSerializer(KeySerializer, SerializableType.serializer())

        @OptIn(ExperimentalSerializationApi::class)
        override val descriptor = SerialDescriptor("TableType", delegate.descriptor)

        override fun deserialize(decoder: Decoder): MutableMap<StringType, SerializableType> {
            return delegate.deserialize(decoder).toMutableMap()
        }

        override fun serialize(encoder: Encoder, value: MutableMap<StringType, SerializableType>) {
            return delegate.serialize(encoder, value)
        }
    }

    object KeySerializer : KSerializer<StringType> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("KeyValue", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): StringType {
            return StringType(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: StringType) {
            return encoder.encodeString(value.value)
        }
    }
}

infix fun String.to(that: String): Pair<String, StringType> = Pair(this, StringType(that))
infix fun String.to(that: Number): Pair<String, NumberType> = Pair(this, NumberType(that.toDouble()))
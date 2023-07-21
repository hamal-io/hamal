package io.hamal.lib.kua.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// FIXME have serializable table and table which can have them all

@Serializable
@SerialName("TableEntry")
data class TableEntry(val key: StringValue, val value: Value)

@SerialName("TableValue")
@Serializable
data class TableValue(
//    @Serializable(with = Serializer::class)
    private val entries: MutableMap<StringValue, Value> = mutableMapOf(),
) : Value, Collection<TableEntry> {
    constructor(vararg pairs: Pair<Any, Value>) : this() {
        pairs.forEach { pair ->
            when (val key = pair.first) {
                is String -> entries[StringValue(key)] = pair.second
                is Number -> entries[StringValue(key.toString())] = pair.second
                is StringValue -> entries[key] = pair.second
            }
        }
    }

    operator fun set(key: Int, value: Value) {
        this[StringValue(key.toString())] = value
    }

    operator fun set(key: StringValue, value: Value) {
        entries[key] = value
    }

    fun setAll(tableValue: TableValue) {
        tableValue.entries.forEach { entry ->
            entries[entry.key] = entry.value
        }
    }

    operator fun get(key: Int): Value = get(StringValue(key.toString()))
    operator fun get(key: String): Value = get(StringValue(key))
    operator fun get(key: Value): Value = when (key) {
        is StringValue -> get(key)
        is DecimalValue -> get(StringValue(key.value.toString()))
        else -> TODO()
    }

    operator fun get(key: StringValue): Value {
        return entries[key]!!
    }


    fun remove(key: Int) {
        remove(DecimalValue(key))
    }

    fun remove(key: Value) {
        entries.remove(key)
    }

    override val size: Int get() = entries.size

    override fun isEmpty() = entries.isEmpty()

    override fun iterator(): Iterator<TableEntry> {
        return entries.asIterable().map { entry -> TableEntry(entry.key, entry.value) }.iterator()
    }

    override fun containsAll(elements: Collection<TableEntry>) = elements.all(::contains)
    override fun contains(element: TableEntry) = TODO() // store.containsKey(element.first)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TableValue

        return entries == other.entries
    }

    override fun hashCode(): Int {
        return entries.hashCode()
    }

    override fun toString(): String {
        val valueString = entries.map { "(${it.key},${it.value})" }.joinToString(",")
        return "{$valueString}"
    }

//    object Serializer : KSerializer<MutableMap<StringValue, Value>> {
//        private val delegate = MapSerializer(KeySerializer, Value.serializer())
//
//        @OptIn(ExperimentalSerializationApi::class)
//        override val descriptor = SerialDescriptor("TableValue", delegate.descriptor)
//
//        override fun deserialize(decoder: Decoder): MutableMap<StringValue, Value> {
//            return delegate.deserialize(decoder).toMutableMap()
//        }
//
//        override fun serialize(encoder: Encoder, value: MutableMap<StringValue, Value>) {
//            return delegate.serialize(encoder, value)
//        }
//    }
//
//    object KeySerializer : KSerializer<StringValue> {
//        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("KeyValue", PrimitiveKind.STRING)
//
//        override fun deserialize(decoder: Decoder): StringValue {
//            return StringValue(decoder.decodeString())
//        }
//
//        override fun serialize(encoder: Encoder, value: StringValue) {
//            return encoder.encodeString(value.value)
//        }
//    }
}

infix fun String.to(that: String): Pair<String, StringValue> = Pair(this, StringValue(that))
infix fun String.to(that: Number): Pair<String, DecimalValue> = Pair(this, DecimalValue(that))
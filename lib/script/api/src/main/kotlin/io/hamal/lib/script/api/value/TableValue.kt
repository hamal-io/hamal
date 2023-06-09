package io.hamal.lib.script.api.value

import kotlinx.serialization.*
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


@Serializable
sealed interface KeyValue : Value

@Serializable
@SerialName("TableEntry")
data class TableEntry(val key: Value, val value: Value)

@SerialName("TableValue")
@Serializable
data class TableValue(
    @Serializable(with = Serializer::class)
    private val entries: MutableMap<Value, Value> = mutableMapOf(),
    @Transient
    override val metaTable: MetaTable = DefaultTableMetaTable
) : Value, Collection<TableEntry> {

    constructor(vararg pairs: Pair<Value, Value>) : this() {
        pairs.forEach { pair -> entries[pair.first] = pair.second }
    }

    constructor(map: Map<Value, Value>) : this() {
        map.forEach { (key, value) -> entries[key] = value }
    }

    operator fun set(key: Int, value: Value) {
        this[NumberValue(key)] = value
    }

    operator fun set(key: Value, value: Value) {
        entries[key] = value
    }

    operator fun get(key: Int): Value = entries[NumberValue(key)] ?: NilValue
    operator fun get(key: Value): Value = entries[key] ?: NilValue

    fun remove(key: Int) {
        remove(NumberValue(key))
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

    object Serializer : KSerializer<MutableMap<Value, Value>> {
        private val delegate = MapSerializer(KeySerializer, Value.serializer())

        @OptIn(ExperimentalSerializationApi::class)
        override val descriptor = SerialDescriptor("TableValue", delegate.descriptor)

        override fun deserialize(decoder: Decoder): MutableMap<Value, Value> {
            return delegate.deserialize(decoder).toMutableMap()
        }

        override fun serialize(encoder: Encoder, value: MutableMap<Value, Value>) {
            return delegate.serialize(encoder, value)
        }
    }

    object KeySerializer : KSerializer<Value> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("KeyValue", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): Value {
            val str = decoder.decodeString()
            return when {
                str.startsWith("s_") -> StringValue(str.substring(2))
                str.startsWith("n_") -> NumberValue(str.substring(2))
                else -> throw IllegalArgumentException("Only StringValue and NumberValue are supported")
            }
        }

        override fun serialize(encoder: Encoder, value: Value) {
            when (value) {
                is StringValue -> encoder.encodeString("s_${value.value}")
                is NumberValue -> encoder.encodeString("n_${value.value}")
                else -> TODO()
            }
        }

    }
}

object DefaultTableMetaTable : MetaTable {
    override val type = "table"
    override val operators: List<ValueOperator> = listOf()
}

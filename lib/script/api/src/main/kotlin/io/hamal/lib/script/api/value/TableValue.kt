package io.hamal.lib.script.api.value

import kotlinx.serialization.*
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


//@Serializable
//sealed interface TableKeyValue : Value

@Serializable
@SerialName("TableEntry")
data class TableEntry(val key: IdentValue, val value: Value)

@SerialName("TableValue")
@Serializable
data class TableValue(
    @Serializable(with = Serializer::class)
    private val entries: MutableMap<IdentValue, Value> = mutableMapOf(),
    @Transient
    override val metaTable: MetaTable = DefaultTableValueMetaTable
) : Value, Collection<TableEntry> {
    constructor(vararg pairs: Pair<Any, Value>) : this() {
        pairs.forEach { pair ->
            val key = pair.first
            if (key is String) {
                entries[IdentValue(key)] = pair.second
            } else if (key is Number) {
                entries[IdentValue(key.toString())] = pair.second
            } else if (key is IdentValue) {
                entries[key] = pair.second
            }
        }
    }

    constructor(map: Map<IdentValue, Value>) : this() {
        map.forEach { (key, value) ->
            entries[key] = value
        }
    }

    operator fun set(key: Int, value: Value) {
        this[IdentValue(key.toString())] = value
    }

    operator fun set(key: IdentValue, value: Value) {
        entries[key] = value
    }

    operator fun get(key: Int): Value = get(IdentValue(key.toString()))
    operator fun get(key: String): Value = get(IdentValue(key))
    operator fun get(key: Value): Value = when (key) {
        is StringValue -> get(IdentValue(key.value))
        is NumberValue -> get(IdentValue(key.value.toString()))
        is IdentValue -> get(key)
        else -> TODO()
    }

    operator fun get(key: IdentValue): Value = entries[key] ?: NilValue


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

    object Serializer : KSerializer<MutableMap<IdentValue, Value>> {
        private val delegate = MapSerializer(KeySerializer, Value.serializer())

        @OptIn(ExperimentalSerializationApi::class)
        override val descriptor = SerialDescriptor("TableValue", delegate.descriptor)

        override fun deserialize(decoder: Decoder): MutableMap<IdentValue, Value> {
            return delegate.deserialize(decoder).toMutableMap()
        }

        override fun serialize(encoder: Encoder, value: MutableMap<IdentValue, Value>) {
            return delegate.serialize(encoder, value)
        }
    }

    object KeySerializer : KSerializer<IdentValue> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("KeyValue", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): IdentValue {
            return IdentValue(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: IdentValue) {
            return encoder.encodeString(value.value)
        }

    }
}

object DefaultTableValueMetaTable : MetaTable {
    override val type = "table"
    override val operators: List<ValueOperator> = listOf()
}

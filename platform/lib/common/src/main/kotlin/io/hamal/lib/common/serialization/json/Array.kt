package io.hamal.lib.common.serialization.json

import io.hamal.lib.common.Decimal
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*


@JvmInline
value class JsonArray(
    val nodes: List<JsonNode<*>>
) : JsonNode<JsonArray>, Collection<JsonNode<*>> {

    override val size get() : Int = nodes.size
    override fun isEmpty() = nodes.isEmpty()
    override fun iterator() = nodes.iterator()
    override fun containsAll(elements: Collection<JsonNode<*>>) = nodes.containsAll(elements)
    override fun contains(element: JsonNode<*>) = nodes.contains(element)

    override val isArray get(): Boolean = true

    fun find(idx: Int): JsonNode<*>? = if (idx >= 0 && idx < nodes.size) {
        nodes[idx]
    } else {
        null
    }

    operator fun get(idx: Int): JsonNode<*> =
        find(idx) ?: throw NoSuchElementException("Element at index $idx not found")

    fun isArray(idx: Int): Boolean = find(idx)?.isArray ?: false
    override fun asArray(): JsonArray = this
    fun asArray(idx: Int): JsonArray = find(idx)
        ?.let { if (it.isArray) it as JsonArray else null }
        ?: throw IllegalStateException("Not JsonArray")

    fun isBoolean(idx: Int): Boolean = find(idx)?.isBoolean ?: false
    fun asBoolean(idx: Int): JsonBoolean = find(idx)
        ?.let { if (it.isBoolean) it as JsonBoolean else null }
        ?: throw IllegalStateException("Not JsonBoolean")

    fun booleanValue(idx: Int): Boolean = asBoolean(idx).value

    fun isNumber(idx: Int): Boolean = find(idx)?.isNumber ?: false
    fun asNumber(idx: Int): JsonNumber = find(idx)
        ?.let { if (it.isNumber) it as JsonNumber else null }
        ?: throw IllegalStateException("Not JsonNumber")

    fun decimalValue(idx: Int): Decimal = asNumber(idx).decimalValue
    fun byteValue(idx: Int): Byte = asNumber(idx).byteValue
    fun doubleValue(idx: Int): Double = asNumber(idx).doubleValue
    fun floatValue(idx: Int): Float = asNumber(idx).floatValue
    fun intValue(idx: Int): Int = asNumber(idx).intValue
    fun longValue(idx: Int): Long = asNumber(idx).longValue
    fun shortValue(idx: Int): Short = asNumber(idx).shortValue

    fun isNull(idx: Int): Boolean = find(idx)?.isNull ?: true
    fun asNull(idx: Int): JsonNull = find(idx)
        ?.let { if (it.isNull) it as JsonNull else throw IllegalStateException("Not JsonNull") }
        ?: JsonNull

    fun isObject(idx: Int): Boolean = find(idx)?.isObject ?: false
    fun asObject(idx: Int): JsonObject = find(idx)
        ?.let { if (it.isObject) it as JsonObject else null }
        ?: throw IllegalStateException("Not JsonObject")

    fun isString(idx: Int): Boolean = find(idx)?.isString ?: false
    fun asString(idx: Int): JsonString = find(idx)
        ?.let { if (it.isString) it as JsonString else null }
        ?: throw IllegalStateException("Not JsonString")

    fun stringValue(idx: Int): String = asString(idx).stringValue

    fun isPrimitive(idx: Int): Boolean = find(idx)?.isPrimitive ?: false
    fun asPrimitive(idx: Int): JsonPrimitive<*> = find(idx)
        ?.let { if (it.isPrimitive) it as JsonPrimitive else null }
        ?: throw IllegalStateException("Not JsonPrimitive")

    override fun deepCopy(): JsonArray {
        val builder = builder()
        nodes.forEach { node -> builder.append(node.deepCopy()) }
        return builder.build()
    }

    companion object {
        val empty = JsonArray(LinkedList())

        fun builder() = JsonArrayBuilder()
    }

}

class JsonArrayBuilder {

    fun append(value: String): JsonArrayBuilder {
        nodes.add(JsonString(value))
        return this
    }

    fun append(value: Byte): JsonArrayBuilder {
        nodes.add(JsonNumber(value))
        return this
    }

    fun append(value: Short): JsonArrayBuilder {
        nodes.add(JsonNumber(value))
        return this
    }

    fun append(value: BigInteger): JsonArrayBuilder {
        nodes.add(JsonNumber(value))
        return this
    }

    fun append(value: BigDecimal): JsonArrayBuilder {
        nodes.add(JsonNumber(value))
        return this
    }

    fun append(value: Int): JsonArrayBuilder {
        nodes.add(JsonNumber(value))
        return this
    }

    fun append(value: Float): JsonArrayBuilder {
        nodes.add(JsonNumber(value))
        return this
    }

    fun append(value: Double): JsonArrayBuilder {
        nodes.add(JsonNumber(value))
        return this
    }

    fun append(value: Long): JsonArrayBuilder {
        nodes.add(JsonNumber(value))
        return this
    }

    fun append(value: Boolean): JsonArrayBuilder {
        nodes.add(JsonBoolean(value))
        return this
    }

    fun append(value: JsonNode<*>): JsonArrayBuilder {
        nodes.add(value)
        return this
    }

    fun appendNull() = append(JsonNull)

    fun build(): JsonArray {
        return JsonArray(nodes)
    }

    private val nodes: MutableList<JsonNode<*>> = LinkedList()

}

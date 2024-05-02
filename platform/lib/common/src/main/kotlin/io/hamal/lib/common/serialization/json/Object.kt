package io.hamal.lib.common.serialization.json

import io.hamal.lib.common.Decimal
import java.math.BigDecimal
import java.math.BigInteger

@JvmInline
value class JsonObject(
    val nodes: LinkedHashMap<String, JsonNode<*>>
) : JsonNode<JsonObject> {

    override val isObject get()  : Boolean = true

    val size get() : Int = nodes.size

    fun containsKey(key: String): Boolean = nodes.containsKey(key)

    fun find(key: String): JsonNode<*>? = nodes[key]

    operator fun get(key: String): JsonNode<*> = find(key) ?: throw NoSuchElementException("$key not found")

    fun isArray(key: String): Boolean = find(key)?.isArray ?: false
    fun asArray(key: String): JsonArray = find(key)
        ?.let { if (it.isArray) it as JsonArray else null }
        ?: throw IllegalStateException("Not HotArray")

    fun isBoolean(key: String): Boolean = find(key)?.isBoolean ?: false
    fun asBoolean(key: String): JsonBoolean = find(key)
        ?.let { if (it.isBoolean) it as JsonBoolean else null }
        ?: throw IllegalStateException("Not HotBoolean")

    fun booleanValue(key: String): Boolean = asBoolean(key).value

    fun isNumber(key: String): Boolean = find(key)?.isNumber ?: false
    fun asNumber(key: String): JsonNumber = find(key)
        ?.let { if (it.isNumber) it as JsonNumber else null }
        ?: throw IllegalStateException("Not HotNumber")

    fun decimalValue(key: String): Decimal = asNumber(key).decimalValue
    fun byteValue(key: String): Byte = asNumber(key).byteValue
    fun doubleValue(key: String): Double = asNumber(key).doubleValue
    fun floatValue(key: String): Float = asNumber(key).floatValue
    fun intValue(key: String): Int = asNumber(key).intValue
    fun longValue(key: String): Long = asNumber(key).longValue
    fun shortValue(key: String): Short = asNumber(key).shortValue

    fun isNull(key: String): Boolean = find(key)?.isNull ?: true
    fun asNull(key: String): JsonNull = find(key)
        ?.let { if (it.isNull) it as JsonNull else throw IllegalStateException("Not HotNull") }
        ?: JsonNull

    fun isObject(key: String): Boolean = find(key)?.isObject ?: false
    override fun asObject(): JsonObject = this
    fun asObject(key: String): JsonObject = find(key)
        ?.let { if (it.isObject) it as JsonObject else null }
        ?: throw IllegalStateException("Not HotObject")

    fun isString(key: String): Boolean = find(key)?.isString ?: false
    fun asString(key: String): JsonString = find(key)
        ?.let { if (it.isString) it as JsonString else null }
        ?: throw IllegalStateException("Not HotString")

    fun stringValue(key: String): String = asString(key).stringValue

    fun isPrimitive(key: String): Boolean = find(key)?.isPrimitive ?: false
    fun asPrimitive(key: String): JsonPrimitive<*> = find(key)
        ?.let { if (it.isPrimitive) it as JsonPrimitive else null }
        ?: throw IllegalStateException("Not HotTerminal")

    override fun deepCopy(): JsonObject {
        val builder = builder()
        nodes.forEach { (key, value) -> builder[key] = value.deepCopy() }
        return builder.build()
    }

    companion object {
        val empty = JsonObject(LinkedHashMap())

        fun builder() = JsonObjectBuilder()
    }
}

class JsonObjectBuilder {
    val nodes: LinkedHashMap<String, JsonNode<*>> = LinkedHashMap()

    operator fun set(key: String, value: String): JsonObjectBuilder {
        nodes[key] = JsonString(value)
        return this
    }

    operator fun set(key: String, value: Byte): JsonObjectBuilder {
        nodes[key] = JsonNumber(value)
        return this
    }

    operator fun set(key: String, value: Short): JsonObjectBuilder {
        nodes[key] = JsonNumber(value)
        return this
    }

    operator fun set(key: String, value: BigInteger): JsonObjectBuilder {
        nodes[key] = JsonNumber(value)
        return this
    }

    operator fun set(key: String, value: BigDecimal): JsonObjectBuilder {
        nodes[key] = JsonNumber(value)
        return this
    }

    operator fun set(key: String, value: Int): JsonObjectBuilder {
        nodes[key] = JsonNumber(value)
        return this
    }

    operator fun set(key: String, value: Float): JsonObjectBuilder {
        nodes[key] = JsonNumber(value)
        return this
    }

    operator fun set(key: String, value: Double): JsonObjectBuilder {
        nodes[key] = JsonNumber(value)
        return this
    }

    operator fun set(key: String, value: Long): JsonObjectBuilder {
        nodes[key] = JsonNumber(value)
        return this
    }

    operator fun set(key: String, value: Boolean): JsonObjectBuilder {
        nodes[key] = JsonBoolean(value)
        return this
    }

    fun setNull(key: String): JsonObjectBuilder {
        nodes[key] = JsonNull
        return this
    }

    operator fun set(key: String, value: JsonNode<*>): JsonObjectBuilder {
        nodes[key] = value
        return this
    }

    operator fun set(key: String, value: Enum<*>): JsonObjectBuilder {
        nodes[key] = JsonString(value.name)
        return this
    }

    fun build(): JsonObject {
        return JsonObject(nodes)
    }
}

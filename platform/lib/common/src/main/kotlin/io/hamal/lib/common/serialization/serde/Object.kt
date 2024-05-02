package io.hamal.lib.common.serialization.serde

import io.hamal.lib.common.Decimal
import java.math.BigDecimal
import java.math.BigInteger

@JvmInline
value class SerdeObject(
    val nodes: LinkedHashMap<kotlin.String, SerdeNode<*>>
) : SerdeNode<SerdeObject> {

    override val isObject get()  : kotlin.Boolean = true

    val size get() : Int = nodes.size

    fun containsKey(key: kotlin.String): kotlin.Boolean = nodes.containsKey(key)

    fun find(key: kotlin.String): SerdeNode<*>? = nodes[key]

    operator fun get(key: kotlin.String): SerdeNode<*> = find(key) ?: throw NoSuchElementException("$key not found")

    fun isArray(key: kotlin.String): kotlin.Boolean = find(key)?.isArray ?: false
    fun asArray(key: kotlin.String): SerdeArray = find(key)
        ?.let { if (it.isArray) it as SerdeArray else null }
        ?: throw IllegalStateException("Not HotArray")

    fun isBoolean(key: kotlin.String): kotlin.Boolean = find(key)?.isBoolean ?: false
    fun asBoolean(key: kotlin.String): SerdeBoolean = find(key)
        ?.let { if (it.isBoolean) it as SerdeBoolean else null }
        ?: throw IllegalStateException("Not HotBoolean")

    fun booleanValue(key: kotlin.String): kotlin.Boolean = asBoolean(key).value

    fun isNumber(key: kotlin.String): kotlin.Boolean = find(key)?.isNumber ?: false
    fun asNumber(key: kotlin.String): SerdeNumber = find(key)
        ?.let { if (it.isNumber) it as SerdeNumber else null }
        ?: throw IllegalStateException("Not HotNumber")

    fun decimalValue(key: kotlin.String): Decimal = asNumber(key).decimalValue
    fun byteValue(key: kotlin.String): Byte = asNumber(key).byteValue
    fun doubleValue(key: kotlin.String): Double = asNumber(key).doubleValue
    fun floatValue(key: kotlin.String): Float = asNumber(key).floatValue
    fun intValue(key: kotlin.String): Int = asNumber(key).intValue
    fun longValue(key: kotlin.String): Long = asNumber(key).longValue
    fun shortValue(key: kotlin.String): Short = asNumber(key).shortValue

    fun isNull(key: kotlin.String): kotlin.Boolean = find(key)?.isNull ?: true
    fun asNull(key: kotlin.String): SerdeNull = find(key)
        ?.let { if (it.isNull) it as SerdeNull else throw IllegalStateException("Not HotNull") }
        ?: SerdeNull

    fun isObject(key: kotlin.String): kotlin.Boolean = find(key)?.isObject ?: false
    override fun asObject(): SerdeObject = this
    fun asObject(key: kotlin.String): SerdeObject = find(key)
        ?.let { if (it.isObject) it as SerdeObject else null }
        ?: throw IllegalStateException("Not HotObject")

    fun isString(key: kotlin.String): kotlin.Boolean = find(key)?.isString ?: false
    fun asString(key: kotlin.String): SerdeString = find(key)
        ?.let { if (it.isString) it as SerdeString else null }
        ?: throw IllegalStateException("Not HotString")

    fun stringValue(key: kotlin.String): kotlin.String = asString(key).stringValue

    fun isTerminal(key: kotlin.String): kotlin.Boolean = find(key)?.isPrimitive ?: false
    fun asTerminal(key: kotlin.String): SerdePrimitive<*> = find(key)
        ?.let { if (it.isPrimitive) it as SerdePrimitive else null }
        ?: throw IllegalStateException("Not HotTerminal")

    override fun deepCopy(): SerdeObject {
        val builder = builder()
        nodes.forEach { (key, value) -> builder[key] = value.deepCopy() }
        return builder.build()
    }

    companion object {
        val empty = SerdeObject(LinkedHashMap())

        fun builder() = HotObjectBuilder()
    }
}

class HotObjectBuilder {
    val nodes: LinkedHashMap<kotlin.String, SerdeNode<*>> = LinkedHashMap()

    operator fun set(key: kotlin.String, value: kotlin.String): HotObjectBuilder {
        nodes[key] = SerdeString(value)
        return this
    }

    operator fun set(key: kotlin.String, value: Byte): HotObjectBuilder {
        nodes[key] = SerdeNumber(value)
        return this
    }

    operator fun set(key: kotlin.String, value: Short): HotObjectBuilder {
        nodes[key] = SerdeNumber(value)
        return this
    }

    operator fun set(key: kotlin.String, value: BigInteger): HotObjectBuilder {
        nodes[key] = SerdeNumber(value)
        return this
    }

    operator fun set(key: kotlin.String, value: BigDecimal): HotObjectBuilder {
        nodes[key] = SerdeNumber(value)
        return this
    }

    operator fun set(key: kotlin.String, value: Int): HotObjectBuilder {
        nodes[key] = SerdeNumber(value)
        return this
    }

    operator fun set(key: kotlin.String, value: Float): HotObjectBuilder {
        nodes[key] = SerdeNumber(value)
        return this
    }

    operator fun set(key: kotlin.String, value: Double): HotObjectBuilder {
        nodes[key] = SerdeNumber(value)
        return this
    }

    operator fun set(key: kotlin.String, value: Long): HotObjectBuilder {
        nodes[key] = SerdeNumber(value)
        return this
    }

    operator fun set(key: kotlin.String, value: kotlin.Boolean): HotObjectBuilder {
        nodes[key] = SerdeBoolean(value)
        return this
    }

    fun setNull(key: kotlin.String): HotObjectBuilder {
        nodes[key] = SerdeNull
        return this
    }

    operator fun set(key: kotlin.String, value: SerdeNode<*>): HotObjectBuilder {
        nodes[key] = value
        return this
    }

    operator fun set(key: kotlin.String, value: Enum<*>): HotObjectBuilder {
        nodes[key] = SerdeString(value.name)
        return this
    }

    fun build(): SerdeObject {
        return SerdeObject(nodes)
    }
}

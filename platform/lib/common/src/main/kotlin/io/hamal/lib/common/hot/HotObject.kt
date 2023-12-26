package io.hamal.lib.common.hot

import java.math.BigDecimal
import java.math.BigInteger

class HotObject(
    val nodes: LinkedHashMap<String, HotNode>
) : HotNode {

    override val isObject get()  : Boolean = true

    val size get() : Int = TODO()

    fun containsKey(key: String): Boolean = nodes.containsKey(key)

    fun find(key: String): HotNode? = nodes[key]

    fun get(key: String): HotNode = find(key) ?: throw NoSuchElementException("$key not found")

    fun isArray(key: String): Boolean = find(key)?.isArray ?: false
    fun asArray(key: String): HotArray = throw IllegalStateException("Not HotArray")

    fun isBoolean(key: String): Boolean = find(key)?.isBoolean ?: false
    fun asBoolean(key: String): HotBoolean = throw IllegalStateException("Not HotBoolean")
    fun booleanValue(key: String): Boolean = throw IllegalStateException("Not boolean")

    fun isNumber(key: String): Boolean = find(key)?.isNumber ?: false
    fun asNumber(key: String): HotNumber = throw IllegalStateException("Not HotNumber")
    fun bigDecimalValue(key: String): BigDecimal = throw IllegalStateException("Not BigDecimal")
    fun bigIntegerValue(key: String): BigInteger = throw IllegalStateException("Not BigInteger")
    fun byteValue(key: String): Byte = throw IllegalStateException("Not byte")
    fun doubleValue(key: String): Double = throw IllegalStateException("Not double")
    fun floatValue(key: String): Float = throw IllegalStateException("Not float")
    fun intValue(key: String): Int = throw IllegalStateException("Not int")
    fun longValue(key: String): Long = throw IllegalStateException("Not long")
    fun numberValue(key: String): Number = throw IllegalStateException("Not number")
    fun shortValue(key: String): Short = throw IllegalStateException("Not short")

    fun isNull(key: String): Boolean = find(key)?.isNull ?: true
    fun asNull(key: String): HotNull = throw IllegalStateException("Not HotNull")

    fun isObject(key: String): Boolean = find(key)?.isObject ?: false
    fun asObject(key: String): HotObject = throw IllegalStateException("Not HotObject")

    fun isString(key: String): Boolean = find(key)?.isString ?: false
    fun asString(key: String): HotString = throw IllegalStateException("Not HotString")
    fun stringValue(key: String): String = throw IllegalStateException("Not string")

    fun isTerminal(key: String): Boolean = find(key)?.isTerminal ?: false
    fun asTerminal(key: String): HotTerminal = throw IllegalStateException("Not HotTerminal")

    override fun deepCopy(): HotNode {
        TODO("Not yet implemented")
    }

    companion object {
        val empty = HotObject(LinkedHashMap())

        fun builder() = HotObjectBuilder()
    }
}

class HotObjectBuilder {
    val nodes: LinkedHashMap<String, HotNode> = LinkedHashMap()

    fun set(key: String, value: String): HotObjectBuilder {
        nodes[key] = HotString(value)
        return this
    }

    fun set(key: String, value: Byte): HotObjectBuilder {
        nodes[key] = HotNumber(value)
        return this
    }

    fun set(key: String, value: Short): HotObjectBuilder {
        nodes[key] = HotNumber(value)
        return this
    }

    fun set(key: String, value: BigInteger): HotObjectBuilder {
        nodes[key] = HotNumber(value)
        return this
    }

    fun set(key: String, value: BigDecimal): HotObjectBuilder {
        nodes[key] = HotNumber(value)
        return this
    }

    fun set(key: String, value: Int): HotObjectBuilder {
        nodes[key] = HotNumber(value)
        return this
    }

    fun set(key: String, value: Float): HotObjectBuilder {
        nodes[key] = HotNumber(value)
        return this
    }

    fun set(key: String, value: Double): HotObjectBuilder {
        nodes[key] = HotNumber(value)
        return this
    }

    fun set(key: String, value: Long): HotObjectBuilder {
        nodes[key] = HotNumber(value)
        return this
    }

    fun set(key: String, value: Boolean): HotObjectBuilder {
        nodes[key] = HotBoolean(value)
        return this
    }

    fun setNull(key: String): HotObjectBuilder {
        nodes[key] = HotNull
        return this
    }

    fun set(key: String, value: HotNode): HotObjectBuilder {
        nodes[key] = value
        return this
    }

    fun set(key: String, value: Enum<*>): HotObjectBuilder {
        nodes[key] = HotString(value.name)
        return this
    }

    fun build(): HotObject {
        return HotObject(nodes)
    }
}

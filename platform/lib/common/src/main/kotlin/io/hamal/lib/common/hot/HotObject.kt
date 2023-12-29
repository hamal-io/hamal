package io.hamal.lib.common.hot

import java.math.BigDecimal
import java.math.BigInteger

class HotObject(
    val nodes: LinkedHashMap<String, HotNode>
) : HotNode {

    override val isObject get()  : Boolean = true

    val size get() : Int = nodes.size

    fun containsKey(key: String): Boolean = nodes.containsKey(key)

    fun find(key: String): HotNode? = nodes[key]

    fun get(key: String): HotNode = find(key) ?: throw NoSuchElementException("$key not found")

    fun isArray(key: String): Boolean = find(key)?.isArray ?: false
    fun asArray(key: String): HotArray = find(key)
        ?.let { if (it.isArray) it as HotArray else null }
        ?: throw IllegalStateException("Not HotArray")

    fun isBoolean(key: String): Boolean = find(key)?.isBoolean ?: false
    fun asBoolean(key: String): HotBoolean = find(key)
        ?.let { if (it.isBoolean) it as HotBoolean else null }
        ?: throw IllegalStateException("Not HotBoolean")

    fun booleanValue(key: String): Boolean = asBoolean(key).value

    fun isNumber(key: String): Boolean = find(key)?.isNumber ?: false
    fun asNumber(key: String): HotNumber = find(key)
        ?.let { if (it.isNumber) it as HotNumber else null }
        ?: throw IllegalStateException("Not HotNumber")

    fun bigDecimalValue(key: String): BigDecimal = asNumber(key).bigDecimalValue
    fun bigIntegerValue(key: String): BigInteger = asNumber(key).bigIntegerValue
    fun byteValue(key: String): Byte = asNumber(key).byteValue
    fun doubleValue(key: String): Double = asNumber(key).doubleValue
    fun floatValue(key: String): Float = asNumber(key).floatValue
    fun intValue(key: String): Int = asNumber(key).intValue
    fun longValue(key: String): Long = asNumber(key).longValue
    fun shortValue(key: String): Short = asNumber(key).shortValue

    fun isNull(key: String): Boolean = find(key)?.isNull ?: true
    fun asNull(key: String): HotNull = find(key)
        ?.let { if (it.isNull) it as HotNull else throw IllegalStateException("Not HotNull") }
        ?: HotNull

    fun isObject(key: String): Boolean = find(key)?.isObject ?: false
    fun asObject(key: String): HotObject = find(key)
        ?.let { if (it.isObject) it as HotObject else null }
        ?: throw IllegalStateException("Not HotObject")

    fun isString(key: String): Boolean = find(key)?.isString ?: false
    fun asString(key: String): HotString = find(key)
        ?.let { if (it.isString) it as HotString else null }
        ?: throw IllegalStateException("Not HotString")

    fun stringValue(key: String): String = asString(key).stringValue

    fun isTerminal(key: String): Boolean = find(key)?.isTerminal ?: false
    fun asTerminal(key: String): HotTerminal = find(key)
        ?.let { if (it.isTerminal) it as HotTerminal else null }
        ?: throw IllegalStateException("Not HotTerminal")

    override fun deepCopy(): HotNode {
        val builder = builder()
        nodes.forEach { (key, value) ->
            builder.set(key, value.deepCopy())
        }
        return builder.build()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HotObject

        return nodes == other.nodes
    }

    override fun hashCode(): Int {
        return nodes.hashCode()
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

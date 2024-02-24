package io.hamal.lib.kua.type

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.serialization.GsonTransform
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.common.snowflake.SnowflakeId
import java.lang.reflect.Type
import kotlin.reflect.KClass

data class KuaTable(
    val value: MutableMap<String, KuaType> = mutableMapOf(),
) : KuaTableType, Map<String, KuaType> {

    override val type: KuaType.Type = KuaType.Type.Map

    constructor(vararg pairs: Pair<String, KuaType>) : this(mutableMapOf(*pairs))

    override val size get() = value.size

    @Transient
    override val entries: Set<Map.Entry<String, KuaType>> = value.entries

    @Transient
    override val keys: Set<String> = value.keys

    @Transient
    override val values: Collection<KuaType> = value.values
    override fun containsKey(key: String): Boolean = this.value.containsKey(key)

    override fun isEmpty(): Boolean = value.isEmpty()
    override fun containsValue(value: KuaType): Boolean = this.value.containsValue(value)

    override operator fun get(key: String): KuaType = value[key] ?: KuaNil
    operator fun set(key: String, value: KuaType): Int {
        this.value[key] = value
        return size
    }

    operator fun set(key: String, value: KuaAny) = set(key, value.value)

    fun unset(key: KuaString) = unset(key.value)

    @Suppress("UNUSED_PARAMETER")
    operator fun set(key: String, value: KuaNil) = unset(key)

    @Suppress("UNUSED_PARAMETER")
    operator fun set(key: KuaString, value: KuaNil) = unset(key.value)
    fun unset(key: String): Int {
        value.remove(key)
        return size
    }

//    fun getArrayType(key: KuaString): KuaArray = getArrayType(key.value)
//    fun getArrayType(key: String): KuaArray {
//        checkExpectedType(key, KuaArray::class)
//        return value[key]!! as KuaArray
//    }

    fun getBooleanValue(key: KuaString) = getBooleanValue(key.value)
    fun getBoolean(key: String): Boolean = getBooleanValue(key).value
    fun getBoolean(key: KuaString): Boolean = getBoolean(key.value)
    fun getBooleanValue(key: String): KuaBoolean {
        val type = type(key)
        check(type == KuaTrue::class || type == KuaFalse::class) {
            "Expected type to be boolean but was $type"
        }
        return value[key]!! as KuaBoolean
    }

    operator fun set(key: KuaString, value: KuaBoolean) = set(key, value.value)
    operator fun set(key: KuaString, value: Boolean) = set(key.value, booleanOf(value))
    operator fun set(key: String, value: Boolean) = set(key, booleanOf(value))
    operator fun set(key: String, value: KuaBoolean): Int {
        this.value[key] = value
        return size
    }

    fun getCodeType(key: String) = KuaCode(getString(key))
    fun getCodeType(key: KuaString) = getCodeType(key.value)
    operator fun set(key: KuaString, value: KuaCode) = set(key.value, value)
    operator fun set(key: String, value: KuaCode): Int {
        this.value[key] = KuaString(value.value)
        return size
    }

    operator fun set(key: String, value: ValueObjectId) = set(key, value.value.value.toString(16))
    operator fun set(key: KuaString, value: ValueObjectId) = set(key.value, value.value.value.toString(16))
    operator fun set(key: String, value: SnowflakeId) = set(key, value.value.toString(16))
    operator fun set(key: KuaString, value: SnowflakeId) = set(key.value, value.value.toString(16))

    fun getInt(key: String): Int = getNumberValue(key).value.toInt()
    fun getInt(key: KuaString) = getInt(key.value)
    fun getLong(key: String): Long = getNumberValue(key).value.toLong()
    fun getLong(key: KuaString): Long = getLong(key.value)
    fun getFloat(key: String): Float = getNumberValue(key).value.toFloat()
    fun getFloat(key: KuaString): Float = getFloat(key.value)
    fun getDouble(key: String): Double = getNumberValue(key).value
    fun getDouble(key: KuaString): Double = getDouble(key.value)
    fun getNumberValue(key: KuaString): KuaNumber = getNumberValue(key.value)
    fun getNumberValue(key: String): KuaNumber {
        checkExpectedType(key, KuaNumber::class)
        return value[key]!! as KuaNumber
    }

    operator fun set(key: String, value: Int) = set(key, value.toDouble())
    operator fun set(key: String, value: Long) = set(key, value.toDouble())
    operator fun set(key: String, value: Float) = set(key, value.toDouble())
    operator fun set(key: String, value: Double) = set(key, KuaNumber(value))
    operator fun set(key: KuaString, value: KuaNumber) = set(key.value, value.value)
    operator fun set(key: String, value: KuaNumber): Int {
        this.value[key] = value
        return size
    }

    operator fun set(key: String, value: KuaTable): Int {
        this.value[key] = value
        return size
    }

    fun getStringType(key: KuaString) = getStringType(key.value)
    fun getString(key: String): String = getStringType(key).value
    fun getString(key: KuaString): String = getString(key.value)
    fun getStringType(key: String): KuaString {
        checkExpectedType(key, KuaString::class)
        return value[key]!! as KuaString
    }


    fun findStringType(key: KuaString) = findStringType(key.value)
    fun findString(key: String): String? = findStringType(key)?.value
    fun findString(key: KuaString): String? = findString(key.value)
    fun findStringType(key: String): KuaString? {
        if (isNull(key)) {
            return null
        }
        checkExpectedType(key, KuaString::class)
        return value[key]!! as KuaString
    }


    operator fun set(key: String, value: String) = set(key, KuaString(value))
    operator fun set(key: KuaString, value: KuaString) = set(key.value, value.value)
    operator fun set(key: String, value: KuaString): Int {
        this.value[key] = value
        return size
    }

    fun type(key: String): KClass<out KuaType> {
        return value[key]?.let { it::class } ?: KuaNil::class
    }

    object Adapter : JsonAdapter<KuaTable> {
        override fun serialize(instance: KuaTable, type: Type, ctx: JsonSerializationContext): JsonElement {
            val valueBuilder = HotObject.builder()
            instance.forEach { (key, value) ->
                valueBuilder.set(key, GsonTransform.toNode(ctx.serialize(value)))
            }
            return ctx.serialize(
                HotObject.builder()
                    .set("value", valueBuilder.build())
                    .set("type", instance.type.name)
                    .build()
            )
        }

        override fun deserialize(element: JsonElement, type: Type, ctx: JsonDeserializationContext): KuaTable {
            val obj = element.asJsonObject.get("value").asJsonObject
            val map = mutableMapOf<String, KuaType>()
            obj.keySet().forEach { key ->
                map[key] = ctx.deserialize(obj.get(key), KuaType::class.java)
            }
            return KuaTable(map)
        }
    }
}

internal fun KuaTable.isNull(key: String) = type(key) == KuaNil::class

internal fun KuaTable.checkExpectedType(key: String, expected: KClass<out KuaType>) {
    check(type(key) == expected) {
        "Expected type to be $expected but was $this"
    }
}
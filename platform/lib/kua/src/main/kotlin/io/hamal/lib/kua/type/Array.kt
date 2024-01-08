package io.hamal.lib.kua.type

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.serialization.GsonSerde
import io.hamal.lib.common.serialization.GsonTransform
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.kua.type.KuaType.Type.Array
import java.lang.reflect.Type
import kotlin.reflect.KClass


data class KuaArray(
    val value: MutableMap<Int, KuaType> = mutableMapOf(),
) : KuaTableType, Map<Int, KuaType> {

    override val type: KuaType.Type = Array

    override val size get() = value.size

    @Transient
    override val entries: Set<Map.Entry<Int, KuaType>> = value.entries

    @Transient
    override val keys: Set<Int> = value.keys

    @Transient
    override val values: Collection<KuaType> = value.values
    override fun isEmpty(): Boolean = value.isEmpty()
    override fun containsValue(value: KuaType): Boolean = this.value.containsValue(value)
    override fun containsKey(key: Int): Boolean = this.containsKey(key)

    fun append(value: KuaArray): Int {
        this.value[this.value.size + 1] = value
        return size
    }

    fun append(value: KuaType): Int {
        this.value[this.value.size + 1] = value
        return size
    }

    fun append(value: KuaAny) = append(value.value)

    override operator fun get(key: Int): KuaType {
        return value[key]!!
    }

    fun getBoolean(idx: Int) = getBooleanValue(idx) == KuaTrue
    fun getBooleanValue(idx: Int): KuaBoolean {
        checkExpectedType(idx, KuaBoolean::class)
        return value[idx]!! as KuaBoolean
    }

    fun append(value: Boolean) = append(booleanOf(value))
    fun append(value: KuaBoolean): Int {
        this.value[this.value.size + 1] = value
        return size
    }


    fun getInt(idx: Int) = getNumberType(idx).value.toInt()
    fun getLong(idx: Int) = getNumberType(idx).value.toLong()
    fun getFloat(idx: Int) = getNumberType(idx).value.toFloat()
    fun getDouble(idx: Int) = getNumberType(idx).value.toDouble()
    fun getNumberType(idx: Int): KuaNumber {
        checkExpectedType(idx, KuaNumber::class)
        return value[idx]!! as KuaNumber
    }

    fun getDecimalType(idx: Int): KuaDecimal {
        checkExpectedType(idx, KuaDecimal::class)
        return value[idx]!! as KuaDecimal
    }


    fun append(value: Int) = append(value.toDouble())
    fun append(value: Long) = append(value.toDouble())
    fun append(value: Float) = append(value.toDouble())
    fun append(value: Double) = append(KuaNumber(value))
    fun append(value: KuaNumber): Int {
        this.value[this.value.size + 1] = value
        return size
    }

    fun append(value: KuaDecimal): Int {
        this.value[this.value.size + 1] = value
        return size
    }


    fun append(value: SnowflakeId) = append(value.value.toString(16))
    fun append(value: ValueObjectId) = append(value.value.value)

    fun append(value: KuaMap): Int {
        this.value[this.value.size + 1] = value
        return size
    }

    fun getMap(idx: Int): KuaMap {
        checkExpectedType(idx, KuaMap::class)
        return value[idx]!! as KuaMap
    }

    fun getArray(idx: Int): KuaArray {
        checkExpectedType(idx, KuaArray::class)
        return value[idx]!! as KuaArray
    }

    fun getString(idx: Int) = getStringType(idx).value
    fun getStringType(idx: Int): KuaString {
        checkExpectedType(idx, KuaString::class)
        return value[idx]!! as KuaString
    }

    fun append(value: String) = append(KuaString(value))
    fun append(value: KuaString): Int {
        this.value[this.value.size + 1] = value
        return size
    }

    fun getCodeType(idx: Int) = KuaCode(getString(idx))

    fun type(idx: Int): KClass<out KuaType> {
        return value[idx]?.let { it::class } ?: KuaNil::class
    }

    object Serde : GsonSerde<KuaArray> {
        override fun serialize(instance: KuaArray, type: Type, ctx: JsonSerializationContext): JsonElement {
            val valueBuilder = HotObject.builder()
            instance.forEach { (key, value) ->
                valueBuilder.set(key.toString(), GsonTransform.toNode(ctx.serialize(value)))
            }
            return ctx.serialize(
                HotObject.builder()
                    .set("value", valueBuilder.build())
                    .set("type", instance.type.name)
                    .build()
            )
        }

        override fun deserialize(element: JsonElement, type: Type, ctx: JsonDeserializationContext): KuaArray {
            val obj = element.asJsonObject.get("value").asJsonObject
            val map = mutableMapOf<Int, KuaType>()
            obj.keySet().forEach { key ->
                map[key.toInt()] = ctx.deserialize(obj.get(key), KuaType::class.java)
            }
            return KuaArray(map)
        }
    }
}

internal fun KuaArray.checkExpectedType(idx: Int, expected: KClass<out KuaType>) {
    check(type(idx) == expected) {
        "Expected type to be $expected but was $this"
    }
}
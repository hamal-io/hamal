package io.hamal.lib.common.hot

import kotlinx.serialization.json.JsonArray
import java.math.BigDecimal
import java.math.BigInteger

sealed interface HotNode {
    val isArray get(): Boolean = false
    fun asArray(): JsonArray = throw IllegalStateException("Not HotArray")

    val isBoolean get(): Boolean = false
    fun asBoolean(): HotBoolean = throw IllegalStateException("Not HotBoolean")
    val booleanValue get() : Boolean = throw IllegalStateException("Not boolean")

    val isNumber get(): Boolean = false
    fun asNumber(): HotNumber = throw IllegalStateException("Not HotNumber")
    val bigDecimalValue get() : BigDecimal = throw IllegalStateException("Not BigDecimal")
    val bigIntegerValue get() : BigInteger = throw IllegalStateException("Not BigInteger")
    val byteValue get() : Byte = throw IllegalStateException("Not byte")
    val doubleValue get() : Double = throw IllegalStateException("Not double")
    val floatValue get() : Float = throw IllegalStateException("Not float")
    val intValue get() : Int = throw IllegalStateException("Not int")
    val longValue get() : Long = throw IllegalStateException("Not long")
    val numberValue get() : Number = throw IllegalStateException("Not number")
    val shortValue get() : Short = throw IllegalStateException("Not short")

    val isNull get(): Boolean = false
    fun asNull(): HotNull = throw IllegalStateException("Not HotNull")

    val isObject get(): Boolean = false
    fun asObject(): HotObject = throw IllegalStateException("Not HotObject")

    val isString get(): Boolean = false
    fun asString(): HotString = throw IllegalStateException("Not HotString")
    val stringValue get(): String = throw IllegalStateException("Not string")

    val isTerminal get(): Boolean = false
    fun asTerminal(): HotTerminal = throw IllegalStateException("Not HotTerminal")

    fun deepCopy(): HotNode
}
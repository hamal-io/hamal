package io.hamal.lib.common.serialization.json

import io.hamal.lib.common.Decimal

sealed interface JsonNode<NODE_TYPE : JsonNode<NODE_TYPE>> {
    val isArray get(): Boolean = false
    fun asArray(): JsonArray = throw IllegalStateException("Not HotArray")

    val isBoolean get(): Boolean = false
    fun asBoolean(): JsonBoolean = throw IllegalStateException("Not HotBoolean")
    val booleanValue get() : Boolean = throw IllegalStateException("Not Boolean")

    val isNumber get(): Boolean = false
    fun asNumber(): JsonNumber = throw IllegalStateException("Not HotNumber")
    val decimalValue get() : Decimal = throw IllegalStateException("Not Decimal")
    val byteValue get() : Byte = throw IllegalStateException("Not Byte")
    val doubleValue get() : Double = throw IllegalStateException("Not Double")
    val floatValue get() : Float = throw IllegalStateException("Not Float")
    val intValue get() : Int = throw IllegalStateException("Not Int")
    val longValue get() : Long = throw IllegalStateException("Not Long")
    val numberValue get() : kotlin.Number = throw IllegalStateException("Not Number")
    val shortValue get() : Short = throw IllegalStateException("Not Short")

    val isNull get(): Boolean = false
    fun asNull(): JsonNull = throw IllegalStateException("Not HotNull")

    val isObject get(): Boolean = false
    fun asObject(): JsonObject = throw IllegalStateException("Not HotObject")

    val isString get(): Boolean = false
    fun asString(): JsonString = throw IllegalStateException("Not HotString")
    val stringValue get(): kotlin.String = throw IllegalStateException("Not String")

    val isPrimitive get(): Boolean = false
    fun asPrimitive(): JsonPrimitive<*> = throw IllegalStateException("Not HotTerminal")

    fun deepCopy(): NODE_TYPE
}
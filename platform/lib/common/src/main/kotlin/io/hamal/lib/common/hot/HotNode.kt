package io.hamal.lib.common.hot

import kotlinx.serialization.json.JsonArray

sealed interface HotNode {

    val isArray get(): Boolean = false
    fun asArray(): JsonArray = throw IllegalStateException("Not HotArray")

    val isBoolean get(): Boolean = false
    fun asBoolean(): HotBoolean = throw IllegalStateException("Not boolean")
    val booleanValue get() : Boolean = throw IllegalStateException("Not boolean")

    val isNumber get(): Boolean = false
    fun asNumber(): HotNumber = throw IllegalStateException("Not number")
    //  intValue, floatValue, doubleValue, ....

    val isNull get(): Boolean = false
    fun asNull(): HotNull = throw IllegalStateException("Not HotNull")

    val isObject get(): Boolean = false
    fun asObject(): HotObject = throw IllegalStateException("Not HotObject")

    val isText get(): Boolean = false
    val asText get() : HotText = throw IllegalStateException("Not HotText")
    val stringValue get(): String = throw IllegalStateException("Not string")

    val isTerminal get(): Boolean = false
    fun asTerminal(): HotTerminal = throw IllegalStateException("Not HotTerminal")

}
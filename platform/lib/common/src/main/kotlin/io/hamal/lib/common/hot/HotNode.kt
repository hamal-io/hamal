package io.hamal.lib.common.hot

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.Decimal
import io.hamal.lib.common.serialization.GsonTransform
import io.hamal.lib.common.serialization.JsonAdapter
import java.lang.reflect.Type

sealed interface HotNode<NODE_TYPE : HotNode<NODE_TYPE>> {
    val isArray get(): Boolean = false
    fun asArray(): HotArray = throw IllegalStateException("Not HotArray")

    val isBoolean get(): Boolean = false
    fun asBoolean(): HotBoolean = throw IllegalStateException("Not HotBoolean")
    val booleanValue get() : Boolean = throw IllegalStateException("Not Boolean")

    val isNumber get(): Boolean = false
    fun asNumber(): HotNumber = throw IllegalStateException("Not HotNumber")
    val decimalValue get() : Decimal = throw IllegalStateException("Not Decimal")
    val byteValue get() : Byte = throw IllegalStateException("Not Byte")
    val doubleValue get() : Double = throw IllegalStateException("Not Double")
    val floatValue get() : Float = throw IllegalStateException("Not Float")
    val intValue get() : Int = throw IllegalStateException("Not Int")
    val longValue get() : Long = throw IllegalStateException("Not Long")
    val numberValue get() : Number = throw IllegalStateException("Not Number")
    val shortValue get() : Short = throw IllegalStateException("Not Short")

    val isNull get(): Boolean = false
    fun asNull(): HotNull = throw IllegalStateException("Not HotNull")

    val isObject get(): Boolean = false
    fun asObject(): HotObject = throw IllegalStateException("Not HotObject")

    val isString get(): Boolean = false
    fun asString(): HotString = throw IllegalStateException("Not HotString")
    val stringValue get(): String = throw IllegalStateException("Not String")

    val isTerminal get(): Boolean = false
    fun asTerminal(): HotTerminal<*> = throw IllegalStateException("Not HotTerminal")

    fun deepCopy(): NODE_TYPE

    object Adapter : JsonAdapter<HotNode<*>> {
        override fun serialize(src: HotNode<*>, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(src)
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): HotNode<*> {
            return GsonTransform.toNode(json)
        }
    }
}
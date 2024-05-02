package io.hamal.lib.common.serialization.serde

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.Decimal
import io.hamal.lib.common.serialization.GsonTransform
import io.hamal.lib.common.serialization.JsonAdapter
import java.lang.reflect.Type

sealed interface SerdeNode<NODE_TYPE : SerdeNode<NODE_TYPE>> {
    val isArray get(): Boolean = false
    fun asArray(): SerdeArray = throw IllegalStateException("Not HotArray")

    val isBoolean get(): Boolean = false
    fun asBoolean(): SerdeBoolean = throw IllegalStateException("Not HotBoolean")
    val booleanValue get() : Boolean = throw IllegalStateException("Not Boolean")

    val isNumber get(): Boolean = false
    fun asNumber(): SerdeNumber = throw IllegalStateException("Not HotNumber")
    val decimalValue get() : Decimal = throw IllegalStateException("Not Decimal")
    val byteValue get() : Byte = throw IllegalStateException("Not Byte")
    val doubleValue get() : Double = throw IllegalStateException("Not Double")
    val floatValue get() : Float = throw IllegalStateException("Not Float")
    val intValue get() : Int = throw IllegalStateException("Not Int")
    val longValue get() : Long = throw IllegalStateException("Not Long")
    val numberValue get() : kotlin.Number = throw IllegalStateException("Not Number")
    val shortValue get() : Short = throw IllegalStateException("Not Short")

    val isNull get(): Boolean = false
    fun asNull(): SerdeNull = throw IllegalStateException("Not HotNull")

    val isObject get(): Boolean = false
    fun asObject(): SerdeObject = throw IllegalStateException("Not HotObject")

    val isString get(): Boolean = false
    fun asString(): SerdeString = throw IllegalStateException("Not HotString")
    val stringValue get(): kotlin.String = throw IllegalStateException("Not String")

    val isPrimitive get(): Boolean = false
    fun asPrimitive(): SerdePrimitive<*> = throw IllegalStateException("Not HotTerminal")

    fun deepCopy(): NODE_TYPE

    object Adapter : JsonAdapter<SerdeNode<*>> {
        override fun serialize(src: SerdeNode<*>, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(src)
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): SerdeNode<*> {
            return GsonTransform.toNode(json)
        }
    }
}
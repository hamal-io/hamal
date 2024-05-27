package io.hamal.lib.nodes

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.AdapterGeneric

// Object(xyz:Object(a:Number,b:number),list:List[string],object_list:List[Object(x:boolean, y:string)])
interface Form {

    data object Boolean : Form {}
    data object Number : Form
    data object String : Form

    data class Object(val fields: LinkedHashMap<String, Form>) : Form


    data class Field(
        val name: String,
        val form: Form
    )

    object Adapter : AdapterGeneric<Form> {
        override fun serialize(
            src: Form,
            typeOfSrc: java.lang.reflect.Type,
            context: JsonSerializationContext
        ): JsonElement {
            return when (src) {
                is Form.Boolean -> JsonPrimitive("Boolean")
                is Form.Number -> JsonPrimitive("Number")
                is Form.String -> JsonPrimitive("String")
                else -> TODO()
            }
        }

        override fun deserialize(
            json: JsonElement,
            typeOfT: java.lang.reflect.Type,
            context: JsonDeserializationContext
        ): Form {
            return when (json.asString) {
                "Boolean" -> Form.Boolean
                "Number" -> Form.Number
                "String" -> Form.String
                else -> TODO()
            }
        }
    }

}

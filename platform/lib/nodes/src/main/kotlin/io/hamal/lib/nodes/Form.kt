package io.hamal.lib.nodes

// Object(xyz:Object(a:Number,b:number),list:List[string],object_list:List[Object(x:boolean, y:string)])
interface Form {

    sealed interface Primitive : Form {
        data object Boolean : Primitive {}
        data object Number : Primitive
        data object String : Primitive
    }

    data class Object(val fields: LinkedHashMap<String, Form>) : Form


    data class Field(
        val name: String,
        val form: Form
    )

}

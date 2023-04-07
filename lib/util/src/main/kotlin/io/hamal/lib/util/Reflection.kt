//package io.hamal.lib.util
//
//import io.hamal.lib.meta.exception.IllegalArgumentException
//import io.hamal.lib.meta.exception.throwIf
//import io.hamal.lib.util.Reflection.setPropertyValue
//import kotlin.reflect.KClass
//import kotlin.reflect.KMutableProperty
//import kotlin.reflect.KProperty
//import kotlin.reflect.full.declaredMemberProperties
//import kotlin.reflect.full.memberProperties
//
//object Reflection {
//
//    fun <TYPE : Any> TYPE.declaredProperty(name: String): KProperty<*>? {
//        return declaredPropertiesOf(this::class)
//            .firstOrNull { it.name == name }
//    }
//
//    fun <TYPE : Any> TYPE.memberProperty(name: String): KProperty<*>? {
//        return memberPropertiesOf(this::class)
//            .firstOrNull { it.name == name }
//    }
//
//    fun <TYPE : Any, PROPERTY_VALUE_TYPE : Any> TYPE.setPropertyValue(
//        path: String,
//        newValue: PROPERTY_VALUE_TYPE
//    ): KProperty<*>? {
//        var currentObj = this as Any
//        var kClass = this::class as KClass<out Any>
//
//        val p = path.split(".")
//        for (i in 0 until p.size - 1) {
//            val next = kClass.declaredMemberProperties.find { it.name == p[i] }
//            if (next != null) {
//                currentObj = next.getter.call(currentObj)!!
//                kClass = currentObj::class
//            } else {
//                System.err.println("Member at path $path not found")
//            }
//        }
//        val member = kClass.declaredMemberProperties.find { it.name == p.last() } as KMutableProperty<Any>?
//        if (member != null) {
//            throwIf(member.returnType.classifier != newValue::class){
//                IllegalArgumentException("TODO")
//            }
//            val currentValue = member.getter.call(currentObj)
//            println("Current value of $path is $currentValue")
//            member.setter.call(currentObj, newValue)
//            println("New value is $newValue")
//        } else {
//            System.err.println("Member at path $path not found")
//        }
//
//        return null
//    }
//
//    fun memberPropertiesOf(clazz: KClass<*>): List<KProperty<*>> {
//        return clazz::memberProperties.get()
//            .filterNot { it.name.startsWith("__$") }
//            .toList()
//    }
//
//    fun declaredPropertiesOf(clazz: KClass<*>): List<KProperty<*>> {
//        return clazz::declaredMemberProperties.get()
//            .filterNot { it.name.startsWith("__$") }
//            .toList()
//    }
//
//}
//
//
//data class SomeDataClass(private val x: Int, val y: Double)
//
//open class Root(protected val x: Int)
//
//class SUb(val y: String) : Root(10)
//
////fun main() {
////    Reflection.propertiesOf(SUb::class).forEach {
////        println(it)
////    }
////
////    val s = SUb("Hello Wowrld")
////    println(s.y)
//////    (s.property("y") as KMutableProperty1<SUb, String>).setter.call("Bye")
////
////    println(s.y)
////
////}
//
//
//data class Person(
//    val name: String,
//    var age: Int,
//    var address: Address
//)
//
//data class Address(
//    var street: String,
//    var number: Int
//)
//
//fun main() {
//    val person = Person("John", 24, Address("Bakers", 42))
//
//    person.setPropertyValue("name", "Overwritten")
//    println(person)
//}
//
//

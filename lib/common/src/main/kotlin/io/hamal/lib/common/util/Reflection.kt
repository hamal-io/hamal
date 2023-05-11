package io.hamal.lib.common.util

import internal.JavaReflection
import java.lang.reflect.Field
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties

object Reflection {

    fun declaredProperty(obj: Any, name: String): KProperty<*>? {
        return declaredPropertiesOf(obj::class)
            .firstOrNull { it.name == name }
    }

    fun memberProperty(obj: Any, name: String): KProperty<*>? {
        return memberPropertiesOf(obj::class)
            .firstOrNull { it.name == name }
    }

    fun <TYPE : Any> allFields(clazz: KClass<out TYPE>): List<Field> {
        return allFields(clazz.java)
    }

    fun <TYPE : Any> allFields(clazz: Class<out TYPE>): List<Field> {
        return internal.JavaReflection.Fields.allFieldsOf(clazz)
            .filterNot { it.name.contains("this$") }
    }

    fun <TYPE : Any> allFields(obj: TYPE): List<Field> {
        return allFields(obj::class)
    }

    fun <TYPE : Any> getValue(obj: TYPE, path: String): Any {
        var currentObj = obj as Any
        fun throwNotFound() {
            throw IllegalArgumentException("${obj::class.simpleName} does have field '$path'")
        }

        val pathParts = path.split(".")
        for (idx in 0 until pathParts.size - 1) {
            currentObj = allFields(currentObj).find { it.name == pathParts[idx] }
                ?.let {
                    it.isAccessible = true
                    it.get(currentObj)
                } ?: throwNotFound()
        }

        return allFields(currentObj).find { it.name == pathParts.last() }?.let {
            it.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            it.get(currentObj) as TYPE
        } ?: throwNotFound()
    }

    fun memberPropertiesOf(clazz: KClass<*>): List<KProperty<*>> {
        return clazz::memberProperties.get()
            .filterNot { it.name.startsWith("__$") }
            .toList()
    }

    fun declaredPropertiesOf(clazz: KClass<*>): List<KProperty<*>> {
        return clazz::declaredMemberProperties.get()
            .filterNot { it.name.startsWith("__$") }
            .toList()
    }

}

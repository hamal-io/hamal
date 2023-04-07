package io.hamal.lib.ddd.base


interface ValueObject<VALUE_TYPE : Any> {
    val value: VALUE_TYPE
//    val valueClass: Class<VALUE_TYPE>

    abstract class BaseImpl<VALUE_TYPE : Any>(
        override val value: VALUE_TYPE,
//        override val valueClass: Class<VALUE_TYPE>
    ) : ValueObject<VALUE_TYPE> {

//        override val valueClass: Class<VALUE_TYPE> by lazy {
//            val genericSuperclass = this.javaClass.superclass.genericSuperclass as ParameterizedType
//            @Suppress("UNCHECKED_CAST")
//            genericSuperclass.actualTypeArguments[0] as Class<VALUE_TYPE>
//        }

    }

    abstract class ComparableImpl<VALUE_TYPE : Comparable<VALUE_TYPE>>(value: VALUE_TYPE) : BaseImpl<VALUE_TYPE>(value),
        Comparable<ValueObject<VALUE_TYPE>> {
        override fun compareTo(other: ValueObject<VALUE_TYPE>): Int {
            return value.compareTo(other.value)
        }
    }
}

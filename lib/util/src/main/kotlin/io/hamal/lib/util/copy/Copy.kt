package io.hamal.lib.util.copy

import internal.copy.*

/**
 * To the best of my knowledge this functionality has to be implemented in Java in order to be able to replace
 * val values
 */
object Copy {

    operator fun <T> invoke(source: T, target: T): T {
        return JavaCopy.copyFields(
            source, target,
            setOf<JavaCopy.Strategy>(
                primitiveFieldStrategy,
                immutableFieldStrategy,
                enumFieldStrategy,
//                valueObjectStrategy
            )
        );
    }

    internal val immutableClasses: Set<Class<*>> = mutableSetOf()

    private val primitiveFieldStrategy = JavaPrimitiveStrategy()
    private val immutableFieldStrategy = JavaImmutableStrategy(immutableClasses)
    private val enumFieldStrategy = JavaEnumStrategy()
//    private val valueObjectStrategy = JavaValueObjectFieldStrategy(
//        JavaCopy.MultiCopyStrategy(
//            listOf(
//                JavaPrimitiveCopyStrategy(),
//                JavaImmutableCopyStrategy(immutableClasses),
//                JavaEnumCopyStrategy()
//            )
//        )
//    )
}

fun main() {
    data class Test(val value: Int)

    val x = Test(10)
    val y = Test(20)

    println(y)
    Copy(x, y)
    println(y)

}
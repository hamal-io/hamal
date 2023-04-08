package io.hamal.lib.util.copy

import internal.copy.*
import internal.copy.JavaValueObjectStrategy.CopyStrategy

/**
 * To the best of my knowledge this functionality has to be implemented in Java in order to be able to replace
 * val values
 */
object Copy {

    operator fun <T> invoke(source: T, target: T): T {
        return JavaCopy.copyFields(
            source, target,
            setOf(
                primitiveFieldStrategy,
                immutableFieldStrategy,
                enumFieldStrategy,
                valueObjectStrategy
            )
        );
    }

    internal val immutableClasses: Set<Class<*>> = mutableSetOf()

    private val primitiveFieldStrategy = JavaPrimitiveStrategy()
    private val immutableFieldStrategy = JavaImmutableStrategy(immutableClasses)
    private val enumFieldStrategy = JavaEnumStrategy()
    private val valueObjectStrategy = JavaValueObjectStrategy(
        CopyStrategy(
            listOf(
                primitiveFieldStrategy,
                immutableFieldStrategy,
                enumFieldStrategy
            )
        )
    )
}

package io.hamal.lib.common.util.copy

import internal.copy.JavaValueObjectStrategy.CopyStrategy

/**
 * To the best of my knowledge this functionality has to be implemented in Java in order to be able to replace
 * val values
 */
object Copy {

    operator fun invoke(source: Any, target: Any): Any {
        return internal.copy.JavaCopy.copyFields(
            source, target, setOf(
                primitiveFieldStrategy, immutableFieldStrategy, enumFieldStrategy, valueObjectStrategy
            )
        );
    }

    internal val immutableClasses: Set<Class<*>> = mutableSetOf()

    private val primitiveFieldStrategy = internal.copy.JavaPrimitiveStrategy()
    private val immutableFieldStrategy = internal.copy.JavaImmutableStrategy(immutableClasses)
    private val enumFieldStrategy = internal.copy.JavaEnumStrategy()
    private val valueObjectStrategy = internal.copy.JavaValueObjectStrategy(
        CopyStrategy(
            listOf(
                primitiveFieldStrategy, immutableFieldStrategy, enumFieldStrategy
            )
        )
    )
}



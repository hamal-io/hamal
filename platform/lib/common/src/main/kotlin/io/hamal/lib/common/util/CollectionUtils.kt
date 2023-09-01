package io.hamal.lib.common.util

object CollectionUtils {
    fun <T, U> Array<T>.cross(other: Array<U>): Sequence<Pair<T, U>> {
        return this.asSequence().cross(other.asSequence())
    }

    fun <T, U> Sequence<T>.cross(other: Sequence<U>): Sequence<Pair<T, U>> {
        return flatMap { lhs -> other.map { rhs -> lhs to rhs } }
    }

    inline fun <T> Collection<T>.takeWhileInclusive(
        predicate: (T) -> Boolean
    ): List<T> {
        var shouldContinue = true
        return takeWhile {
            val result = shouldContinue
            shouldContinue = predicate(it)
            result
        }
    }
}
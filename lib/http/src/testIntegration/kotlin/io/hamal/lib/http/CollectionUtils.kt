package io.hamal.lib.http

import io.hamal.lib.http.CollectionUtils.cross

object CollectionUtils {
    fun <T, U> Array<T>.cross(other: Array<U>): Sequence<Pair<T, U>> {
        return this.asSequence().cross(other.asSequence())
    }

    fun <T, U> Sequence<T>.cross(other: Sequence<U>): Sequence<Pair<T, U>> {
        return flatMap { lhs -> other.map { rhs -> lhs to rhs } }
    }
}
package io.hamal.lib.meta

import java.util.*

data class Maybe<VALUE_TYPE : Any>(internal val value: Optional<VALUE_TYPE>) {
    constructor(value: VALUE_TYPE?) : this(Optional.ofNullable(value))

    companion object {
        fun <VALUE_TYPE : Any> some(value: VALUE_TYPE): Maybe<VALUE_TYPE> {
            return Maybe(value)
        }

        fun <VALUE_TYPE : Any> none(): Maybe<VALUE_TYPE> {
            return Maybe(null)
        }
    }
}

fun <VALUE_TYPE : Any> Maybe<VALUE_TYPE>.isPresent() = value.isPresent

fun <VALUE_TYPE : Any> Maybe<VALUE_TYPE>.isAbsent() = value.isEmpty

fun <VALUE_TYPE : Any> Maybe<VALUE_TYPE>.get(): VALUE_TYPE = value.get()

fun <VALUE_TYPE : Any> Maybe<VALUE_TYPE>.orElseThrow(supplier: () -> Exception): VALUE_TYPE =
    value.orElseThrow { supplier() }
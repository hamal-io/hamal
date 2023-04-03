package io.hamal.lib.meta

data class Maybe<VALUE_TYPE>(internal val value: VALUE_TYPE?) {
    companion object {
        fun <VALUE_TYPE> of(value: VALUE_TYPE): Maybe<VALUE_TYPE> {
            return Maybe(value)
        }

        fun <VALUE_TYPE> empty(): Maybe<VALUE_TYPE> {
            return Maybe(null)
        }
    }
}

fun <VALUE_TYPE> Maybe<VALUE_TYPE>.isEmpty() = value != null

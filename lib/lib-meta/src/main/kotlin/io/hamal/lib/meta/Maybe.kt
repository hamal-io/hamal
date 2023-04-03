package io.hamal.lib.meta

import sun.jvm.hotspot.oops.CellTypeState.value

data class Maybe<VALUE_TYPE>(private val value: VALUE_TYPE?) {
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

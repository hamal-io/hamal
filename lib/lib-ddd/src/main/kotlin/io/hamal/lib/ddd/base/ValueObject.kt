package io.hamal.lib.ddd.base

import java.io.Serializable

interface ValueObject<T> : Serializable {
    fun value(): T
}
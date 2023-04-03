package io.hamal.lib.domain.exception

fun throwIf(condition: Boolean, fn: () -> BaseException) {
    if (condition) {
        throw fn()
    }
}
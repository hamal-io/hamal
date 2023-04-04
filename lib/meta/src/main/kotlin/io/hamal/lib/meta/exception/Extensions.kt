package io.hamal.lib.meta.exception


fun throwIf(condition: Boolean, fn: () -> HamalException) {
    if (condition) {
        throw fn()
    }
}
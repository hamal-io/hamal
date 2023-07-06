package io.hamal.lib.script.api.value

interface ValueProp<in VALUE : Value> {
    operator fun invoke(self: @UnsafeVariance VALUE): Value
}
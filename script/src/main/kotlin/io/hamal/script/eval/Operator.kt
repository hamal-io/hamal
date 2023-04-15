package io.hamal.script.eval

import io.hamal.script.value.Value

interface EvalOperator {
    operator fun invoke(lhs: Value, rhs: Value, env: Environment)
}
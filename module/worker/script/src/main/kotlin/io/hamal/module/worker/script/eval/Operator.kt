package io.hamal.module.worker.script.eval

import io.hamal.module.worker.script.value.Value

interface EvalOperator {
    operator fun invoke(lhs: Value, rhs: Value, env: Environment)
}
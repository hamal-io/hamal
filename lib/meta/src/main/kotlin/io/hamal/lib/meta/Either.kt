package io.hamal.lib.meta

data class Either<LEFT_VALUE_TYPE : Any, RIGHT_VALUE_TYPE : Any>(
    private val lhs: Maybe<LEFT_VALUE_TYPE>,
    private val rhs: Maybe<RIGHT_VALUE_TYPE>
)
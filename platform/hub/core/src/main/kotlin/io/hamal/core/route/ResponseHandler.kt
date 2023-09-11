package io.hamal.core.route

interface ResponseHandler<IN : Any, OUT : Any> {
    operator fun invoke(input: IN): OUT
}
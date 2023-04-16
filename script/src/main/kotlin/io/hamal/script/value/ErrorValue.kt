package io.hamal.script.value

data class ErrorValue(
    val cause: StringValue
) : Value{

}
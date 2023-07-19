package io.hamal.lib.kua.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("CodeValue")
data class CodeValue(val value: String) : Value {
    constructor(str: StringValue) : this(str.value)
}
package io.hamal.lib.kua.type

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("CodeType")
data class CodeType(val value: String) : SerializableType() {
    constructor(str: StringType) : this(str.value)
}
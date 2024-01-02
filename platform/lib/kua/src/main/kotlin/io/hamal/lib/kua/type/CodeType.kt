package io.hamal.lib.kua.type

data class CodeType(val value: String) : SerializableType() {
    constructor(str: StringType) : this(str.value)
}
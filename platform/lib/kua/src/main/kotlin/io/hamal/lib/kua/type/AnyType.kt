package io.hamal.lib.kua.type

data class AnyType(val value: Type) : Type

data class AnySerializableType(val value: SerializableType) : SerializableType()
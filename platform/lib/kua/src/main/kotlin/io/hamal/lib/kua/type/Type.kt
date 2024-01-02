package io.hamal.lib.kua.type

interface Type

sealed class SerializableType : Type

sealed class TableType : SerializableType()

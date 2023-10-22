package io.hamal.lib.kua.type

import kotlinx.serialization.Serializable

interface Type

@Serializable
sealed class SerializableType : Type

@Serializable
sealed class TableType : SerializableType()

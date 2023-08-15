package io.hamal.lib.kua.type

import kotlinx.serialization.Serializable

interface Type

@Serializable
sealed interface SerializableType : Type
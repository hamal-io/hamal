package io.hamal.lib.kua.value

import kotlinx.serialization.Serializable

//@Serializable
interface Value

@Serializable
sealed interface SerializableValue : Value // FIXME
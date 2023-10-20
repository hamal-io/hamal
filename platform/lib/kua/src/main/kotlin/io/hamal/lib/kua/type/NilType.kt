package io.hamal.lib.kua.type

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("NilType")
object NilType : SerializableType()
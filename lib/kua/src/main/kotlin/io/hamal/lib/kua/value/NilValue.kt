package io.hamal.lib.kua.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("NilValue")
object NilValue : Value {
    override val type = Value.Type.Nil
}
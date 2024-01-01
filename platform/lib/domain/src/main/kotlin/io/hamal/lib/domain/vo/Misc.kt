package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.StringValueObjectSerializer
import io.hamal.lib.common.domain.ValueObjectString
import kotlinx.serialization.Serializable

@Serializable(with = Email.Serializer::class)
class Email(override val value: String) : ValueObjectString() {
    //FIXME validate email
    internal object Serializer : StringValueObjectSerializer<Email>(::Email)
}

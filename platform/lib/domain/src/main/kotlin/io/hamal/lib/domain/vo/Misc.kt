package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.StringValueObject
import io.hamal.lib.common.domain.StringValueObjectSerializer
import kotlinx.serialization.Serializable

@Serializable(with = Email.Serializer::class)
class Email(override val value: String) : StringValueObject() {
    //FIXME validate email
    internal object Serializer : StringValueObjectSerializer<Email>(::Email)
}

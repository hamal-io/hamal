package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.StringValueObject
import io.hamal.lib.common.domain.StringValueObjectSerializer
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.kua.type.StringType
import kotlinx.serialization.Serializable

@Serializable(with = FeedbackId.Serializer::class)
class FeedbackId(override val value: SnowflakeId) : SerializableDomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))

    internal object Serializer : SerializableDomainIdSerializer<FeedbackId>(::FeedbackId)
}

@Serializable(with = FeedbackMessage.Serializer::class)
class FeedbackMessage(override val value: String) : StringValueObject() {
    constructor(str: StringType) : this(str.value)

    internal object Serializer : StringValueObjectSerializer<FeedbackMessage>(::FeedbackMessage)
}
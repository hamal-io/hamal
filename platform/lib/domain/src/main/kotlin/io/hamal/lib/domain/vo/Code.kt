package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.IntValueObject
import io.hamal.lib.common.domain.IntValueObjectSerializer
import io.hamal.lib.common.domain.StringValueObjectSerializer
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.kua.type.StringType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(with = CodeId.Serializer::class)
class CodeId(override val value: SnowflakeId) : SerializableDomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))

    internal object Serializer : SerializableDomainIdSerializer<CodeId>(::CodeId)
}


@SerialName("CodeValue")
@Serializable(with = CodeValue.Serializer::class)
class CodeValue(override val value: String) : ValueObjectString() {
    constructor(str: StringType) : this(str.value)

    internal object Serializer : StringValueObjectSerializer<CodeValue>(::CodeValue)
}


@Serializable(with = CodeVersion.Serializer::class)
class CodeVersion(override val value: Int) : IntValueObject() {
    init {
        require(value > 0) { "CodeVersion must be positive" }
    }

    internal object Serializer : IntValueObjectSerializer<CodeVersion>(::CodeVersion)
}
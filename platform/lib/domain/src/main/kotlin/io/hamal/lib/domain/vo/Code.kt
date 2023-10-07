package io.hamal.lib.domain.vo

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.common.domain.DomainIdSerializer
import io.hamal.lib.common.domain.ValueObject
import io.hamal.lib.kua.type.StringType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(with = CodeId.Serializer::class)
class CodeId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))

    internal object Serializer : DomainIdSerializer<CodeId>(::CodeId)
}


@Serializable
@SerialName("CodeValue")
data class CodeValue(override val value: String) : ValueObject<String> {
    constructor(str: StringType) : this(str.value)
}

@Serializable
@SerialName("CodeVersion")
data class CodeVersion(override val value: Int) : ValueObject<Int>


package io.hamal.lib.domain.vo

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.common.domain.DomainIdSerializer
import io.hamal.lib.common.domain.StringValueObject
import io.hamal.lib.common.domain.StringValueObjectSerializer
import io.hamal.lib.domain.vo.base.Inputs
import io.hamal.lib.domain.vo.base.InputsSerializer
import io.hamal.lib.kua.type.MapType
import kotlinx.serialization.Serializable

@Serializable(with = ExecId.Serializer::class)
class ExecId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))

    internal object Serializer : DomainIdSerializer<ExecId>(::ExecId)
}

@Serializable(with = ExecInputs.Serializer::class)
class ExecInputs(override val value: MapType = MapType()) : Inputs() {
    internal object Serializer : InputsSerializer<ExecInputs>(::ExecInputs)
}

enum class ExecStatus(val value: Int) {
    Planned(1),
    Scheduled(2),
    Queued(3),
    Started(4),
    Completed(5),
    Failed(6);

    companion object {
        fun valueOf(value: Int) = requireNotNull(mapped[value]) { "$value is not an exec status" }

        private val mapped = ExecStatus.values()
            .associateBy { it.value }
    }
}


@Serializable(with = ExecToken.Serializer::class)
class ExecToken(override val value: String) : StringValueObject() {
    internal object Serializer : StringValueObjectSerializer<ExecToken>(::ExecToken)
}
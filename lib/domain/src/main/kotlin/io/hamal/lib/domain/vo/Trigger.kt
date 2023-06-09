package io.hamal.lib.domain.vo

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.vo.base.*
import io.hamal.lib.script.api.value.TableValue
import kotlinx.serialization.Serializable


@Serializable(with = TriggerId.Serializer::class)
class TriggerId(override val value: SnowflakeId) : DomainId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong()))

    internal object Serializer : DomainIdSerializer<TriggerId>(::TriggerId)
}

@Serializable(with = TriggerName.Serializer::class)
class TriggerName(override val value: String) : DomainName() {
    internal object Serializer : DomainNameSerializer<TriggerName>(::TriggerName)
}

@Serializable(with = TriggerInputs.Serializer::class)
class TriggerInputs(override val value: TableValue) : Inputs() {
    internal object Serializer : InputsSerializer<TriggerInputs>(::TriggerInputs)
}


@Serializable(with = TriggerSecrets.Serializer::class)
class TriggerSecrets(override val value: List<Secret>) : Secrets() {
    internal object Serializer : SecretsSerializer<TriggerSecrets>(::TriggerSecrets)
}

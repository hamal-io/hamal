package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.StringValueObject
import io.hamal.lib.common.domain.StringValueObjectSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ApiHost.Serializer::class)
class ApiHost(override val value: String) : StringValueObject() {
    internal object Serializer : StringValueObjectSerializer<ApiHost>(::ApiHost)
}
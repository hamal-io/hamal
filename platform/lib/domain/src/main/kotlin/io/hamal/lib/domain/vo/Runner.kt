package io.hamal.lib.domain.vo

import io.hamal.lib.domain.vo.base.InputsSerializer
import io.hamal.lib.domain.vo.base.MapValueObject
import io.hamal.lib.kua.type.MapType
import kotlinx.serialization.Serializable


@Serializable(with = RunnerEnv.Serializer::class)
class RunnerEnv(override val value: MapType = MapType()) : MapValueObject() {
    internal object Serializer : InputsSerializer<RunnerEnv>(::RunnerEnv)
}
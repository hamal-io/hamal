package io.hamal.lib.domain.vo

import io.hamal.lib.domain.vo.base.MapValueObject
import io.hamal.lib.kua.type.KuaMap


class RunnerEnv(override val value: KuaMap = KuaMap()) : MapValueObject()
package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectHotObject
import io.hamal.lib.common.hot.HotObject


class HookHeaders(override val value: HotObject = HotObject.empty) : ValueObjectHotObject()

class HookParameters(override val value: HotObject = HotObject.empty) : ValueObjectHotObject()

class HookContent(override val value: HotObject = HotObject.empty) : ValueObjectHotObject()
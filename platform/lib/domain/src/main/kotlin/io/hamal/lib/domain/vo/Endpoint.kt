package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectHotObject
import io.hamal.lib.common.hot.HotObject

class EndpointHeaders(override val value: HotObject = HotObject.empty) : ValueObjectHotObject()

class EndpointParameters(override val value: HotObject = HotObject.empty) : ValueObjectHotObject()

class EndpointContent(override val value: HotObject = HotObject.empty) : ValueObjectHotObject()

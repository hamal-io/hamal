package io.hamal.lib.domain.vo

import kotlinx.serialization.Serializable

@Serializable
class FlowId(override val value: String) : Id()
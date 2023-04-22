package io.hamal.lib.domain.vo

import kotlinx.serialization.Serializable

@Serializable
class TriggerId(override val value: String) : Id()

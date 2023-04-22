package io.hamal.lib.domain.vo

import kotlinx.serialization.Serializable

@Serializable
class TaskId(override val value: String) : Id()

package io.hamal.backend.core.task

import io.hamal.backend.core.task.Task.Type
import io.hamal.backend.core.task.Task.Type.Script
import io.hamal.lib.domain.vo.TaskId
import kotlinx.serialization.Serializable

@Serializable
sealed interface Task {
    val id: TaskId
    val type: Type

    enum class Type {
        Script
    }
}

@Serializable
data class ScriptTask(
    override val id: TaskId
) : Task {
    override val type: Type = Script
}
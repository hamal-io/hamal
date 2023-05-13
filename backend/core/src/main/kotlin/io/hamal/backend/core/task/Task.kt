package io.hamal.backend.core.task

import io.hamal.backend.core.task.TaskType.Script
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.TaskId
import kotlinx.serialization.Serializable

enum class TaskType {
    Script
}

@Serializable
sealed interface Task {
    val id: TaskId
    val taskType: TaskType


}

@Serializable
data class ScriptTask(
    override val id: TaskId,
    val code: Code
) : Task {
    override val taskType: TaskType = Script
}
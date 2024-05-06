package io.hamal.lib.domain._enum

enum class TopicTypes(val value: Int) {
    Internal(1),
    Namespace(2),
    Workspace(3),
    Public(4);

    companion object {
        fun valueOf(value: Int) = requireNotNull(mapped[value]) { "$value is not a topic type" }
        private val mapped = entries.associateBy { it.value }
    }
}
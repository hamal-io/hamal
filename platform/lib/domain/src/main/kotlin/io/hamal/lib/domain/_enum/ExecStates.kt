package io.hamal.lib.domain._enum

enum class ExecStates(val value: Int) {
    Planned(1),
    Scheduled(2),
    Queued(3),
    Started(4),
    Completed(5),
    Failed(6);

    companion object {
        fun valueOf(value: Int) = requireNotNull(mapped[value]) { "$value is not an exec status" }
        private val mapped = entries.associateBy { it.value }
    }
}
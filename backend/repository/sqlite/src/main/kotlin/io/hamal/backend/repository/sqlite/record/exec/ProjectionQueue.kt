package io.hamal.backend.repository.sqlite.record.exec

import io.hamal.backend.repository.api.domain.QueuedExec

//FIXME this must be concurrent safe
internal object ProjectionQueue {
    private val queue = mutableListOf<QueuedExec>()
    fun add(exec: QueuedExec) {
        queue.add(exec)
    }

    fun pop(limit: Int): List<QueuedExec> {
        val result = mutableListOf<QueuedExec>()
        for (idx in 0 until limit) {
            if (queue.isEmpty()) {
                break
            }
            result.add(queue.removeFirst())
        }
        return result
    }
}

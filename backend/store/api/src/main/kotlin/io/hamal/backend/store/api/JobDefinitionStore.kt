package io.hamal.backend.store.api

import io.hamal.backend.core.model.FlowDefinition
import io.hamal.lib.RequestId
import io.hamal.lib.util.Snowflake
import io.hamal.lib.vo.FlowDefinitionId
import io.hamal.lib.vo.TriggerId


interface DefinitionStore {
    fun create(definition: FlowDefinition)

    fun request(requestId: RequestId): FlowDefinition

}

interface Command {

    class InsertFD : Command {}
    class InsertTrigger : Command {}

}

class Recorder {
    fun insertFlowDefinition(): FlowDefinitionId {
        println("InsertFD")
        return FlowDefinitionId(Snowflake.Id(10))
    }

    fun insertTrigger(flowDefinitionId: FlowDefinitionId): TriggerId {
        println("InsertTrigger")
        return TriggerId(Snowflake.Id(23))
    }

    internal fun commands(): List<Command> {
        return listOf()
    }


}

object TestStore {

    fun request(requestId: RequestId, record: (Recorder) -> Unit): FlowDefinition {
        val recorder = Recorder()
        record(recorder)
        val commands = recorder.commands()

        // insert fds
        // insert triggers
        // update ...
        // remove ...

        return FlowDefinition(
            id = FlowDefinitionId(Snowflake.Id(0)),
            triggers = listOf()
        )
    }
}


fun main() {

    TestStore.request(RequestId(123)) {
        val id = it.insertFlowDefinition()
        it.insertTrigger(id)
        it.insertTrigger(id)
    }
}
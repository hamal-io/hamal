package io.hamal.lib.domain.vo

import io.hamal.lib.domain.vo.flow.FlowId
import io.hamal.lib.domain.vo.task.TaskId

data class T(val id: FlowId)

fun main(){
    val x = FlowId("TEST")
    val y = FlowId("TEST")
    println(x == y)

}
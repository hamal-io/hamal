package io.hamal.lib.script.api.ast


interface Node {
    val position: Position

    data class Position(
        val line: Int,
        val position: Int
    )
}
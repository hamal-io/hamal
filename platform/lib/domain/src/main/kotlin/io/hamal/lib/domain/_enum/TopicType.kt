package io.hamal.lib.domain._enum

enum class TopicType(val value: Int) {
    Internal(1),
    Namespace(2),
    Workspace(3),
    Public(4)
}
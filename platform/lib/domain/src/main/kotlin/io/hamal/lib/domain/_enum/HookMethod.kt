package io.hamal.lib.domain._enum

enum class HookMethod(val value: Int) {
    Delete(1),
    Get(2),
    Patch(3),
    Post(4),
    Put(5)
}
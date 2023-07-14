package io.hamal.lib.kua

class State {
    external fun init()

    external fun versionNumber(): Int
    external fun integerWidth(): Int

    external fun top(): Int
    external fun peekBoolean(idx: Int): Boolean
    external fun pushBoolean(value: Boolean)
}

package io.hamal.lib.kua

enum class LuaType(
    val value: Int
) {
    Nil(0),
    Boolean(1),
    Pointer(2),
    Number(3),
    String(4),
    Table(5),
    Function(6),
    UserData(7),
    Thread(8)
}
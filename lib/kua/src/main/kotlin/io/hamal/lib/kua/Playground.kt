package io.hamal.lib.kua


fun main() {
//    System.load("/home/ddymke/Repo/hamal/lib/kua/native/cmake-build-debug/kua/libkua.so")
    FixedPathLoader.load()
    val s = LuaState()

    val sbox = LuaSandbox(s)

    sbox.state.createTable(1, 1)
    sbox.state.pushString("value")
    sbox.state.setTableField(1, "key")
    sbox.state.getTableField(1, "key")
    sbox.state.getTableField(1, "key")

    val result = sbox.state.tableRawLength(1)
    println(result)

}

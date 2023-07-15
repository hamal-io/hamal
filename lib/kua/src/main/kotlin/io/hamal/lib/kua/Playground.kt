package io.hamal.lib.kua

fun main() {
//    System.load("/home/ddymke/Repo/hamal/lib/kua/native/cmake-build-debug/kua/libkua.so")
    FixedPathLoader.load()
    val s = LuaState()

    val sbox = LuaSandbox(s)

//    println(sbox.stack.size())
//    println(sbox.stack.pushBoolean(true))
//    println(sbox.stack.toBoolean(1))

//    println(sbox.state.loadString("""
//        local table = {}
//        table['test'] = 4211
//
//        table.test = table.test + 10
//
//        print("test", table['test'])
//        assert(1==1)
//    """.trimIndent()))
//    println(sbox.stack.size())
//    sbox.state.call(0, 0)
//    println(sbox.stack.size())


    sbox.state.pushNumber(2.0)
    println(sbox.stack.toBoolean(1))

}

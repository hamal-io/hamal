package io.hamal.lib.kua

fun main() {
//    System.load("/home/ddymke/Repo/hamal/lib/kua/native/cmake-build-debug/kua/libkua.so")
    FixedPathLoader.load()
    val s = LuaState()

    val sbox = LuaSandbox(s)

    println(sbox.stack.size())
    println(sbox.stack.pushBoolean(true))
    println(sbox.stack.toBoolean(1))
}

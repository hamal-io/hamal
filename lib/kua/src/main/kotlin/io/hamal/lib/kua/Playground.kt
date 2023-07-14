package io.hamal.lib.kua

fun main() {
//    System.load("/home/ddymke/Repo/hamal/lib/kua/native/cmake-build-debug/kua/libkua.so")
    FixedPathLoader.load()
    val s = State()
//    println(s.integerWidth())
//    println(s.versionNumber())

    s.init()
    println(s.top())
    s.pushBoolean(true)
    println(s.top())
    println(s.peekBoolean(2))
//    println(s.peekIndex(1))
//    println(s.pushBoolean(false))
//    println(s.peekIndex(2))
}
package io.hamal.lib.kua

fun main() {
    System.load("/home/ddymke/Repo/hamal/lib/kua/native/cmake-build-debug/kua/libkua.so")
    val s = State()

    println(s.integerWidth())
}
package io.hamal.lib.kua

import org.terasology.kua.LuaState

fun main() {
    System.load("/home/ddymke/Repo/hamal/lib/kua/native/kua/build/libkua.so")
    val s = LuaState()

    println(s.versionNumber())
}
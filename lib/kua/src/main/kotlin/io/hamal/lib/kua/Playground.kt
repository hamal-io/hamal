package io.hamal.lib.kua

import org.terasology.kua.LuaState53

fun main() {
    System.load("/home/ddymke/Repo/hamal/lib/kua/native/kua/build/libkua.so")
    val s = LuaState53()

    println(s.lua_versionnum())
}
package io.hamal.lib.kua


interface Loader {
    fun load()
}

object FixedPathLoader : Loader {
    override fun load() {
        System.load("/home/ddymke/Repo/hamal/lib/kua/native/cmake-build-debug/lua/liblua.so")
        System.load("/home/ddymke/Repo/hamal/lib/kua/native/cmake-build-debug/kua/libkua.so")
    }
}

object ResourceLoader : Loader {
    override fun load() {
        val classloader = Thread.currentThread().contextClassLoader
        System.load(classloader.getResource("./liblua.so")!!.file)
        System.load(classloader.getResource("./libkua.so")!!.file)
    }
}



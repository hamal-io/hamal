package io.hamal.lib.kua


interface Loader {
    fun load()
}

object FixedPathLoader : Loader {
    override fun load() {
        System.load("/home/ddymke/Repo/hamal/lib/kua/native/cmake-build-debug/kua/libkua.so")
    }
}

object ResourceLoader : Loader {
    override fun load() {
        val classloader = Thread.currentThread().contextClassLoader
        val path = classloader.getResource("./libkua.so")!!
        System.load(path.file)
    }
}



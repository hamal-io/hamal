package io.hamal.lib.kua

import io.hamal.lib.domain.Once


interface Loader {
    fun load()
}

object FixedPathLoader : Loader {
    override fun load() {
        once {
            System.load("/home/ddymke/Repo/hamal/lib/kua/native/cmake-build-debug/lua/liblua.so")
            System.load("/home/ddymke/Repo/hamal/lib/kua/native/cmake-build-debug/kua/libkua.so")
        }
    }

    @JvmStatic
    private val once = Once.default<Unit>()
}


object ResourceLoader : Loader {
    override fun load() {
        once {
            val classloader = Thread.currentThread().contextClassLoader
            System.load(classloader.getResource("./liblua.so")!!.file)
            System.load(classloader.getResource("./libkua.so")!!.file)
        }
    }

    @JvmStatic
    private val once = Once.default<Unit>()
}





package io.hamal.lib.kua

import cz.adamh.utils.NativeUtils
import io.hamal.lib.domain.Once

object NativeLoader {

    enum class Preference {
        Jar,
        Resources,
        BuildDir
    }

    fun load(preference: Preference) {
        if (tryToLoad(preference)) {
            return
        }

        while (loaders.isNotEmpty()) {
            val next = loaders.keys.first()
            if (tryToLoad(next)) {
                return
            }
            loaders.remove(next)
        }

        throw IllegalStateException("Unable to load shared libraries")
    }

    private fun tryToLoad(preference: Preference) = loaders[preference]!!.load()

    private val loaders = mutableMapOf(
        Preference.Jar to JarLoader,
        Preference.Resources to ResourcesLoader,
        Preference.BuildDir to BuildDirLoader,
    )
}

private interface Loader {
    fun load(): Boolean
}

internal object BuildDirLoader : Loader {
    override fun load(): Boolean {
        return once {
            val baseDir = System.getProperty("user.dir")
            try {
                System.load("$baseDir/lib/kua/native/cmake-build-debug/lua/liblua.so")
                System.load("$baseDir/lib/kua/native/cmake-build-debug/kua/libkua.so")
                true
            } catch (t: Throwable) {
                false
            }
        }
    }

    @JvmStatic
    private val once = Once.default<Boolean>()
}


fun main() {
    BuildDirLoader.load()
}

internal object ResourcesLoader : Loader {
    override fun load(): Boolean {
        return once {
            val classloader = Thread.currentThread().contextClassLoader
            val result = classloader.getResource("./liblua.so")
                ?.let { System.load(it.file); true }
                ?: false
            if (result) {
                classloader.getResource("./libkua.so")
                    ?.let { System.load(it.file); true }
                    ?: false
            } else {
                false
            }
        }
    }

    @JvmStatic
    private val once = Once.default<Boolean>()
}


internal object JarLoader : Loader {
    override fun load(): Boolean {
        return once {
            try {
                NativeUtils.loadLibraryFromJar("/BOOT-INF/classes/liblua.so")
                NativeUtils.loadLibraryFromJar("/BOOT-INF/classes/libkua.so")
                true
            } catch (t: Throwable) {
                false
            }
        }
    }

    @JvmStatic
    private val once = Once.default<Boolean>()
}

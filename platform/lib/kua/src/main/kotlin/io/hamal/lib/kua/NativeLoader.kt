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
                System.load("$baseDir/lib/kua/native/cmake-build-debug/lua/$luaFile")
                System.load("$baseDir/lib/kua/native/cmake-build-debug/kua/$kuaFile")
                System.load("$baseDir/lib/kua/native/cmake-build-debug/kua/$mpdecimal")
                true
            } catch (t: Throwable) {
                false
            }
        }
    }

    @JvmStatic
    private val once = Once.default<Boolean>()
}

internal object ResourcesLoader : Loader {
    override fun load(): Boolean {
        return once {
            val classloader = Thread.currentThread().contextClassLoader
            val result = classloader.getResource("./$luaFile")
                ?.let { System.load(it.file); true }
                ?: false
            if (result) {
                classloader.getResource("./${kuaFile}")
                    ?.let { System.load(it.file); true }
                    ?: false

                classloader.getResource("./${mpdecimal}")
                    ?.let { System.load(it.file) }
                    ?: throw IllegalStateException("unable to load mpdecimal")

                true
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
                NativeUtils.loadLibraryFromJar("/$luaFile")
                NativeUtils.loadLibraryFromJar("/$mpdecimal")
                NativeUtils.loadLibraryFromJar("/$kuaFile")
                true
            } catch (t: Throwable) {
                false
            }
        }
    }

    @JvmStatic
    private val once = Once.default<Boolean>()
}

private val kuaFile by lazy {
    when (detectSystem()) {
        HostSystem.Linux64 -> "libkua.so"
        HostSystem.MacArm64 -> "libkua.dylib"
    }
}

private val luaFile by lazy {
    when (detectSystem()) {
        HostSystem.Linux64 -> "liblua.so"
        HostSystem.MacArm64 -> "liblua.dylib"
    }
}

private val mpdecimal by lazy {
    when (detectSystem()) {
        HostSystem.Linux64 -> "libmpdecimal_100.so"
        HostSystem.MacArm64 -> "libmpdecimal_100.dylib"
    }
}

enum class HostSystem {
    Linux64,
    MacArm64
}

private fun detectSystem(): HostSystem {
    val os = System.getProperty("os.name").lowercase()
    return if (os.startsWith("mac")) {
        HostSystem.MacArm64
    } else {
        HostSystem.Linux64
    }
}
package io.hamal.lib.common.util

import io.hamal.lib.domain.Once

object GitUtils {
    fun gitHash(): String {
        return once {
            val classloader = Thread.currentThread().contextClassLoader
            classloader.getResource("./git_commit.txt")
                ?.let { String(it.readBytes()) }
                ?: "NoHash"
        }
    }

    @JvmStatic
    private val once = Once.default<String>()
}
package io.hamal.lib.util

import java.nio.file.Path


object Files {

    fun createDirectories(path: Path) {
        java.nio.file.Files.createDirectory(path)
    }

    fun exists(path: Path) = java.nio.file.Files.exists(path)

    fun delete(path: Path) {
        path.toFile().deleteRecursively()
    }
}

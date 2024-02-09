package io.hamal.lib.common.util

import java.nio.file.Path
import java.nio.file.attribute.FileAttribute
import kotlin.io.path.Path


object FileUtils {
    fun createDirectories(path: Path, vararg attribs: FileAttribute<*>): Path {
        return java.nio.file.Files.createDirectories(path, *attribs)
    }

    fun exists(path: Path) = java.nio.file.Files.exists(path)

    fun delete(path: Path) {
        path.toFile().deleteRecursively()
    }

    fun ensureFilePath(path: Path, filename: String): Path {
        return createDirectories(path).resolve(path.resolve(Path(filename)))
    }
}

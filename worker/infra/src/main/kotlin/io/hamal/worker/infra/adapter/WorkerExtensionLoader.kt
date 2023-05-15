package io.hamal.worker.infra.adapter

import io.hamal.worker.extension.api.WorkerExtension
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import java.util.*


internal interface WorkerExtensionLoader {
    fun load(workerExtensionFile: File): WorkerExtension

    class DefaultImpl() : WorkerExtensionLoader {

        override fun load(entryPointFile: File): WorkerExtension {
            println("load WorkerExtensionEntryPoint from ${entryPointFile.absolutePath}")
            ensureIsFile(entryPointFile)
            val workerExtensionClassLoader = createWorkerExtensionClassLoader(entryPointFile)
            val currentClassLoader = Thread.currentThread().contextClassLoader
            try {
                Thread.currentThread().contextClassLoader = workerExtensionClassLoader
                for (entryPoint in ServiceLoader.load(
                    WorkerExtension::class.java,
                    workerExtensionClassLoader
                )) {
                    return entryPoint
                }
            } catch (t: Throwable) {
                System.err.println("load error:")
                t.printStackTrace()
            } finally {
                Thread.currentThread().contextClassLoader = currentClassLoader
            }
            throw Error("no WorkerExtensionEntryPoint found in ${entryPointFile}")
        }

        private fun ensureIsFile(file: File) {
            if (!file.isFile) {
                throw Error("$file must be a file")
            }
        }

        private fun createWorkerExtensionClassLoader(file: File): ClassLoader {
            val urls: Array<URL> = arrayOf(toUrl(file))
            return WorkerExtensionClassLoader(
                urls,
                javaClass.classLoader
            )
        }

        private fun toUrl(file: File): URL {
            return try {
                val res = file.toURI().toURL()
                println("FILE: ${res}")
                println(file.exists())
                res
            } catch (e: MalformedURLException) {
                throw Error(e)
            }
        }
    }
}


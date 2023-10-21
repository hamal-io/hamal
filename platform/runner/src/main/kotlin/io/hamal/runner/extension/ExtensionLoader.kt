//package io.hamal.runner.extension
//
//import io.hamal.lib.kua.extension.NativeExtensionFactory
//import java.io.File
//import java.net.MalformedURLException
//import java.net.URL
//import java.util.*
//
//
//internal interface ExtensionLoader {
//    fun load(extensionFile: File): NativeExtensionFactory
//
//    class DefaultImpl() : ExtensionLoader {
//
//        override fun load(entryPointFile: File): NativeExtensionFactory {
//            println("load ExtensionEntryPoint from ${entryPointFile.absolutePath}")
//            ensureIsFile(entryPointFile)
//            val extensionClassLoader = createWorkerExtensionClassLoader(entryPointFile)
//            val currentClassLoader = Thread.currentThread().contextClassLoader
//            try {
//                Thread.currentThread().contextClassLoader = extensionClassLoader
//                for (entryPoint in ServiceLoader.load(
//                    NativeExtensionFactory::class.java,
//                    extensionClassLoader
//                )) {
//                    return entryPoint
//                }
//            } catch (t: Throwable) {
//                System.err.println("load error:")
//                t.printStackTrace()
//            } finally {
//                Thread.currentThread().contextClassLoader = currentClassLoader
//            }
//            throw Error("no ExtensionEntryPoint found in ${entryPointFile}")
//        }
//
//        private fun ensureIsFile(file: File) {
//            if (!file.isFile) {
//                throw Error("$file must be a file")
//            }
//        }
//
//        private fun createWorkerExtensionClassLoader(file: File): ClassLoader {
//            val urls: Array<URL> = arrayOf(toUrl(file))
//            return ExtensionClassLoader(
//                urls,
//                javaClass.classLoader
//            )
//        }
//
//        private fun toUrl(file: File): URL {
//            return try {
//                val res = file.toURI().toURL()
//                println("FILE: ${res}")
//                println(file.exists())
//                res
//            } catch (e: MalformedURLException) {
//                throw Error(e)
//            }
//        }
//    }
//}
//

package io.hamal.runner.extension

import java.net.URL
import java.net.URLClassLoader


internal class ExtensionClassLoader(
    urls: Array<URL>, private val urlClassLoader: URLClassLoader, private val parentClassLoader: ClassLoader
) : URLClassLoader(urls, null) {
    //    public WorkerExtensionClassLoader(URL[] urls, ClassLoader parentClassLoader) {
    //        super(urls, null);
    //        this.parentClassLoader = parentClassLoader;
    //    }
    constructor(
        urls: Array<URL>, parentClassLoader: ClassLoader
    ) : this(
        urls, URLClassLoader(urls, parentClassLoader), parentClassLoader
    )

    @Throws(ClassNotFoundException::class)
    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        println("Load Class ${name} $resolve")
        val loadedClass = loadClassByName(name)
        if (resolve) {
            resolveClass(loadedClass)
        }
        return loadedClass
    }

    @Throws(ClassNotFoundException::class)
    fun loadClassByName(name: String): Class<*> {
        val loadedClass = findLoadedClass(name)
        println("loadClassByName $name $loadedClass ")
        if (loadedClass != null) {
            return loadedClass
        }


        return if (loadWithParentClassLoader(name)) {
            println("parent")
            val res = parentClassLoader.loadClass(name)
            println("use parent class loader $name $res")
            res
        } else {
            println("url $name")
            try {
                val res = urlClassLoader.loadClass(name)
                println("use url class loader $name $res")
                res
            } catch (t: Throwable) {
                throw RuntimeException(t)
            }
        }
    }

    fun loadWithParentClassLoader(name: String): Boolean {
        return isFromExtensionSPI(name) || isFromLib(name)
    }

    private fun isFromExtensionSPI(name: String): Boolean {
        return name.startsWith("io.hamal.agent.extend.api")
    }

    private fun isFromLib(name: String): Boolean {
        return name.startsWith("io.hamal.lib")
    }
}

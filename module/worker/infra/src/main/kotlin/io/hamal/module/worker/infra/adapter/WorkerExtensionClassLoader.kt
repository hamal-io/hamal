package io.hamal.module.worker.infra.adapter

import java.net.URL
import java.net.URLClassLoader


internal class WorkerExtensionClassLoader(
    urls: Array<URL>, private val urlClassLoader: URLClassLoader, private val parentClassLoader: ClassLoader
) : URLClassLoader(urls, null) {
    //    public WorkerExtensionClassLoader(URL[] urls, ClassLoader parentClassLoader) {
    //        super(urls, null);
    //        this.parentClassLoader = parentClassLoader;
    //    }
    constructor(
        urls: Array<URL>, parentClassLoader: ClassLoader
    ) : this(
        urls, URLClassLoader(urls), parentClassLoader
    )

    @Throws(ClassNotFoundException::class)
    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        val loadedClass = loadClassByName(name)
        if (resolve) {
            resolveClass(loadedClass)
        }
        return loadedClass
    }

    @Throws(ClassNotFoundException::class)
    fun loadClassByName(name: String): Class<*> {
        val loadedClass = findLoadedClass(name)
        if (loadedClass != null) {
            return loadedClass
        }
        return if (loadWithParentClassLoader(name)) {
            parentClassLoader.loadClass(name)
        } else urlClassLoader.loadClass(name)
    }

    fun loadWithParentClassLoader(name: String): Boolean {
        return isFromExtensionSPI(name) || isFromLib(name)
    }

    private fun isFromExtensionSPI(name: String): Boolean {
        return name.startsWith("io.hamal.worker.extension.api")
    }

    private fun isFromLib(name: String): Boolean {
        return name.startsWith("io.hamal.lib")
    }
}

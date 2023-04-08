package io.hamal.module.worker.infra.service

import io.hamal.module.worker.infra.adapter.WorkerExtensionEntryPointLoader
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.File
import java.util.concurrent.TimeUnit

@Service
class WorkerService {

    @Scheduled(initialDelay = 100, fixedDelay = 1000, timeUnit = TimeUnit.MILLISECONDS)
    fun run() {
        println("Worker active")

        val entryPointLoader = WorkerExtensionEntryPointLoader.DefaultImpl()
        val x =
            entryPointLoader.load(File("/Users/ddymke/repo/hamal/module/worker/extension/impl/starter/build/libs/extension-starter.jar"))

        x.factories()
            .flatMap { it.load() }
            .forEach { it() }

        println(x)
    }

}
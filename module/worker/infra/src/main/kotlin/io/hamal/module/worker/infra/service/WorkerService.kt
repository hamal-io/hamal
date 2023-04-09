package io.hamal.module.worker.infra.service

import io.hamal.lib.domain_notification.DomainNotificationConsumer
import io.hamal.lib.domain_notification.QueueDomainNotification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class WorkerService {

    @Autowired
    lateinit var consumer: DomainNotificationConsumer

    @Scheduled(initialDelay = 100, fixedRate = 1, timeUnit = TimeUnit.MILLISECONDS)
    fun run() {
//            println("Worker active")
        val notification = consumer.poll(QueueDomainNotification.JobEnqueued::class.java.name)
        if (notification is QueueDomainNotification.JobEnqueued) {
            println("worker polled some work - $notification")
            println(Thread.currentThread().name)
        }

//    @Scheduled(initialDelay = 100, fixedDelay = 1000, timeUnit = TimeUnit.MILLISECONDS)
//    fun run() {
////        println("Worker active")
////
////        val entryPointLoader = WorkerExtensionLoader.DefaultImpl()
////        val x =
////            entryPointLoader.load(File("/Users/ddymke/repo/hamal/module/worker/extension/impl/starter/build/libs/extension-starter.jar"))
////
////        x.functionFactories()
////            .map { it() }
////            .forEach { it() }
////
////        println(x)
    }

}
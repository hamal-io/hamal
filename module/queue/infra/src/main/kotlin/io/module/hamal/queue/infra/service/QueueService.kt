//package io.module.hamal.queue.infra.service
//
//import io.hamal.lib.ddd.usecase.InvokeUseCasePort
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.stereotype.Service
//
//@Service
//class QueueService {
//
////    @Autowired
////    lateinit var consumer: DomainNotificationConsumer
//
//    @Autowired
//    lateinit var invokeUseCasePort: InvokeUseCasePort
//
////    @Scheduled(initialDelay = 100, fixedRate = 1, timeUnit = TimeUnit.MILLISECONDS)
////    fun run() {
//////            println("Queue active")
////        val notification = consumer.poll(JobDomainNotification.Scheduled::class.java.name)
////        if (notification is JobDomainNotification.Scheduled) {
////            println("queue polled $notification")
////            println(Thread.currentThread().name)
////            invokeUseCasePort.command(
////                EnqueueJobUseCase(
////                    notification.id,
////                    notification.regionId,
////                    notification.inputs
////                )
////            )
////        }
//////
//////        val entryPointLoader = WorkerExtensionLoader.DefaultImpl()
//////        val x =
//////            entryPointLoader.load(File("/Users/ddymke/repo/hamal/module/worker/extension/impl/starter/build/libs/extension-starter.jar"))
//////
//////        x.functionFactories()
//////            .map { it() }
//////            .forEach { it() }
//////
//////        println(x)
////    }
//
//}
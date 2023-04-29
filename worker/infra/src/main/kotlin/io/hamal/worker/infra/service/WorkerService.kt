//package io.hamal.module.worker.infra.service
//
//import org.springframework.stereotype.Service
//
//@Service
//class WorkerService {
//
////    @Autowired
////    lateinit var consumer: DomainNotificationConsumer
////
////    @Scheduled(initialDelay = 100, fixedRate = 1, timeUnit = TimeUnit.MILLISECONDS)
////    fun run() {
//////            println("Worker active")
////        val notification = consumer.poll(QueueDomainNotification.FlowEnqueued::class.java.name)
////        if (notification is QueueDomainNotification.FlowEnqueued) {
////            println("worker polled some work - $notification")
////            println(Thread.currentThread().name)
////        }
////
//////    @Scheduled(initialDelay = 100, fixedDelay = 1000, timeUnit = TimeUnit.MILLISECONDS)
//////    fun run() {
////////        println("Worker active")
////////
////////        val entryPointLoader = WorkerExtensionLoader.DefaultImpl()
////////        val x =
////////            entryPointLoader.load(File("/Users/ddymke/repo/hamal/module/worker/extension/impl/starter/build/libs/extension-starter.jar"))
////////
////////        x.functionFactories()
////////            .map { it() }
////////            .forEach { it() }
////////
////////        println(x)
////    }
//
//}
package io.hamal.backend.infra

import io.hamal.backend.request.flow_definition.FlowDefinitionRequest
import io.hamal.lib.ddd.usecase.InvokeUseCasePort
import io.hamal.lib.vo.RegionId
import io.hamal.lib.vo.base.DomainId
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

enum class DomainType {
    FlowDefinition,
    Trigger
}

@Serializable
data class Request<ID : DomainId>(
    val id: Int,
    val regionId: RegionId,
    val domainId: DomainId,
    val domainVersion: Int,
    val domainType: DomainType,
    val requestType: String

    // assembled request object here - maybe RequestContext?!
)

val requests: MutableList<Request<*>> = mutableListOf<Request<*>>()
val inProgressRequests: MutableList<Request<*>> = mutableListOf<Request<*>>()
val completedRequests: MutableList<Request<*>> = mutableListOf<Request<*>>()


@Service
class RequestProcessor(
    @Autowired val invokeUseCasePort: InvokeUseCasePort
) {

//    @Autowired
//    lateinit var invokeUseCasePort: InvokeUseCasePort

    @Scheduled(fixedDelay = 100, timeUnit = TimeUnit.MILLISECONDS)
    fun run() {
        // find request where domain id is not active yet

        if (!requests.isEmpty()) {
            if (inProgressRequests.isEmpty()) {
                println("Request in process")
            }

            val request = requests.removeFirst()
            println("I got called! with $request")

            inProgressRequests.add(request)

            invokeUseCasePort.invoke(
                FlowDefinitionRequest.CreateFlowDefinition(
                    request.regionId,
                )
            )


        }
    }

    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.MILLISECONDS)
    fun runCompleted() {
        if (!completedRequests.isEmpty()) {
            val result = completedRequests.removeFirst()
            println("Request was completed: $result")
            inProgressRequests.removeFirst()

        }
    }

}
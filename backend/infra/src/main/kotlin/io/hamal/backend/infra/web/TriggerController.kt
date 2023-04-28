package io.hamal.backend.infra.web

import io.hamal.backend.core.model.InvokedTrigger
import io.hamal.lib.ddd.base.DomainObject
import io.hamal.lib.ddd.usecase.InvokeUseCasePort
import io.hamal.lib.util.Snowflake
import io.hamal.lib.util.TimeUtils
import io.hamal.lib.vo.*
import io.hamal.lib.vo.port.GenerateDomainIdPort
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RestController
import java.util.*
import kotlin.reflect.KClass

@Serializable
class SomeTest(val id: JobId, val definitionId: JobDefinitionId, val regionId: RegionId)

interface NewUseCase<RESULT: DomainObject>{
}

 data class NewInvokeManualTriggerUseCase (
    val regionId: RegionId,
    val triggerId: TriggerId
): NewUseCase<InvokedTrigger>

interface NewUseCaseOperation<RESULT: DomainObject, USE_CASE: NewUseCase<RESULT>>{
     operator fun invoke(useCase: @UnsafeVariance USE_CASE): RESULT
}

class NewInvokeManualTriggerUseCaseOperation : NewUseCaseOperation<InvokedTrigger, NewInvokeManualTriggerUseCase>{
    override fun invoke(useCase: NewInvokeManualTriggerUseCase): InvokedTrigger {
        return InvokedTrigger(
            id = InvokedTriggerId(Snowflake.Id(1)),
            triggerId = TriggerId(Snowflake.Id(2)),
            reference = TriggerReference("some-ref"),
            jobDefinitionId = JobDefinitionId(Snowflake.Id(3)),
            accountId = AccountId(Snowflake.Id(4)),
            invokedAt = InvokedAt(TimeUtils.now())
        )
    }
}

interface NewGetUseCasePort{
    operator fun <RESULT : DomainObject, USE_CASE : NewUseCase<RESULT>> get(
        useCaseClass: KClass<USE_CASE>
    ): NewUseCaseOperation<RESULT, USE_CASE>
}

object NewUseCaseRepositoryImpl : NewGetUseCasePort{
    override fun <RESULT : DomainObject, USE_CASE : NewUseCase<RESULT>> get(useCaseClass: KClass<USE_CASE>): NewUseCaseOperation<RESULT, USE_CASE> {
        val operation = operations[useCaseClass]
            ?: useCaseClass.java.interfaces.asSequence().mapNotNull { operations[it.kotlin] }.firstOrNull()

        check(operation != null) { "Unable to find operation"}

        return operation as NewUseCaseOperation<RESULT, USE_CASE>
    }


    private val operations =
        mutableMapOf<KClass<out NewUseCase<*>>, NewUseCaseOperation<*, NewUseCase<*>>>(
            NewInvokeManualTriggerUseCase::class to NewInvokeManualTriggerUseCaseOperation() as NewUseCaseOperation<*, NewUseCase<*>>
        )
}

object NewUseCaseInvoker{
    fun <RESULT : DomainObject, USE_CASE : NewUseCase<RESULT>> newCommand(useCase: USE_CASE): RESULT{
        val operation = NewUseCaseRepositoryImpl[useCase::class]
        return operation(useCase)
    }
}

@RestController
open class JobController(
    @Autowired val invokeUseCasePort: InvokeUseCasePort,
    @Autowired val generateDomainId: GenerateDomainIdPort
) {

    //FIXME must operate on manual trigger instead of job def hack
    @PostMapping("/v1/triggers/manual/{triggerId}")
    fun manualTrigger(
        @PathVariable("triggerId") triggerId: String,
        @RequestAttribute("regionId") regionId: RegionId
    ): ResponseEntity<SomeTest> {

        val result = NewUseCaseInvoker.newCommand(NewInvokeManualTriggerUseCase(
            regionId = regionId, triggerId = generateDomainId(regionId, ::TriggerId)
        ))

        println(result)

        // assemble api model


//        val invokedTrigger : InvokedTrigger = invokeUseCasePort.newCommand(InvokeManualTriggerUseCase(
//                regionId = regionId,
//                triggerId = generateDomainId(regionId, ::TriggerId)
//            )
//        )
        return ResponseEntity.ok(
            SomeTest(
                generateDomainId(regionId, ::JobId),
                generateDomainId(regionId, ::JobDefinitionId),
                regionId
            )
        )
    }

}
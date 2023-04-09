package io.hamal.module.launchpad.infra.web

import io.hamal.lib.ddd.usecase.InvokeUseCasePort
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.domain.vo.base.RegionId
import io.hamal.module.launchpad.application.trigger.InvokeManualTriggerUseCase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


@RestController
open class JobController(
    @Autowired val invokeUseCasePort: InvokeUseCasePort
) {

    //FIXME must operate on manual trigger instead of job def hack
    @PostMapping("/v1/triggers/manual/{triggerId}")
    fun manualTrigger(
        @PathVariable("triggerId") triggerId: String
    ) {
        invokeUseCasePort.command(
            InvokeManualTriggerUseCase(
                regionId = RegionId("this"),
                triggerId = TriggerId(triggerId)
            )
        )
    }

}
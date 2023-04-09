package io.hamal.module.launchpad.core.job.model

import io.hamal.lib.domain.vo.JobDefinitionId
import io.hamal.lib.domain.vo.base.RegionId

class JobDefinition(
    val id: JobDefinitionId,
    val regionId: RegionId
) {

}
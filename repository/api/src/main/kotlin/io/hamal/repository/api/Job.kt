package io.hamal.repository.api

import io.hamal.lib.domain.vo.JobId

interface JobRepository : Repository {

}

interface JobReadonlyRepository : Repository {

}

data class JobEntity(
    val id: JobId

)
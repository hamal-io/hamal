package io.hamal.lib.sdk.service

import io.hamal.lib.domain.vo.JobId
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.domain.ApiWorkerJobs

interface JobService {
    fun poll(): ApiWorkerJobs

    fun complete(jobId: JobId)

    fun fail(jobId: JobId)
}

class DefaultJobService : JobService {
    override fun poll(): ApiWorkerJobs {
        return HttpTemplate("http://localhost:8084")
            .post("/v1/dequeue")
            .execute(ApiWorkerJobs::class)
    }

    override fun complete(jobId: JobId) {
        HttpTemplate("http://localhost:8084")
            .post("/v1/jobs/${jobId.value.value}/complete")
            .execute()
    }

    override fun fail(jobId: JobId) {
        HttpTemplate("http://localhost:8084")
            .post("/v1/jobs/${jobId.value.value}/fail")
            .execute()
    }

}
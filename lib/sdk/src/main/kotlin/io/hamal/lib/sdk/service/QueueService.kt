package io.hamal.lib.sdk.service

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.domain.ApiWorkerJobs

interface QueueService {
    fun poll(): ApiWorkerJobs
}

class DefaultQueueService : QueueService {
    override fun poll(): ApiWorkerJobs {
        return HttpTemplate("http://localhost:8084")
            .post("/v1/dequeue")
            .execute(ApiWorkerJobs::class)
    }

}
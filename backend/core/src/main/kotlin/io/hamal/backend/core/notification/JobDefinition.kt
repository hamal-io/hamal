package io.hamal.backend.core.notification

//sealed class JobDefinitionDomainNotification(regionId: RegionId) : DomainNotification(regionId) {
//
//    @DomainNotificationTopic("launchpad::job_enqueued")
//    class Created(
//        val id: JobDefinitionId,
//        regionId: RegionId,
//    ) : JobDefinitionDomainNotification(regionId)
//
//}
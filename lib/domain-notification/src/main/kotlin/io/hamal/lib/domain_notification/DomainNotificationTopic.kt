package io.hamal.lib.domain_notification

@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DomainNotificationTopic(val value: String)

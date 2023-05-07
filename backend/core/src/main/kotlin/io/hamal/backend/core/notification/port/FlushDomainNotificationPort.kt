package io.hamal.backend.core.notification.port

fun interface FlushDomainNotificationPort {
    operator fun invoke()
}

package io.hamal.backend.core.port.notification

fun interface FlushDomainNotificationPort {
    operator fun invoke()
}

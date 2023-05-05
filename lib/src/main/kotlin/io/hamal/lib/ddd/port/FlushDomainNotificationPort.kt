package io.hamal.lib.ddd.port

fun interface FlushDomainNotificationPort {
    operator fun invoke()
}

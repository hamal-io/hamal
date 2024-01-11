package io.hamal.repository.api.event

import io.hamal.repository.api.Account

data class AccountCreatedEvent(
    val account: Account,
) : PlatformEvent()


data class AccountConvertedEvent(
    val account: Account,
) : PlatformEvent()

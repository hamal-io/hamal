package io.hamal.repository.api.event

import io.hamal.repository.api.Account

@PlatformEventTopic("account::created")
data class AccountCreatedEvent(
    val account: Account,
) : PlatformEvent()


@PlatformEventTopic("account::converted")
data class AccountConvertedEvent(
    val account: Account,
) : PlatformEvent()

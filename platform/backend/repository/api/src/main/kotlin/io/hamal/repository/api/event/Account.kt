package io.hamal.repository.api.event

import io.hamal.repository.api.Account

data class AccountCreatedEvent(
    val account: Account,
) : InternalEvent()


data class AccountConvertedEvent(
    val account: Account,
) : InternalEvent()

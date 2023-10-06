package io.hamal.repository.api.event

import io.hamal.repository.api.Account
import kotlinx.serialization.Serializable

@Serializable
@PlatformEventTopic("account::created")
data class AccountCreatedEvent(
    val account: Account,
) : PlatformEvent()

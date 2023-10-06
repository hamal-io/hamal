package io.hamal.repository.api.event

import io.hamal.repository.api.Account
import kotlinx.serialization.Serializable

@Serializable
@HubEventTopic("account::created")
data class AccountCreatedEvent(
    val account: Account,
) : HubEvent()

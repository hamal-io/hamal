package io.hamal.lib.domain

import kotlinx.serialization.Serializable


@Serializable
data class SecretKey(val value: String)

@Serializable
data class SecretStore(val value: String)

@Serializable
data class SecretStoreIdentifier(val value: String)

//FIXME as vo with proper serialisation - should become key@store:identifiers
@Serializable
data class Secret(
    val key: SecretKey,
    val store: SecretStore,
    val storeIdentifier: SecretStoreIdentifier
)

//FIXME should be serialized to [ key@store:identifier, key2@store2...]
@Serializable
data class Secrets(val values: List<Secret>)
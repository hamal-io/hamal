package io.hamal.lib.domain.vo.base

import io.hamal.lib.common.ddd.ValueObject
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


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

//FIXME should be serialized to [ key@store:ident, key2@store2...]

@Serializable
abstract class Secrets : ValueObject.BaseImpl<List<Secret>>() {
    override fun toString(): String {
        return "${this::class.simpleName}(${value})"
    }
}

abstract class SecretsSerializer<SECRETS : Secrets>(
    val fn: (List<Secret>) -> SECRETS
) : KSerializer<SECRETS> {
    private val delegate = ListSerializer(Secret.serializer())
    override val descriptor = delegate.descriptor

    override fun deserialize(decoder: Decoder): SECRETS {
        return fn(delegate.deserialize(decoder))
    }

    override fun serialize(encoder: Encoder, value: SECRETS) {
        delegate.serialize(encoder, value.value)
    }
}



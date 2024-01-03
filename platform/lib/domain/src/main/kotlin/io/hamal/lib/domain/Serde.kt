package io.hamal.lib.domain

import io.hamal.lib.common.serialization.GsonFactoryBuilder
import io.hamal.lib.common.serialization.ValueObjectIdAdapter
import io.hamal.lib.common.serialization.ValueObjectStringAdapter
import io.hamal.lib.domain.request.Requested
import io.hamal.lib.domain.request.TypeAdapter
import io.hamal.lib.domain.vo.*
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.reflect.KClass

val gsonInstance = GsonFactoryBuilder
    .registerTypeAdapter(AuthId::class.java, ValueObjectIdAdapter(::AuthId))
    .registerTypeAdapter(AuthToken::class.java, ValueObjectStringAdapter(::AuthToken))
    .registerTypeAdapter(Email::class.java, ValueObjectStringAdapter(::Email))
    .registerTypeAdapter(Password::class.java, ValueObjectStringAdapter(::Password))
    .registerTypeAdapter(Requested::class.java, TypeAdapter())
    .registerTypeAdapter(GroupId::class.java, ValueObjectIdAdapter(::GroupId))
    .registerTypeAdapter(FlowId::class.java, ValueObjectIdAdapter(::FlowId))

    .registerTypeAdapter(RequestId::class.java, ValueObjectIdAdapter(::RequestId))
    .build()


object Serde {
    fun <TYPE : Any> serialize(src: TYPE): String {
        return gsonInstance.toJson(src)
    }

    fun <TYPE : Any> deserialize(content: String, src: KClass<TYPE>): TYPE {
        return gsonInstance.fromJson(content, src.java)
    }

    fun <TYPE : Any> deserialize(stream: InputStream, src: KClass<TYPE>): TYPE {
        return gsonInstance.fromJson(InputStreamReader(stream), src.java)
    }
}
package io.hamal.backend.event

import io.hamal.lib.common.Shard
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

sealed interface Event {
    val shard: Shard
}

@Serializable
sealed class SystemEvent : Event {
    val topic = this::class.topic()
}

fun <EVENT : SystemEvent> KClass<EVENT>.topic() =
    (annotations.find { annotation -> annotation.annotationClass == SystemEventTopic::class }
        ?: throw IllegalStateException("SystemEvent not annotated with @SystemEventTopic"))
        .let { it as SystemEventTopic }.value

@Serializable
data class TenantEvent(
    override val shard: Shard,
    val contentType: String,
    val value: ByteArray
) : Event


@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class SystemEventTopic(val value: String)
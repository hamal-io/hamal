package io.hamal.backend.event

import io.hamal.lib.domain.Shard
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

//FIXME SystemEvent : Event
//FIXME TenantEvent : Event

@Serializable
sealed class Event { //FIXME can this be an interface?!
    /**
     *FIXME maybe as function as each event should contain at least one domain object which has the shard already encoded
     */
    abstract val shard: Shard

    //FIXME move this out as there is a tenant event now
    open val topic: String by lazy {
        val topicAnnotation =
            this::class.annotations.find { annotation -> annotation.annotationClass == SystemEventTopic::class }
                ?: throw IllegalStateException("SystemEvent not annotated with @SystemEventTopic")
        (topicAnnotation as SystemEventTopic).value
    }
}


@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class SystemEventTopic(val value: String)


fun <EVENT : Event> KClass<EVENT>.topic() =
    annotations.find { annotation -> annotation.annotationClass == SystemEventTopic::class }
        .let { it as SystemEventTopic }.value

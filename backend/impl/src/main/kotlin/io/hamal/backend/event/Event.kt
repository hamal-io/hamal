package io.hamal.backend.event

import io.hamal.lib.common.Shard
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
            this::class.annotations.find { annotation -> annotation.annotationClass == EventTopic::class }
                ?: throw IllegalStateException("Event not annotated with @EventTopic")
        (topicAnnotation as EventTopic).value
    }
}


@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EventTopic(val value: String)


fun <EVENT : Event> KClass<EVENT>.topic() =
    annotations.find { annotation -> annotation.annotationClass == EventTopic::class }
        .let { it as EventTopic }.value

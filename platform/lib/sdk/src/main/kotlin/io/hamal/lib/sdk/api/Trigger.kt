package io.hamal.lib.sdk.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain._enum.TriggerStatus
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.request.TriggerCreateRequest
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpRequest
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiTriggerService.TriggerQuery
import io.hamal.lib.sdk.fold

data class ApiTriggerCreateReq(
    override val type: TriggerType,
    override val name: TriggerName,
    override val funcId: FuncId,
    override val inputs: TriggerInputs? = null,
    override val correlationId: CorrelationId? = null,
    override val duration: TriggerDuration? = null,
    override val topicId: TopicId? = null,
    override val hookId: HookId? = null,
    override val hookMethod: HookMethod? = null,
    override val cron: CronPattern? = null
) : TriggerCreateRequest

data class ApiTriggerCreateRequested(
    override val id: RequestId,
    override val status: RequestStatus,
    val triggerId: TriggerId,
    val groupId: GroupId,
    val namespaceId: NamespaceId
) : ApiRequested()


data class ApiTriggerStatusRequested(
    override val id: RequestId,
    override val status: RequestStatus,
    val triggerId: TriggerId,
    val triggerStatus: TriggerStatus
) : ApiRequested()

data class ApiTriggerList(
    val triggers: List<Trigger>
) : ApiObject() {
    sealed interface Trigger {
        val id: TriggerId
        val name: TriggerName
        val func: Func
        val namespace: Namespace
        val type: TriggerType

        data class Func(
            val id: FuncId,
            val name: FuncName
        )

        data class Namespace(
            val id: NamespaceId,
            val name: NamespaceName
        )

        object Adapter : JsonAdapter<Trigger> {
            override fun serialize(
                src: Trigger,
                typeOfSrc: java.lang.reflect.Type,
                context: JsonSerializationContext
            ): JsonElement {
                return context.serialize(src)
            }

            override fun deserialize(
                json: JsonElement,
                typeOfT: java.lang.reflect.Type,
                context: JsonDeserializationContext
            ): Trigger {
                val triggerType = json.asJsonObject.get("type").asString
                return context.deserialize(
                    json, (classMapping[triggerType]
                        ?: throw NotImplementedError("$triggerType not supported")).java
                )
            }

            private val classMapping = mapOf(
                "Event" to Event::class,
                "FixedRate" to FixedRate::class,
                "Hook" to Hook::class,
                "Cron" to Cron::class
            )
        }
    }

    class FixedRate(
        override val id: TriggerId,
        override val name: TriggerName,
        override val func: Trigger.Func,
        override val namespace: Trigger.Namespace,
        val duration: TriggerDuration
    ) : Trigger {
        override val type: TriggerType = TriggerType.FixedRate
    }

    class Event(
        override val id: TriggerId,
        override val name: TriggerName,
        override val func: Trigger.Func,
        override val namespace: Trigger.Namespace,
        val topic: Topic
    ) : Trigger {
        override val type: TriggerType = TriggerType.Event

        data class Topic(
            val id: TopicId,
            val name: TopicName
        )
    }

    class Hook(
        override val id: TriggerId,
        override val name: TriggerName,
        override val func: Trigger.Func,
        override val namespace: Trigger.Namespace,
        val hook: Hook
    ) : Trigger {
        override val type: TriggerType = TriggerType.Hook

        data class Hook(
            val id: HookId,
            val name: HookName,
            val method: HookMethod
        )
    }

    class Cron(
        override val id: TriggerId,
        override val name: TriggerName,
        override val func: Trigger.Func,
        override val namespace: Trigger.Namespace,
        val cron: CronPattern
    ) : Trigger {
        override val type: TriggerType = TriggerType.Cron
    }
}

sealed class ApiTrigger : ApiObject() {
    abstract val id: TriggerId
    abstract val type: TriggerType
    abstract val name: TriggerName
    abstract val func: Func
    abstract val namespace: Namespace
    abstract val inputs: TriggerInputs
    abstract val correlationId: CorrelationId?
    abstract val status: TriggerStatus

    data class Func(
        val id: FuncId,
        val name: FuncName
    )

    data class Namespace(
        val id: NamespaceId,
        val name: NamespaceName
    )

    object Adapter : JsonAdapter<ApiTrigger> {
        override fun serialize(
            src: ApiTrigger,
            typeOfSrc: java.lang.reflect.Type,
            context: JsonSerializationContext
        ): JsonElement {
            return context.serialize(src)
        }

        override fun deserialize(
            json: JsonElement,
            typeOfT: java.lang.reflect.Type,
            context: JsonDeserializationContext
        ): ApiTrigger {
            val triggerType = json.asJsonObject.get("type").asString
            return context.deserialize(
                json, (classMapping[triggerType]
                    ?: throw NotImplementedError("$triggerType not supported")).java
            )
        }

        private val classMapping = mapOf(
            "Event" to Event::class,
            "FixedRate" to FixedRate::class,
            "Hook" to Hook::class,
            "Cron" to Cron::class
        )
    }


    class FixedRate(
        override val id: TriggerId,
        override val name: TriggerName,
        override val func: Func,
        override val namespace: Namespace,
        override val inputs: TriggerInputs,
        override val status: TriggerStatus,
        override val correlationId: CorrelationId? = null,
        val duration: TriggerDuration
    ) : ApiTrigger() {
        override val type: TriggerType = TriggerType.FixedRate
    }

    class Event(
        override val id: TriggerId,
        override val name: TriggerName,
        override val func: Func,
        override val namespace: Namespace,
        override val inputs: TriggerInputs,
        override val status: TriggerStatus,
        override val correlationId: CorrelationId? = null,
        val topic: Topic
    ) : ApiTrigger() {
        override val type: TriggerType = TriggerType.Event

        data class Topic(
            val id: TopicId,
            val name: TopicName
        )
    }

    class Hook(
        override val id: TriggerId,
        override val name: TriggerName,
        override val func: Func,
        override val namespace: Namespace,
        override val inputs: TriggerInputs,
        override val status: TriggerStatus,
        override val correlationId: CorrelationId? = null,
        val hook: Hook
    ) : ApiTrigger() {
        override val type: TriggerType = TriggerType.Hook

        data class Hook(
            val id: HookId,
            val name: HookName,
            val method: HookMethod
        )
    }


    class Cron(
        override val id: TriggerId,
        override val name: TriggerName,
        override val func: Func,
        override val namespace: Namespace,
        override val inputs: TriggerInputs,
        override val status: TriggerStatus,
        override val correlationId: CorrelationId? = null,
        val cron: CronPattern
    ) : ApiTrigger() {
        override val type: TriggerType = TriggerType.Cron
    }

}


interface ApiTriggerService {
    fun create(namespaceId: NamespaceId, req: ApiTriggerCreateReq): ApiTriggerCreateRequested
    fun list(query: TriggerQuery): List<ApiTriggerList.Trigger>
    fun get(triggerId: TriggerId): ApiTrigger
    fun activate(triggerId: TriggerId): ApiTriggerStatusRequested
    fun deactivate(triggerId: TriggerId): ApiTriggerStatusRequested

    data class TriggerQuery(
        var afterId: FuncId = FuncId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(25),
        var funcIds: List<FuncId> = listOf(),
        var namespaceIds: List<NamespaceId> = listOf(),
        var groupIds: List<GroupId> = listOf()
    ) {
        fun setRequestParameters(req: HttpRequest) {
            req.parameter("after_id", afterId)
            req.parameter("limit", limit)
            if (funcIds.isNotEmpty()) req.parameter("func_ids", funcIds)
            if (namespaceIds.isNotEmpty()) req.parameter("namespace_ids", namespaceIds)
            if (groupIds.isNotEmpty()) req.parameter("group_ids", groupIds)
        }
    }
}


internal class ApiTriggerServiceImpl(
    private val template: HttpTemplate
) : ApiTriggerService {
    override fun create(namespaceId: NamespaceId, req: ApiTriggerCreateReq): ApiTriggerCreateRequested =
        template.post("/v1/namespaces/{namespaceId}/triggers")
            .path("namespaceId", namespaceId)
            .body(req)
            .execute()
            .fold(ApiTriggerCreateRequested::class)

    override fun list(query: TriggerQuery) =
        template.get("/v1/triggers")
            .also(query::setRequestParameters)
            .execute()
            .fold(ApiTriggerList::class)
            .triggers

    override fun get(triggerId: TriggerId) =
        template.get("/v1/triggers/{triggerId}")
            .path("triggerId", triggerId)
            .execute()
            .fold(ApiTrigger::class)

    override fun activate(triggerId: TriggerId): ApiTriggerStatusRequested =
        template.post("/v1/trigger/{triggerId}/activate")
            .path("triggerId", triggerId)
            .execute()
            .fold(ApiTriggerStatusRequested::class)


    override fun deactivate(triggerId: TriggerId): ApiTriggerStatusRequested =
        template.post("/v1/trigger/{triggerId}/deactivate")
            .path("triggerId", triggerId)
            .execute()
            .fold(ApiTriggerStatusRequested::class)
}
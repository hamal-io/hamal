package io.hamal.agent.extension.std.sys

import io.hamal.agent.extension.api.Extension
import io.hamal.agent.extension.api.ExtensionFunc
import io.hamal.agent.extension.api.ExtensionFuncInvocationContext
import io.hamal.lib.domain.req.*
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.script.api.value.*
import io.hamal.lib.sdk.domain.ListEventsResponse
import io.hamal.lib.sdk.domain.ListFuncsResponse
import io.hamal.lib.sdk.domain.ListTopicsResponse

class StdSysExtension : Extension {
    override fun create(): EnvValue {
        return EnvValue(
            ident = IdentValue("sys"),
            values = mapOf(
                IdentValue("_cfg") to TableValue(),
                IdentValue("invoke") to InvokeFunc(),
                IdentValue("func") to TableValue(
                    "create" to CreateFunc(),
                    "list" to ListFuncs()
                ),
                IdentValue("topic") to TableValue(
                    "create" to CreateTopic(),
                    "list" to ListTopics()
                ),
                IdentValue("evt") to TableValue(
                    "emitter" to CreateEventEmitter(),
                    "list" to ListEvents()
                ),
//                IdentValue("on") to TableValue(
//                    "complete" to TableValue(
//                        "emit" to CreateEventEmitter()
//                    ),
//                    "fail" to TableValue(
//
//                    )
//                )
            )
        )

    }
}

class CreateEventEmitter : ExtensionFunc() {
    override fun invoke(ctx: ExtensionFuncInvocationContext): Value {
        val topicId = ctx.parameters.first() as IdentValue

        return TableValue(
            "topic_id" to StringValue(topicId.value),
            "emit" to object : FuncValue<ExtensionFuncInvocationContext>() {
                override fun invoke(ctx: ExtensionFuncInvocationContext): Value {
                    println("EMIT")
                    println(ctx.env["topic_id"])

                    HttpTemplate("http://localhost:8084")
                        .post("/v1/topics/${ctx.env["topic_id"]}/events")
                        .header("Content-Type", "application/json")
                        .body("""{}""")
                        .execute(SubmittedAppendEventReq::class)

                    return NilValue
                }
            }
        )
    }
}

class ListEvents : ExtensionFunc() {
    override fun invoke(ctx: ExtensionFuncInvocationContext): Value {
        println("List Events")

        val f = ctx.parameters.first() as TableValue
        val topicId = when (val t = f["topic_id"]) {
            is IdentValue -> ctx.env["topic_id"]
            else -> t
        }

        val response = HttpTemplate("http://localhost:8084")
            .get("/v1/topics/$topicId/events")
            .execute(ListEventsResponse::class)
            .events
            .mapIndexed { idx, evt ->
                IdentValue((idx + 1).toString()) to TableValue(
                    "id" to StringValue(evt.id.value.toString()),
                    "content_type" to StringValue(evt.contentType.value),
//                    "content" to evt.content
                )
            }.toMap<IdentValue, Value>()

        return TableValue(response)
    }
}

class ListFuncs : ExtensionFunc() {
    override fun invoke(ctx: ExtensionFuncInvocationContext): Value {
        println("ListFuncs")

        val response = HttpTemplate("http://localhost:8084")
            .get("/v1/funcs")
            .execute(ListFuncsResponse::class)
            .funcs
            .mapIndexed { idx, func ->
                IdentValue((idx + 1).toString()) to TableValue(
                    "id" to StringValue(func.id.value.toString()),
                    "name" to StringValue(func.name.value)
                )
            }.toMap<IdentValue, Value>()

        return TableValue(response)
    }
}

class ListTopics : ExtensionFunc() {
    override fun invoke(ctx: ExtensionFuncInvocationContext): Value {
        println("ListFuncs")

        val response = HttpTemplate("http://localhost:8084")
            .get("/v1/topics")
            .execute(ListTopicsResponse::class)
            .topics
            .mapIndexed { idx, topic ->
                IdentValue((idx + 1).toString()) to TableValue(
                    "id" to StringValue(topic.id.value.toString()),
                    "name" to StringValue(topic.name.value)
                )
            }.toMap<IdentValue, Value>()

        return TableValue(response)
    }
}


class CreateTopic : ExtensionFunc() {
    override fun invoke(ctx: ExtensionFuncInvocationContext): Value {
        try {
            println("CREATE TOPIC")

            val f = ctx.parameters.first() as TableValue
            println(f)

            val r = CreateTopicReq(
                name = TopicName((f[IdentValue("name")] as StringValue).value)
            )

            val res = HttpTemplate("http://localhost:8084")
                .post("/v1/topics")
                .body(r)
                .execute(SubmittedCreateTopicReq::class)

            println(res)
            return StringValue(res.topicId.value.toString())
        } catch (t: Throwable) {
            t.printStackTrace()
            throw t
        }
    }
}

class InvokeFunc : ExtensionFunc() {
    override fun invoke(ctx: ExtensionFuncInvocationContext): Value {
        val funcId = (ctx.parameters.first() as StringValue).toString().replace("'", "")
        println("DEBUG: ${funcId}")

        HttpTemplate("http://localhost:8084")
            .post("/v1/funcs/${funcId}/exec")
            .body(
                InvokeOneshotReq(
                    inputs = InvocationInputs(),
                    correlationId = null
                )
            )
            .execute()
        return NilValue
    }

}


class CreateFunc : ExtensionFunc() {
    override fun invoke(ctx: ExtensionFuncInvocationContext): Value {
        try {
            println("CREATE_FUNC")

            val f = ctx.parameters.first() as TableValue
            println(f)

            val r = CreateFuncReq(
                name = FuncName((f[IdentValue("name")] as StringValue).value),
                inputs = FuncInputs(TableValue()),
                code = Code((f[IdentValue("run")] as CodeValue).value)
            )

            val res = HttpTemplate("http://localhost:8084")
                .post("/v1/funcs")
                .body(r)
                .execute(SubmittedCreateFuncReq::class)

            println(res)

            // FIXME await completion

            return StringValue(res.funcId.value.toString())
        } catch (t: Throwable) {
            t.printStackTrace()
            throw t
        }
    }
}
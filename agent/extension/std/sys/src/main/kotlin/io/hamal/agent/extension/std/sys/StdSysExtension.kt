package io.hamal.agent.extension.std.sys

import io.hamal.agent.extension.api.Extension
import io.hamal.lib.domain.Func
import io.hamal.lib.domain.HamalError
import io.hamal.lib.domain.req.*
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import io.hamal.lib.script.api.value.*
import io.hamal.lib.sdk.domain.ListEventsResponse
import io.hamal.lib.sdk.domain.ListFuncsResponse
import io.hamal.lib.sdk.domain.ListTopicsResponse

class StdSysExtension : Extension {
    override fun create(): EnvValue {
        return EnvValue(
            ident = IdentValue("sys"),
            values = TableValue(
                "_cfg" to TableValue(),
                "func" to TableValue(
                    "create" to CreateFunc(),
                    "get" to GetFunc(),
                    "list" to ListFuncs()
                ),
                "topic" to TableValue(
                    "create" to CreateTopic(),
                    "list" to ListTopics()
                ),
                "evt" to TableValue(
                    "emitter" to CreateEventEmitter(),
                    "list" to ListEvents()
                ),
            )
        )

    }
}

class CreateEventEmitter : FuncValue() {
    override fun invoke(ctx: FuncContext): Value {
        val topicId = ctx.params.first().value as IdentValue

        return TableValue(
            "topic_id" to StringValue(topicId.value),
            "emit" to object : FuncValue() {
                override fun invoke(ctx: FuncContext): Value {
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

class ListEvents : FuncValue() {
    override fun invoke(ctx: FuncContext): Value {
        println("List Events")

        val f = ctx.params.first().value as TableValue
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

class GetFunc : FuncValue() {
    override fun invoke(ctx: FuncContext): Value {
        println("GetFunc")
        val funcId = when (val value = ctx.params.first().value) {
            is StringValue -> value.value
            is IdentValue -> (ctx.env[value] as StringValue).value
            else -> TODO()
        }

        val response = HttpTemplate("http://localhost:8084")
            .get("/v1/funcs/${funcId}")
            .execute()

        if (response is SuccessHttpResponse) {
            return response.result(Func::class)
                .let { func ->
                    TableValue(
                        "id" to StringValue(func.id.value.toString()),
                        "name" to StringValue(func.name.value),
                        "inputs" to func.inputs.value,
                        "code" to CodeValue(func.code.value)
                    )
                }
        } else {
            require(response is ErrorHttpResponse)
            return response.error(HamalError::class)
                .let { error ->
                    ErrorValue(error.message ?: "An unknown error occurred")
                }
        }
    }
}

class ListFuncs : FuncValue() {
    override fun invoke(ctx: FuncContext): Value {
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

class ListTopics : FuncValue() {
    override fun invoke(ctx: FuncContext): Value {
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


class CreateTopic : FuncValue() {
    override fun invoke(ctx: FuncContext): Value {
        try {
            println("CREATE TOPIC")

            val f = ctx.params.first().value as TableValue
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

class InvokeFunc : FuncValue() {
    override fun invoke(ctx: FuncContext): Value {
        val funcId = (ctx.params.first().value as StringValue).toString().replace("'", "")
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


class CreateFunc : FuncValue() {
    override fun invoke(ctx: FuncContext): Value {
        try {
            val f = ctx.params.first().value as TableValue

            val inputs = when (val x = f["inputs"]) {
                is NilValue -> TableValue()
                is TableValue -> x
                else -> TODO()
            }

            val code = when (val x = f["code"]) {
                is NilValue -> CodeValue("")
                is CodeValue -> x
                is StringValue -> CodeValue(x)
                else -> TODO()
            }

            val r = CreateFuncReq(
                name = FuncName((f[IdentValue("name")] as StringValue).value),
                inputs = FuncInputs(inputs),
                code = code
            )

            val res = HttpTemplate("http://localhost:8084")
                .post("/v1/funcs")
                .body(r)
                .execute(SubmittedCreateFuncReq::class)


            return StringValue(res.funcId.value.toString())
        } catch (t: Throwable) {
            t.printStackTrace()
            throw t
        }
    }
}
package io.hamal.agent.extension.std.sys

//class StdSysExtension(
//    private val templateSupplier: () -> HttpTemplate
//) : Extension {
//    override fun create(): EnvValue {
//        return EnvValue(
//            ident = IdentValue("sys"),
//            values = TableValue(
//                "_cfg" to TableValue(),
//                "adhoc" to InvokeAdhoc(templateSupplier),
//                "execs" to TableValue(
//                    "list" to ListExecs(templateSupplier),
//                    "get" to GetExec(templateSupplier)
//                ),
//                "funcs" to TableValue(
//                    "create" to CreateFunc(templateSupplier),
//                    "get" to GetFunc(templateSupplier),
//                    "list" to ListFuncs(templateSupplier)
//                ),
//                "topics" to TableValue(
//                    "create" to CreateTopic(templateSupplier),
//                    "list" to ListTopics(templateSupplier)
//                ),
//                "events" to TableValue(
//                    "emitter" to CreateEventEmitter(templateSupplier),
//                    "list" to ListEvents(
//                        templateSupplier
//                    )
//                ),
//            )
//        )
//
//    }
//}
//
//class InvokeAdhoc(
//    private val templateSupplier: () -> HttpTemplate
//) : FuncValue() {
//    override fun invoke(ctx: FuncContext): Value {
//        try {
//            val f = ctx.params.first().value as TableValue
//
//            val inputs = when (val x = f["inputs"]) {
//                is NilValue -> TableValue()
//                is TableValue -> x
//                else -> TODO()
//            }
//
//            val code = when (val x = f["code"]) {
//                is NilValue -> CodeValue("")
//                is CodeValue -> x
//                is StringValue -> CodeValue(x)
//                else -> TODO()
//            }
//
//            val r = InvokeAdhocReq(
//                inputs = InvocationInputs(inputs),
//                code = code
//            )
//
//            val res = templateSupplier()
//                .post("/v1/adhoc")
//                .body(r)
//                .execute(SubmittedInvokeAdhocReq::class)
//
//            sleep(500)
//
//            return StringValue(res.execId.value.toString())
//        } catch (t: Throwable) {
//            t.printStackTrace()
//            throw t
//        }
//    }
//
//}
//
//class CreateEventEmitter(
//    private val templateSupplier: () -> HttpTemplate
//) : FuncValue() {
//    override fun invoke(ctx: FuncContext): Value {
//        val topicId = ctx.params.first().value as IdentValue
//
//        return TableValue(
//            "topic_id" to StringValue(topicId.value),
//            "emit" to object : FuncValue() {
//                override fun invoke(ctx: FuncContext): Value {
//                    println("EMIT")
//                    println(ctx.env["topic_id"])
//
//                    templateSupplier()
//                        .post("/v1/topics/${ctx.env["topic_id"]}/events")
//                        .header("Content-Type", "application/json")
//                        .body("""{}""")
//                        .execute(SubmittedAppendEventReq::class)
//
//                    sleep(500)
//
//                    return NilValue
//                }
//            }
//        )
//    }
//}
//
//class ListEvents(
//    private val templateSupplier: () -> HttpTemplate
//) : FuncValue() {
//    override fun invoke(ctx: FuncContext): Value {
//        println("List Events")
//
//        val f = ctx.params.first().value as TableValue
//        val topicId = when (val t = f["topic_id"]) {
//            is IdentValue -> ctx.env["topic_id"]
//            else -> t
//        }
//
//        val response = templateSupplier()
//            .get("/v1/topics/$topicId/events")
//            .execute(ListEventsResponse::class)
//            .events
//            .mapIndexed { idx, evt ->
//                IdentValue((idx + 1).toString()) to TableValue(
//                    "id" to StringValue(evt.id.value.toString()),
//                    "value" to evt.value
//                )
//            }.toMap<IdentValue, Value>()
//
//
//        return TableValue(response)
//    }
//}
//
//class GetFunc(
//    private val templateSupplier: () -> HttpTemplate
//) : FuncValue() {
//    override fun invoke(ctx: FuncContext): Value {
//        println("GetFunc")
//        val funcId = when (val value = ctx.params.first().value) {
//            is StringValue -> value.value
//            is IdentValue -> (ctx.env[value] as StringValue).value
//            else -> TODO()
//        }
//
//        val response = templateSupplier()
//            .get("/v1/funcs/${funcId}")
//            .execute()
//
//        if (response is SuccessHttpResponse) {
//            return response.result(Func::class)
//                .let { func ->
//                    TableValue(
//                        "id" to StringValue(func.id.value.toString()),
//                        "name" to StringValue(func.name.value),
//                        "inputs" to func.inputs.value,
//                        "code" to CodeValue(func.code.value)
//                    )
//                }
//        } else {
//            require(response is ErrorHttpResponse)
//            return response.error(HamalError::class)
//                .let { error ->
//                    ErrorValue(error.message ?: "An unknown error occurred")
//                }
//        }
//    }
//}
//
//class GetExec(
//    private val templateSupplier: () -> HttpTemplate
//) : FuncValue() {
//    override fun invoke(ctx: FuncContext): Value {
//        println("GetExec")
//        val execId = when (val value = ctx.params.first().value) {
//            is StringValue -> value.value
//            is IdentValue -> (ctx.env[value] as StringValue).value
//            else -> TODO()
//        }
//
//        val response = templateSupplier()
//            .get("/v1/execs/${execId}")
//            .execute()
//
//        if (response is SuccessHttpResponse) {
//            return response.result(Exec::class)
//                .let { exec ->
//                    TableValue(
//                        "id" to StringValue(exec.id.value.toString()),
//                        "status" to StringValue(exec.status.name),
//                        "inputs" to exec.inputs.value,
//                        "correlationId" to (exec.correlation?.correlationId?.value?.let { StringValue(it) }
//                            ?: NilValue),
//                        "code" to exec.code
//                    )
//                }
//        } else {
//            require(response is ErrorHttpResponse)
//            return response.error(HamalError::class)
//                .let { error ->
//                    ErrorValue(error.message ?: "An unknown error occurred")
//                }
//        }
//    }
//}
//
//class ListFuncs(
//    private val templateSupplier: () -> HttpTemplate
//) : FuncValue() {
//    override fun invoke(ctx: FuncContext): Value {
//        println("ListFuncs")
//
//        val response = templateSupplier()
//            .get("/v1/funcs")
//            .execute(ListFuncsResponse::class)
//            .funcs
//            .mapIndexed { idx, func ->
//                IdentValue((idx + 1).toString()) to TableValue(
//                    "id" to StringValue(func.id.value.toString()),
//                    "name" to StringValue(func.name.value)
//                )
//            }.toMap<IdentValue, Value>()
//
//        return TableValue(response)
//    }
//}
//
//class ListExecs(
//    private val templateSupplier: () -> HttpTemplate
//) : FuncValue() {
//    override fun invoke(ctx: FuncContext): Value {
//        println("ListExecs")
//
//        val response = templateSupplier()
//            .get("/v1/execs")
//            .execute(ListExecsResponse::class)
//            .execs
//            .mapIndexed { idx, exec ->
//                IdentValue((idx + 1).toString()) to TableValue(
//                    "id" to StringValue(exec.id.value.toString()),
//                    "status" to StringValue(exec.status.name)
//                )
//            }.toMap<IdentValue, Value>()
//
//        return TableValue(response)
//    }
//}
//
//
//class ListTopics(
//    private val templateSupplier: () -> HttpTemplate
//) : FuncValue() {
//    override fun invoke(ctx: FuncContext): Value {
//        println("ListFuncs")
//
//        val response = templateSupplier()
//            .get("/v1/topics")
//            .execute(ListTopicsResponse::class)
//            .topics
//            .mapIndexed { idx, topic ->
//                IdentValue((idx + 1).toString()) to TableValue(
//                    "id" to StringValue(topic.id.value.toString()),
//                    "name" to StringValue(topic.name.value)
//                )
//            }.toMap<IdentValue, Value>()
//
//        return TableValue(response)
//    }
//}
//
//
//class CreateTopic(
//    private val templateSupplier: () -> HttpTemplate
//) : FuncValue() {
//    override fun invoke(ctx: FuncContext): Value {
//        try {
//            println("CREATE TOPIC")
//
//            val f = ctx.params.first().value as TableValue
//            println(f)
//
//            val r = CreateTopicReq(
//                name = TopicName((f[IdentValue("name")] as StringValue).value)
//            )
//
//            val res = templateSupplier()
//                .post("/v1/topics")
//                .body(r)
//                .execute(SubmittedCreateTopicReq::class)
//            sleep(500)
//
//
//            println(res)
//            return StringValue(res.topicId.value.toString())
//        } catch (t: Throwable) {
//            t.printStackTrace()
//            throw t
//        }
//    }
//}
//
//class InvokeFunc(
//    private val templateSupplier: () -> HttpTemplate
//) : FuncValue() {
//    override fun invoke(ctx: FuncContext): Value {
//        val funcId = (ctx.params.first().value as StringValue).toString().replace("'", "")
//        println("DEBUG: ${funcId}")
//
//        templateSupplier()
//            .post("/v1/funcs/${funcId}/exec")
//            .body(
//                InvokeOneshotReq(
//                    inputs = InvocationInputs(),
//                    correlationId = null
//                )
//            )
//            .execute()
//
//        sleep(500)
//        return NilValue
//    }
//
//}
//
//
//class CreateFunc(
//    private val templateSupplier: () -> HttpTemplate
//) : FuncValue() {
//    override fun invoke(ctx: FuncContext): Value {
//        try {
//            val f = ctx.params.first().value as TableValue
//
//            val name: StringValue = when (val x = f["name"]) {
//                is NilValue -> StringValue("")
//                is StringValue -> x
//                is IdentValue -> ctx.env[x] as StringValue
//                else -> TODO()
//            }
//
//            val inputs = when (val x = f["inputs"]) {
//                is NilValue -> TableValue()
//                is TableValue -> x
//                else -> TODO()
//            }
//
//            val code = when (val x = f["code"]) {
//                is NilValue -> CodeValue("")
//                is CodeValue -> x
//                is StringValue -> CodeValue(x)
//                else -> TODO()
//            }
//
//            val r = CreateFuncReq(
//                name = FuncName(name.value),
//                inputs = FuncInputs(inputs),
//                code = code
//            )
//
//            val res = templateSupplier()
//                .post("/v1/funcs")
//                .body(r)
//                .execute(SubmittedCreateFuncReq::class)
//            sleep(500)
//
//            return StringValue(res.funcId.value.toString())
//        } catch (t: Throwable) {
//            t.printStackTrace()
//            throw t
//        }
//    }
//}
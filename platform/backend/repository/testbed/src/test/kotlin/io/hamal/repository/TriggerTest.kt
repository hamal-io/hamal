package io.hamal.repository

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain._enum.HookMethod.*
import io.hamal.lib.domain._enum.TriggerType.Event
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.repository.api.EventTrigger
import io.hamal.repository.api.FixedRateTrigger
import io.hamal.repository.api.TriggerCmdRepository.*
import io.hamal.repository.api.TriggerQueryRepository.TriggerQuery
import io.hamal.repository.api.TriggerRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import kotlin.math.abs
import kotlin.random.Random
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

internal class TriggerRepositoryTest : AbstractUnitTest() {

    @Nested
    inner class CreateFixedRate {
        @TestFactory
        fun `Creates fixed rate trigger`() = runWith(TriggerRepository::class) {
            val result = create(
                CreateFixedRateCmd(
                    id = CmdId(1),
                    triggerId = TriggerId(2),
                    funcId = FuncId(3),
                    groupId = GroupId(4),
                    flowId = FlowId(5),
                    name = TriggerName("trigger-name"),
                    inputs = TriggerInputs(
                        MapType(
                            mutableMapOf(
                                "hamal" to StringType("rocks")
                            )
                        )
                    ),
                    duration = 10.seconds
                )
            )

            with(result) {
                assertThat(id, equalTo(TriggerId(2)))
                assertThat(funcId, equalTo(FuncId(3)))
                assertThat(flowId, equalTo(FlowId(5)))
                assertThat(name, equalTo(TriggerName("trigger-name")))
                assertThat(inputs, equalTo(TriggerInputs(MapType(mutableMapOf("hamal" to StringType("rocks"))))))
                assertThat(duration, equalTo(10.seconds))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to create but same name already exists in flow`() =
            runWith(TriggerRepository::class) {

                createFixedRateTrigger(
                    triggerId = TriggerId(1),
                    flowId = FlowId(2),
                    groupId = GroupId(3),
                    name = TriggerName("first-trigger-name")
                )

                val exception = assertThrows<IllegalArgumentException> {
                    create(
                        CreateFixedRateCmd(
                            id = CmdId(2),
                            triggerId = TriggerId(5),
                            funcId = FuncId(4),
                            groupId = GroupId(3),
                            flowId = FlowId(2),
                            name = TriggerName("first-trigger-name"),
                            inputs = TriggerInputs(),
                            duration = 100.seconds,
                        )
                    )
                }

                assertThat(
                    exception.message,
                    equalTo("TriggerName(first-trigger-name) already exists in flow FlowId(2)")
                )

                verifyCount(1)
            }

        @TestFactory
        fun `Creates with same name but different flow`() =
            runWith(TriggerRepository::class) {

                createFixedRateTrigger(
                    triggerId = TriggerId(1),
                    flowId = FlowId(2),
                    groupId = GroupId(3),
                    name = TriggerName("trigger-name")
                )

                val result = create(
                    CreateFixedRateCmd(
                        id = CmdId(2),
                        triggerId = TriggerId(1111),
                        funcId = FuncId(4),
                        groupId = GroupId(3),
                        flowId = FlowId(22),
                        name = TriggerName("trigger-name"),
                        inputs = TriggerInputs(),
                        duration = 10.hours
                    )
                )

                with(result) {
                    assertThat(id, equalTo(TriggerId(1111)))
                    assertThat(funcId, equalTo(FuncId(4)))
                    assertThat(groupId, equalTo(GroupId(3)))
                    assertThat(flowId, equalTo(FlowId(22)))
                    assertThat(name, equalTo(TriggerName("trigger-name")))
                    assertThat(inputs, equalTo(TriggerInputs()))
                    assertThat(duration, equalTo(10.hours))
                }

                verifyCount(2)
            }

        @TestFactory
        fun `Tries to create but cmd with func id was already applied`() =
            runWith(TriggerRepository::class) {

                createFixedRateTrigger(
                    cmdId = CmdId(23456),
                    triggerId = TriggerId(5),
                    flowId = FlowId(2),
                    groupId = GroupId(3),
                    name = TriggerName("first-trigger-name")
                )


                val result = create(
                    CreateFixedRateCmd(
                        id = CmdId(23456),
                        triggerId = TriggerId(5),
                        funcId = FuncId(8),
                        groupId = GroupId(333),
                        flowId = FlowId(2222),
                        name = TriggerName("second-trigger-name"),
                        inputs = TriggerInputs(),
                        duration = 23.seconds,
                    )
                )

                with(result) {
                    assertThat(id, equalTo(TriggerId(5)))
                    assertThat(funcId, equalTo(FuncId(4)))
                    assertThat(groupId, equalTo(GroupId(3)))
                    assertThat(flowId, equalTo(FlowId(2)))
                    assertThat(name, equalTo(TriggerName("first-trigger-name")))
                    assertThat(inputs, equalTo(TriggerInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
                    assertThat(duration, equalTo(10.seconds))
                }

                verifyCount(1)
            }
    }

    @Nested
    inner class CreateEvent {
        @TestFactory
        fun `Creates event trigger`() = runWith(TriggerRepository::class) {
            val result = create(
                CreateEventCmd(
                    id = CmdId(1),
                    triggerId = TriggerId(2),
                    funcId = FuncId(3),
                    groupId = GroupId(4),
                    flowId = FlowId(5),
                    name = TriggerName("trigger-name"),
                    inputs = TriggerInputs(
                        MapType(
                            mutableMapOf(
                                "hamal" to StringType("rocks")
                            )
                        )
                    ),
                    topicId = TopicId(9)
                )
            )

            with(result) {
                assertThat(id, equalTo(TriggerId(2)))
                assertThat(funcId, equalTo(FuncId(3)))
                assertThat(flowId, equalTo(FlowId(5)))
                assertThat(name, equalTo(TriggerName("trigger-name")))
                assertThat(inputs, equalTo(TriggerInputs(MapType(mutableMapOf("hamal" to StringType("rocks"))))))
                assertThat(topicId, equalTo(TopicId(9)))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to create but same name already exists in flow`() =
            runWith(TriggerRepository::class) {

                createEventTrigger(
                    triggerId = TriggerId(1),
                    flowId = FlowId(2),
                    groupId = GroupId(3),
                    name = TriggerName("first-trigger-name")
                )

                val exception = assertThrows<IllegalArgumentException> {
                    create(
                        CreateEventCmd(
                            id = CmdId(2),
                            triggerId = TriggerId(5),
                            funcId = FuncId(4),
                            groupId = GroupId(3),
                            flowId = FlowId(2),
                            name = TriggerName("first-trigger-name"),
                            inputs = TriggerInputs(),
                            topicId = TopicId(9),
                        )
                    )
                }

                assertThat(
                    exception.message,
                    equalTo("TriggerName(first-trigger-name) already exists in flow FlowId(2)")
                )

                verifyCount(1)
            }

        @TestFactory
        fun `Creates with same name but different flow`() =
            runWith(TriggerRepository::class) {

                createEventTrigger(
                    triggerId = TriggerId(1),
                    flowId = FlowId(2),
                    groupId = GroupId(3),
                    name = TriggerName("trigger-name")
                )

                val result = create(
                    CreateEventCmd(
                        id = CmdId(2),
                        triggerId = TriggerId(1111),
                        funcId = FuncId(4),
                        groupId = GroupId(3),
                        flowId = FlowId(22),
                        name = TriggerName("trigger-name"),
                        inputs = TriggerInputs(),
                        topicId = TopicId(9)
                    )
                )

                with(result) {
                    assertThat(id, equalTo(TriggerId(1111)))
                    assertThat(funcId, equalTo(FuncId(4)))
                    assertThat(groupId, equalTo(GroupId(3)))
                    assertThat(flowId, equalTo(FlowId(22)))
                    assertThat(name, equalTo(TriggerName("trigger-name")))
                    assertThat(inputs, equalTo(TriggerInputs()))
                    assertThat(topicId, equalTo(TopicId(9)))
                }

                verifyCount(2)
            }

        @TestFactory
        fun `Tries to create but cmd with func id was already applied`() =
            runWith(TriggerRepository::class) {

                createEventTrigger(
                    cmdId = CmdId(23456),
                    triggerId = TriggerId(5),
                    flowId = FlowId(2),
                    groupId = GroupId(3),
                    name = TriggerName("first-trigger-name")
                )


                val result = create(
                    CreateEventCmd(
                        id = CmdId(23456),
                        triggerId = TriggerId(5),
                        funcId = FuncId(8),
                        groupId = GroupId(333),
                        flowId = FlowId(2222),
                        name = TriggerName("second-trigger-name"),
                        inputs = TriggerInputs(),
                        topicId = TopicId(999),
                    )
                )

                with(result) {
                    assertThat(id, equalTo(TriggerId(5)))
                    assertThat(funcId, equalTo(FuncId(4)))
                    assertThat(groupId, equalTo(GroupId(3)))
                    assertThat(flowId, equalTo(FlowId(2)))
                    assertThat(name, equalTo(TriggerName("first-trigger-name")))
                    assertThat(inputs, equalTo(TriggerInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
                    assertThat(topicId, equalTo(TopicId(9)))
                }

                verifyCount(1)
            }
    }

    @Nested
    inner class CreateHook {
        @TestFactory
        fun `Creates hook trigger`() = runWith(TriggerRepository::class) {
            val result = create(
                CreateHookCmd(
                    id = CmdId(1),
                    triggerId = TriggerId(2),
                    funcId = FuncId(3),
                    groupId = GroupId(4),
                    flowId = FlowId(5),
                    name = TriggerName("trigger-name"),
                    inputs = TriggerInputs(
                        MapType(
                            mutableMapOf(
                                "hamal" to StringType("rocks")
                            )
                        )
                    ),
                    hookId = HookId(9),
                    hookMethod = Patch
                )
            )

            with(result) {
                assertThat(id, equalTo(TriggerId(2)))
                assertThat(funcId, equalTo(FuncId(3)))
                assertThat(flowId, equalTo(FlowId(5)))
                assertThat(name, equalTo(TriggerName("trigger-name")))
                assertThat(inputs, equalTo(TriggerInputs(MapType(mutableMapOf("hamal" to StringType("rocks"))))))
                assertThat(hookId, equalTo(HookId(9)))
                assertThat(hookMethod, equalTo(Patch))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to create but same name already exists in flow`() =
            runWith(TriggerRepository::class) {

                createHookTrigger(
                    triggerId = TriggerId(1),
                    flowId = FlowId(2),
                    groupId = GroupId(3),
                    name = TriggerName("first-trigger-name")
                )

                val exception = assertThrows<IllegalArgumentException> {
                    create(
                        CreateEventCmd(
                            id = CmdId(2),
                            triggerId = TriggerId(5),
                            funcId = FuncId(4),
                            groupId = GroupId(3),
                            flowId = FlowId(2),
                            name = TriggerName("first-trigger-name"),
                            inputs = TriggerInputs(),
                            topicId = TopicId(9),
                        )
                    )
                }

                assertThat(
                    exception.message,
                    equalTo("TriggerName(first-trigger-name) already exists in flow FlowId(2)")
                )

                verifyCount(1)
            }

        @TestFactory
        fun `Creates with same name but different flow`() =
            runWith(TriggerRepository::class) {

                createHookTrigger(
                    triggerId = TriggerId(1),
                    flowId = FlowId(2),
                    groupId = GroupId(3),
                    name = TriggerName("trigger-name"),
                    hookMethod = Get
                )

                val result = create(
                    CreateHookCmd(
                        id = CmdId(2),
                        triggerId = TriggerId(1111),
                        funcId = FuncId(4),
                        groupId = GroupId(3),
                        flowId = FlowId(22),
                        name = TriggerName("trigger-name"),
                        inputs = TriggerInputs(),
                        hookId = HookId(9),
                        hookMethod = Post
                    )
                )

                with(result) {
                    assertThat(id, equalTo(TriggerId(1111)))
                    assertThat(funcId, equalTo(FuncId(4)))
                    assertThat(groupId, equalTo(GroupId(3)))
                    assertThat(flowId, equalTo(FlowId(22)))
                    assertThat(name, equalTo(TriggerName("trigger-name")))
                    assertThat(inputs, equalTo(TriggerInputs()))
                    assertThat(hookId, equalTo(HookId(9)))
                    assertThat(hookMethod, equalTo(Post))
                }

                verifyCount(2)
            }

        @TestFactory
        fun `Registers valid triggers`() = runWith(TriggerRepository::class) {
            createHookTrigger(
                triggerId = TriggerId(1),
                flowId = FlowId(2),
                groupId = GroupId(1),
                name = TriggerName("trigger-name-1"),
                funcId = FuncId(1),
                hookId = HookId(1),
                hookMethod = Get
            )

            createHookTrigger(
                triggerId = TriggerId(2),
                flowId = FlowId(2),
                groupId = GroupId(1),
                name = TriggerName("trigger-name-2"),
                funcId = FuncId(1),
                hookId = HookId(1),
                hookMethod = Post
            )

            createHookTrigger(
                triggerId = TriggerId(3),
                flowId = FlowId(2),
                groupId = GroupId(1),
                name = TriggerName("trigger-name-3"),
                funcId = FuncId(1),
                hookId = HookId(2),
                hookMethod = Get
            )

            createHookTrigger(
                triggerId = TriggerId(4),
                flowId = FlowId(2),
                groupId = GroupId(1),
                name = TriggerName("trigger-name-4"),
                funcId = FuncId(2),
                hookId = HookId(1),
                hookMethod = Get
            )

            verifyCount(4)
        }

        @TestFactory
        fun `Tries to register invalid hook triggers`() = runWith(TriggerRepository::class) {
            createHookTrigger(
                triggerId = TriggerId(1),
                flowId = FlowId(2),
                groupId = GroupId(1),
                name = TriggerName("trigger-name-1"),
                funcId = FuncId(1),
                hookId = HookId(1),
                hookMethod = Get
            )

            val exception = assertThrows<IllegalArgumentException> {
                createHookTrigger(
                    triggerId = TriggerId(2),
                    flowId = FlowId(2),
                    groupId = GroupId(1),
                    name = TriggerName("other-trigger"),
                    funcId = FuncId(1),
                    hookId = HookId(1),
                    hookMethod = Get
                )
            }
            assertThat(exception.message, equalTo("Trigger already exists"))
            verifyCount(1)
        }

        @TestFactory
        fun `Tries to create but cmd with func id was already applied`() =
            runWith(TriggerRepository::class) {

                createHookTrigger(
                    cmdId = CmdId(23456),
                    triggerId = TriggerId(5),
                    flowId = FlowId(2),
                    groupId = GroupId(3),
                    name = TriggerName("first-trigger-name")
                )


                val result = create(
                    CreateHookCmd(
                        id = CmdId(23456),
                        triggerId = TriggerId(5),
                        funcId = FuncId(8),
                        groupId = GroupId(333),
                        flowId = FlowId(2222),
                        name = TriggerName("second-trigger-name"),
                        inputs = TriggerInputs(),
                        hookId = HookId(999),
                        hookMethod = Get
                    )
                )

                with(result) {
                    assertThat(id, equalTo(TriggerId(5)))
                    assertThat(funcId, equalTo(FuncId(4)))
                    assertThat(groupId, equalTo(GroupId(3)))
                    assertThat(flowId, equalTo(FlowId(2)))
                    assertThat(name, equalTo(TriggerName("first-trigger-name")))
                    assertThat(inputs, equalTo(TriggerInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
                    assertThat(hookId, equalTo(HookId(9)))
                }

                verifyCount(1)
            }
    }

    @Nested
    inner class CreateCron {

        @TestFactory
        fun `Creates cron trigger`() = runWith(TriggerRepository::class) {
            val res = create(
                CreateCronCmd(
                    id = CmdGen(),
                    triggerId = TriggerId(2),
                    funcId = FuncId(3),
                    groupId = GroupId(4),
                    flowId = FlowId(5),
                    name = TriggerName("trigger-name"),
                    inputs = TriggerInputs(
                        MapType(
                            mutableMapOf(
                                "hamal" to StringType("rocks")
                            )
                        )
                    ),
                    cron = CronPattern("0 0 * * * *")
                )
            )

            with(res) {
                assertThat(id, equalTo(TriggerId(2)))
                assertThat(funcId, equalTo(FuncId(3)))
                assertThat(flowId, equalTo(FlowId(5)))
                assertThat(name, equalTo(TriggerName("trigger-name")))
                assertThat(inputs, equalTo(TriggerInputs(MapType(mutableMapOf("hamal" to StringType("rocks"))))))
                assertThat(cron, equalTo(CronPattern("0 0 * * * *")))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Creates with same name but different flow`() = runWith(TriggerRepository::class) {
            createCronTrigger(
                triggerId = TriggerId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                name = TriggerName("trigger-name"),
                cron = CronPattern("0 0 * * * *")
            )

            val result = create(
                CreateCronCmd(
                    id = CmdGen(),
                    triggerId = TriggerId(1111),
                    funcId = FuncId(4),
                    groupId = GroupId(3),
                    flowId = FlowId(22),
                    name = TriggerName("trigger-name"),
                    inputs = TriggerInputs(),
                    cron = CronPattern("0 0 * * * *")
                )
            )

            with(result) {
                assertThat(id, equalTo(TriggerId(1111)))
                assertThat(funcId, equalTo(FuncId(4)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(flowId, equalTo(FlowId(22)))
                assertThat(name, equalTo(TriggerName("trigger-name")))
                assertThat(inputs, equalTo(TriggerInputs()))
                assertThat(cron, equalTo(CronPattern("0 0 * * * *")))
            }

            verifyCount(2)
        }

        @TestFactory
        fun `Tries to create but cmd with func id was already applied`() =
            runWith(TriggerRepository::class) {
                createCronTrigger(
                    cmdId = CmdId(23456),
                    triggerId = TriggerId(5),
                    flowId = FlowId(2),
                    groupId = GroupId(3),
                    name = TriggerName("first-trigger-name")
                )


                val result = create(
                    CreateCronCmd(
                        id = CmdId(23456),
                        triggerId = TriggerId(5),
                        funcId = FuncId(8),
                        groupId = GroupId(333),
                        flowId = FlowId(2222),
                        name = TriggerName("second-trigger-name"),
                        inputs = TriggerInputs(),
                        cron = CronPattern("0 0 * * * *")
                    )
                )

                with(result) {
                    assertThat(id, equalTo(TriggerId(5)))
                    assertThat(funcId, equalTo(FuncId(4)))
                    assertThat(groupId, equalTo(GroupId(3)))
                    assertThat(flowId, equalTo(FlowId(2)))
                    assertThat(name, equalTo(TriggerName("first-trigger-name")))
                    assertThat(inputs, equalTo(TriggerInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
                    assertThat(cron, equalTo(CronPattern("0 0 * * * *")))
                }

                verifyCount(1)
            }
    }

    @Nested
    inner class ClearTest {

        @TestFactory
        fun `Nothing to clear`() = runWith(TriggerRepository::class) {
            clear()
            verifyCount(0)
        }

        @TestFactory
        fun `Clear table`() = runWith(TriggerRepository::class) {

            createFixedRateTrigger(
                triggerId = TriggerId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                name = TriggerName("already-exists")
            )

            createFixedRateTrigger(
                triggerId = TriggerId(2),
                flowId = FlowId(2),
                groupId = GroupId(3),
                name = TriggerName("to-update")
            )

            clear()
            verifyCount(0)
        }

    }

    @Nested
    inner class GetTest {
        @TestFactory
        fun `Get func by id`() = runWith(TriggerRepository::class) {
            createFixedRateTrigger(
                triggerId = TriggerId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                name = TriggerName("SomeTrigger")
            )

            with(get(TriggerId(1))) {
                require(this is FixedRateTrigger)

                assertThat(id, equalTo(TriggerId(1)))
                assertThat(funcId, equalTo(FuncId(4)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(flowId, equalTo(FlowId(2)))
                assertThat(name, equalTo(TriggerName("SomeTrigger")))
                assertThat(inputs, equalTo(TriggerInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
                assertThat(duration, equalTo(10.seconds))
            }
        }

        @TestFactory
        fun `Tries to get func by id but does not exist`() = runWith(TriggerRepository::class) {
            createEventTrigger(
                triggerId = TriggerId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                name = TriggerName("SomeTrigger")
            )

            val exception = assertThrows<NoSuchElementException> {
                get(TriggerId(111111))
            }
            assertThat(exception.message, equalTo("Trigger not found"))
        }
    }

    @Nested
    inner class FindTest {
        @TestFactory
        fun `Find func by id`() = runWith(TriggerRepository::class) {
            createFixedRateTrigger(
                triggerId = TriggerId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                name = TriggerName("SomeTrigger")
            )

            with(find(TriggerId(1))) {
                require(this is FixedRateTrigger)

                assertThat(id, equalTo(TriggerId(1)))
                assertThat(funcId, equalTo(FuncId(4)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(flowId, equalTo(FlowId(2)))
                assertThat(name, equalTo(TriggerName("SomeTrigger")))
                assertThat(inputs, equalTo(TriggerInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
                assertThat(duration, equalTo(10.seconds))
            }
        }

        @TestFactory
        fun `Tries to find func by id but does not exist`() = runWith(TriggerRepository::class) {
            createEventTrigger(
                triggerId = TriggerId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                name = TriggerName("SomeTrigger")
            )

            val result = find(TriggerId(111111))
            assertThat(result, nullValue())
        }
    }

    @Nested
    inner class ListAndCountTest {

        @TestFactory
        fun `With group ids`() = runWith(TriggerRepository::class) {
            setup()

            val query = TriggerQuery(
                groupIds = listOf(GroupId(5), GroupId(4)),
                limit = Limit(10)
            )

            assertThat(count(query), equalTo(2UL))
            val result = list(query)
            assertThat(result, hasSize(2))

            with(result[0]) {
                assertThat(id, equalTo(TriggerId(4)))
                assertThat(flowId, equalTo(FlowId(10)))
                assertThat(groupId, equalTo(GroupId(5)))
                assertThat(name, equalTo(TriggerName("Trigger")))
            }

            with(result[1]) {
                assertThat(id, equalTo(TriggerId(3)))
                assertThat(flowId, equalTo(FlowId(4)))
                assertThat(groupId, equalTo(GroupId(4)))
                assertThat(name, equalTo(TriggerName("Trigger")))
            }
        }

        @TestFactory
        fun `With func ids`() = runWith(TriggerRepository::class) {
            setup()

            val query = TriggerQuery(
                funcIds = listOf(FuncId(9), FuncId(10), FuncId(11)),
                limit = Limit(10)
            )

            assertThat(count(query), equalTo(2UL))
            val result = list(query)
            assertThat(result, hasSize(2))

            with(result[0]) {
                require(this is EventTrigger)
                assertThat(id, equalTo(TriggerId(2)))
            }

            with(result[1]) {
                require(this is FixedRateTrigger)
                assertThat(id, equalTo(TriggerId(1)))
            }
        }

        @TestFactory
        fun `With topic ids`() = runWith(TriggerRepository::class) {
            setup()

            val query = TriggerQuery(
                topicIds = listOf(TopicId(512)),
                limit = Limit(10)
            )

            assertThat(count(query), equalTo(1UL))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(TriggerId(7)))
                assertThat(name, equalTo(TriggerName("event-trigger-one")))
            }
        }

        @TestFactory
        fun `With hook ids`() = runWith(TriggerRepository::class) {
            setup()

            val query = TriggerQuery(
                hookIds = listOf(HookId(512)),
                limit = Limit(10)
            )

            assertThat(count(query), equalTo(1UL))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(TriggerId(5)))
                assertThat(name, equalTo(TriggerName("hook-trigger-one")))
            }
        }

        @TestFactory
        fun `With flow ids`() = runWith(TriggerRepository::class) {
            setup()

            val query = TriggerQuery(
                flowIds = listOf(FlowId(11)),
                limit = Limit(10)
            )

            assertThat(count(query), equalTo(1UL))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(TriggerId(5)))
                assertThat(name, equalTo(TriggerName("hook-trigger-one")))
            }
        }

        @TestFactory
        fun `With trigger types`() = runWith(TriggerRepository::class) {
            setup()

            val query = TriggerQuery(
                types = listOf(Event),
                groupIds = listOf(),
                limit = Limit(10)
            )

            assertThat(count(query), equalTo(3UL))
            val result = list(query).reversed()
            assertThat(result, hasSize(3))

            with(result[1]) {
                assertThat(id, equalTo(TriggerId(4)))
            }

            with(result[0]) {
                assertThat(id, equalTo(TriggerId(2)))
            }
        }

        @TestFactory
        fun `Limit`() = runWith(TriggerRepository::class) {
            setup()

            val query = TriggerQuery(
                groupIds = listOf(),
                limit = Limit(3)
            )

            assertThat(count(query), equalTo(7UL))
            val result = list(query)
            assertThat(result, hasSize(3))
        }

        @TestFactory
        fun `Skip and limit`() = runWith(TriggerRepository::class) {
            setup()

            val query = TriggerQuery(
                afterId = TriggerId(2),
                groupIds = listOf(),
                limit = Limit(1)
            )

            assertThat(count(query), equalTo(1UL))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(TriggerId(1)))
            }
        }

        private fun TriggerRepository.setup() {
            createFixedRateTrigger(
                triggerId = TriggerId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                name = TriggerName("Trigger"),
                funcId = FuncId(10)
            )

            createEventTrigger(
                triggerId = TriggerId(2),
                flowId = FlowId(3),
                groupId = GroupId(3),
                name = TriggerName("Trigger"),
                funcId = FuncId(11)
            )

            createFixedRateTrigger(
                triggerId = TriggerId(3),
                flowId = FlowId(4),
                groupId = GroupId(4),
                name = TriggerName("Trigger"),
                funcId = FuncId(12)
            )

            createEventTrigger(
                triggerId = TriggerId(4),
                flowId = FlowId(10),
                groupId = GroupId(5),
                name = TriggerName("Trigger"),
                funcId = FuncId(13)
            )

            createHookTrigger(
                triggerId = TriggerId(5),
                flowId = FlowId(11),
                groupId = GroupId(6),
                name = TriggerName("hook-trigger-one"),
                funcId = FuncId(14),
                hookId = HookId(512)
            )

            createHookTrigger(
                triggerId = TriggerId(6),
                flowId = FlowId(12),
                groupId = GroupId(6),
                name = TriggerName("hook-trigger-two"),
                funcId = FuncId(15),
                hookId = HookId(1024)
            )

            createEventTrigger(
                triggerId = TriggerId(7),
                flowId = FlowId(12),
                groupId = GroupId(6),
                name = TriggerName("event-trigger-one"),
                funcId = FuncId(15),
                topicId = TopicId(512)
            )

            createEventTrigger(
                triggerId = TriggerId(7),
                flowId = FlowId(12),
                groupId = GroupId(6),
                name = TriggerName("event-trigger-two"),
                funcId = FuncId(15),
                topicId = TopicId(1024)
            )
        }
    }


    private fun TriggerRepository.createFixedRateTrigger(
        triggerId: TriggerId,
        flowId: FlowId,
        name: TriggerName,
        groupId: GroupId,
        funcId: FuncId = FuncId(4),
        cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
    ) {
        create(
            CreateFixedRateCmd(
                id = cmdId,
                triggerId = triggerId,
                groupId = groupId,
                flowId = flowId,
                name = name,
                inputs = TriggerInputs(
                    MapType(
                        mutableMapOf(
                            "hamal" to StringType("rockz")
                        )
                    )
                ),
                funcId = funcId,
                duration = 10.seconds
            )
        )
    }

    private fun TriggerRepository.createEventTrigger(
        triggerId: TriggerId,
        flowId: FlowId,
        name: TriggerName,
        groupId: GroupId,
        funcId: FuncId = FuncId(4),
        topicId: TopicId = TopicId(9),
        cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
    ) {
        create(
            CreateEventCmd(
                id = cmdId,
                triggerId = triggerId,
                groupId = groupId,
                flowId = flowId,
                name = name,
                inputs = TriggerInputs(
                    MapType(
                        mutableMapOf(
                            "hamal" to StringType("rockz")
                        )
                    )
                ),
                funcId = funcId,
                topicId = topicId
            )
        )
    }

    private fun TriggerRepository.createHookTrigger(
        cmdId: CmdId = CmdGen(),
        triggerId: TriggerId,
        flowId: FlowId,
        name: TriggerName,
        groupId: GroupId,
        funcId: FuncId = FuncId(4),
        hookId: HookId = HookId(9),
        hookMethod: HookMethod = Post
    ) {
        create(
            CreateHookCmd(
                id = cmdId,
                triggerId = triggerId,
                groupId = groupId,
                flowId = flowId,
                name = name,
                inputs = TriggerInputs(
                    MapType(
                        mutableMapOf(
                            "hamal" to StringType("rockz")
                        )
                    )
                ),
                funcId = funcId,
                hookId = hookId,
                hookMethod = hookMethod
            )
        )
    }

    private fun TriggerRepository.createCronTrigger(
        triggerId: TriggerId,
        flowId: FlowId,
        name: TriggerName,
        groupId: GroupId,
        cron: CronPattern = CronPattern("0 0 * * * *"),
        funcId: FuncId = FuncId(4),
        cmdId: CmdId = CmdGen()
    ) {
        create(
            CreateCronCmd(
                id = cmdId,
                triggerId = triggerId,
                groupId = groupId,
                flowId = flowId,
                name = name,
                inputs = TriggerInputs(
                    MapType(
                        mutableMapOf(
                            "hamal" to StringType("rockz")
                        )
                    )
                ),
                funcId = funcId,
                cron = cron
            )
        )
    }

    private fun TriggerRepository.verifyCount(expected: Int) {
        verifyCount(expected) { }
    }

    private fun TriggerRepository.verifyCount(expected: Int, block: TriggerQuery.() -> Unit) {
        val counted = count(TriggerQuery(groupIds = listOf()).also(block))
        assertThat("number of trigger expected", counted, equalTo(expected.toULong()))
    }
}
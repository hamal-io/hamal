package io.hamal.repository

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain._enum.HookMethod.*
import io.hamal.lib.domain._enum.TriggerStatus.Active
import io.hamal.lib.domain._enum.TriggerStatus.Inactive
import io.hamal.lib.domain._enum.TriggerType.Event
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.Trigger
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
                    workspaceId = WorkspaceId(4),
                    namespaceId = NamespaceId(5),
                    name = TriggerName("trigger-name"),
                    inputs = TriggerInputs(HotObject.builder().set("hamal", "rocks").build()),
                    duration = TriggerDuration("PT10S")
                )
            )

            with(result) {
                assertThat(id, equalTo(TriggerId(2)))
                assertThat(funcId, equalTo(FuncId(3)))
                assertThat(namespaceId, equalTo(NamespaceId(5)))
                assertThat(name, equalTo(TriggerName("trigger-name")))
                assertThat(inputs, equalTo(TriggerInputs(HotObject.builder().set("hamal", "rocks").build())))
                assertThat(duration, equalTo(TriggerDuration("PT10S")))
                assertThat(status, equalTo(Active))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to create but same name already exists in namespace`() =
            runWith(TriggerRepository::class) {

                createFixedRateTrigger(
                    triggerId = TriggerId(1),
                    namespaceId = NamespaceId(2),
                    workspaceId = WorkspaceId(3),
                    name = TriggerName("first-trigger-name")
                )

                val exception = assertThrows<IllegalArgumentException> {
                    create(
                        CreateFixedRateCmd(
                            id = CmdId(2),
                            triggerId = TriggerId(5),
                            funcId = FuncId(4),
                            workspaceId = WorkspaceId(3),
                            namespaceId = NamespaceId(2),
                            name = TriggerName("first-trigger-name"),
                            inputs = TriggerInputs(),
                            duration = TriggerDuration(100.seconds.toIsoString()),
                        )
                    )
                }

                assertThat(exception.message, equalTo("first-trigger-name already exists in namespace 2"))

                verifyCount(1)
            }

        @TestFactory
        fun `Creates with same name but different namespace`() =
            runWith(TriggerRepository::class) {

                createFixedRateTrigger(
                    triggerId = TriggerId(1),
                    namespaceId = NamespaceId(2),
                    workspaceId = WorkspaceId(3),
                    name = TriggerName("trigger-name")
                )

                val result = create(
                    CreateFixedRateCmd(
                        id = CmdId(2),
                        triggerId = TriggerId(1111),
                        funcId = FuncId(4),
                        workspaceId = WorkspaceId(3),
                        namespaceId = NamespaceId(22),
                        name = TriggerName("trigger-name"),
                        inputs = TriggerInputs(),
                        duration = TriggerDuration(10.hours.toIsoString())
                    )
                )

                with(result) {
                    assertThat(id, equalTo(TriggerId(1111)))
                    assertThat(funcId, equalTo(FuncId(4)))
                    assertThat(workspaceId, equalTo(WorkspaceId(3)))
                    assertThat(namespaceId, equalTo(NamespaceId(22)))
                    assertThat(name, equalTo(TriggerName("trigger-name")))
                    assertThat(inputs, equalTo(TriggerInputs()))
                    assertThat(duration, equalTo(TriggerDuration("PT10H")))
                    assertThat(status, equalTo(Active))
                }

                verifyCount(2)
            }

        @TestFactory
        fun `Tries to create but cmd with trigger id was already applied`() =
            runWith(TriggerRepository::class) {

                createFixedRateTrigger(
                    cmdId = CmdId(23456),
                    triggerId = TriggerId(5),
                    namespaceId = NamespaceId(2),
                    workspaceId = WorkspaceId(3),
                    name = TriggerName("first-trigger-name")
                )


                val result = create(
                    CreateFixedRateCmd(
                        id = CmdId(23456),
                        triggerId = TriggerId(5),
                        funcId = FuncId(8),
                        workspaceId = WorkspaceId(333),
                        namespaceId = NamespaceId(2222),
                        name = TriggerName("second-trigger-name"),
                        inputs = TriggerInputs(),
                        duration = TriggerDuration(23.seconds.toIsoString()),
                    )
                )

                with(result) {
                    assertThat(id, equalTo(TriggerId(5)))
                    assertThat(funcId, equalTo(FuncId(4)))
                    assertThat(workspaceId, equalTo(WorkspaceId(3)))
                    assertThat(namespaceId, equalTo(NamespaceId(2)))
                    assertThat(name, equalTo(TriggerName("first-trigger-name")))
                    assertThat(inputs, equalTo(TriggerInputs(HotObject.builder().set("hamal", "rocks").build())))
                    assertThat(duration, equalTo(TriggerDuration("PT10S")))
                    assertThat(status, equalTo(Active))
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
                    workspaceId = WorkspaceId(4),
                    namespaceId = NamespaceId(5),
                    name = TriggerName("trigger-name"),
                    inputs = TriggerInputs(HotObject.builder().set("hamal", "rocks").build()),
                    topicId = TopicId(9)
                )
            )

            with(result) {
                assertThat(id, equalTo(TriggerId(2)))
                assertThat(funcId, equalTo(FuncId(3)))
                assertThat(namespaceId, equalTo(NamespaceId(5)))
                assertThat(name, equalTo(TriggerName("trigger-name")))
                assertThat(inputs, equalTo(TriggerInputs(HotObject.builder().set("hamal", "rocks").build())))
                assertThat(topicId, equalTo(TopicId(9)))
                assertThat(status, equalTo(Active))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to create but same name already exists in namespace`() =
            runWith(TriggerRepository::class) {

                createEventTrigger(
                    triggerId = TriggerId(1),
                    namespaceId = NamespaceId(2),
                    workspaceId = WorkspaceId(3),
                    name = TriggerName("first-trigger-name")
                )

                val exception = assertThrows<IllegalArgumentException> {
                    create(
                        CreateEventCmd(
                            id = CmdId(2),
                            triggerId = TriggerId(5),
                            funcId = FuncId(4),
                            workspaceId = WorkspaceId(3),
                            namespaceId = NamespaceId(2),
                            name = TriggerName("first-trigger-name"),
                            inputs = TriggerInputs(),
                            topicId = TopicId(9),
                        )
                    )
                }

                assertThat(exception.message, equalTo("first-trigger-name already exists in namespace 2"))

                verifyCount(1)
            }

        @TestFactory
        fun `Creates with same name but different namespace`() =
            runWith(TriggerRepository::class) {

                createEventTrigger(
                    triggerId = TriggerId(1),
                    namespaceId = NamespaceId(2),
                    workspaceId = WorkspaceId(3),
                    name = TriggerName("trigger-name")
                )

                val result = create(
                    CreateEventCmd(
                        id = CmdId(2),
                        triggerId = TriggerId(1111),
                        funcId = FuncId(4),
                        workspaceId = WorkspaceId(3),
                        namespaceId = NamespaceId(22),
                        name = TriggerName("trigger-name"),
                        inputs = TriggerInputs(),
                        topicId = TopicId(9)
                    )
                )

                with(result) {
                    assertThat(id, equalTo(TriggerId(1111)))
                    assertThat(funcId, equalTo(FuncId(4)))
                    assertThat(workspaceId, equalTo(WorkspaceId(3)))
                    assertThat(namespaceId, equalTo(NamespaceId(22)))
                    assertThat(name, equalTo(TriggerName("trigger-name")))
                    assertThat(inputs, equalTo(TriggerInputs()))
                    assertThat(topicId, equalTo(TopicId(9)))
                    assertThat(status, equalTo(Active))
                }

                verifyCount(2)
            }

        @TestFactory
        fun `Tries to create but cmd with func id was already applied`() =
            runWith(TriggerRepository::class) {

                createEventTrigger(
                    cmdId = CmdId(23456),
                    triggerId = TriggerId(5),
                    namespaceId = NamespaceId(2),
                    workspaceId = WorkspaceId(3),
                    name = TriggerName("first-trigger-name")
                )


                val result = create(
                    CreateEventCmd(
                        id = CmdId(23456),
                        triggerId = TriggerId(5),
                        funcId = FuncId(8),
                        workspaceId = WorkspaceId(333),
                        namespaceId = NamespaceId(2222),
                        name = TriggerName("second-trigger-name"),
                        inputs = TriggerInputs(),
                        topicId = TopicId(999),
                    )
                )

                with(result) {
                    assertThat(id, equalTo(TriggerId(5)))
                    assertThat(funcId, equalTo(FuncId(4)))
                    assertThat(workspaceId, equalTo(WorkspaceId(3)))
                    assertThat(namespaceId, equalTo(NamespaceId(2)))
                    assertThat(name, equalTo(TriggerName("first-trigger-name")))
                    assertThat(inputs, equalTo(TriggerInputs(HotObject.builder().set("hamal", "rocks").build())))
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
                    workspaceId = WorkspaceId(4),
                    namespaceId = NamespaceId(5),
                    name = TriggerName("trigger-name"),
                    inputs = TriggerInputs(HotObject.builder().set("hamal", "rocks").build()),
                    hookId = HookId(9),
                    hookMethod = Patch
                )
            )

            with(result) {
                assertThat(id, equalTo(TriggerId(2)))
                assertThat(funcId, equalTo(FuncId(3)))
                assertThat(namespaceId, equalTo(NamespaceId(5)))
                assertThat(name, equalTo(TriggerName("trigger-name")))
                assertThat(inputs, equalTo(TriggerInputs(HotObject.builder().set("hamal", "rocks").build())))
                assertThat(hookId, equalTo(HookId(9)))
                assertThat(hookMethod, equalTo(Patch))
                assertThat(status, equalTo(Active))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to create but same name already exists in namespace`() =
            runWith(TriggerRepository::class) {

                createHookTrigger(
                    triggerId = TriggerId(1),
                    namespaceId = NamespaceId(2),
                    workspaceId = WorkspaceId(3),
                    name = TriggerName("first-trigger-name")
                )

                val exception = assertThrows<IllegalArgumentException> {
                    create(
                        CreateEventCmd(
                            id = CmdId(2),
                            triggerId = TriggerId(5),
                            funcId = FuncId(4),
                            workspaceId = WorkspaceId(3),
                            namespaceId = NamespaceId(2),
                            name = TriggerName("first-trigger-name"),
                            inputs = TriggerInputs(),
                            topicId = TopicId(9),
                        )
                    )
                }

                assertThat(
                    exception.message,
                    equalTo("first-trigger-name already exists in namespace 2")
                )

                verifyCount(1)
            }

        @TestFactory
        fun `Creates with same name but different namespace`() =
            runWith(TriggerRepository::class) {

                createHookTrigger(
                    triggerId = TriggerId(1),
                    namespaceId = NamespaceId(2),
                    workspaceId = WorkspaceId(3),
                    name = TriggerName("trigger-name"),
                    hookMethod = Get
                )

                val result = create(
                    CreateHookCmd(
                        id = CmdId(2),
                        triggerId = TriggerId(1111),
                        funcId = FuncId(4),
                        workspaceId = WorkspaceId(3),
                        namespaceId = NamespaceId(22),
                        name = TriggerName("trigger-name"),
                        inputs = TriggerInputs(),
                        hookId = HookId(9),
                        hookMethod = Post
                    )
                )

                with(result) {
                    assertThat(id, equalTo(TriggerId(1111)))
                    assertThat(funcId, equalTo(FuncId(4)))
                    assertThat(workspaceId, equalTo(WorkspaceId(3)))
                    assertThat(namespaceId, equalTo(NamespaceId(22)))
                    assertThat(name, equalTo(TriggerName("trigger-name")))
                    assertThat(inputs, equalTo(TriggerInputs()))
                    assertThat(hookId, equalTo(HookId(9)))
                    assertThat(hookMethod, equalTo(Post))
                    assertThat(status, equalTo(Active))
                }

                verifyCount(2)
            }

        @TestFactory
        fun `Creates triggers with different hookId, funcId, hookMethod combinations`() =
            runWith(TriggerRepository::class) {
                createHookTrigger(
                    triggerId = TriggerId(1),
                    namespaceId = NamespaceId(2),
                    workspaceId = WorkspaceId(1),
                    name = TriggerName("trigger-name-1"),
                    funcId = FuncId(1),
                    hookId = HookId(1),
                    hookMethod = Get
                )

                createHookTrigger(
                    triggerId = TriggerId(2),
                    namespaceId = NamespaceId(2),
                    workspaceId = WorkspaceId(1),
                    name = TriggerName("trigger-name-2"),
                    funcId = FuncId(1),
                    hookId = HookId(1),
                    hookMethod = Post
                )

                createHookTrigger(
                    triggerId = TriggerId(3),
                    namespaceId = NamespaceId(2),
                    workspaceId = WorkspaceId(1),
                    name = TriggerName("trigger-name-3"),
                    funcId = FuncId(1),
                    hookId = HookId(2),
                    hookMethod = Get
                )

                createHookTrigger(
                    triggerId = TriggerId(4),
                    namespaceId = NamespaceId(2),
                    workspaceId = WorkspaceId(1),
                    name = TriggerName("trigger-name-4"),
                    funcId = FuncId(2),
                    hookId = HookId(1),
                    hookMethod = Get
                )

                verifyCount(4)
            }


        @TestFactory
        fun `Tries to create a trigger when hookId, funcId, hookMethod already exist`() =
            runWith(TriggerRepository::class) {
                createHookTrigger(
                    triggerId = TriggerId(1),
                    namespaceId = NamespaceId(2),
                    workspaceId = WorkspaceId(1),
                    name = TriggerName("trigger-name-1"),
                    funcId = FuncId(1),
                    hookId = HookId(1),
                    hookMethod = Get
                )

                val exception = assertThrows<IllegalArgumentException> {
                    createHookTrigger(
                        triggerId = TriggerId(2),
                        namespaceId = NamespaceId(2),
                        workspaceId = WorkspaceId(1),
                        name = TriggerName("other-trigger"),
                        funcId = FuncId(1),
                        hookId = HookId(1),
                        hookMethod = Get
                    )
                }
                assertThat(exception.message, equalTo("Trigger already exists"))
                assertThat(find(TriggerId(1)), notNullValue())
                verifyCount(1)
            }

        @TestFactory
        fun `Tries to create but cmd with func id was already applied`() =
            runWith(TriggerRepository::class) {

                createHookTrigger(
                    cmdId = CmdId(23456),
                    triggerId = TriggerId(5),
                    namespaceId = NamespaceId(2),
                    workspaceId = WorkspaceId(3),
                    name = TriggerName("first-trigger-name")
                )


                val result = create(
                    CreateHookCmd(
                        id = CmdId(23456),
                        triggerId = TriggerId(5),
                        funcId = FuncId(8),
                        workspaceId = WorkspaceId(333),
                        namespaceId = NamespaceId(2222),
                        name = TriggerName("second-trigger-name"),
                        inputs = TriggerInputs(),
                        hookId = HookId(999),
                        hookMethod = Get
                    )
                )

                with(result) {
                    assertThat(id, equalTo(TriggerId(5)))
                    assertThat(funcId, equalTo(FuncId(4)))
                    assertThat(workspaceId, equalTo(WorkspaceId(3)))
                    assertThat(namespaceId, equalTo(NamespaceId(2)))
                    assertThat(name, equalTo(TriggerName("first-trigger-name")))
                    assertThat(inputs, equalTo(TriggerInputs(HotObject.builder().set("hamal", "rocks").build())))
                    assertThat(hookId, equalTo(HookId(9)))
                    assertThat(status, equalTo(Active))
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
                    workspaceId = WorkspaceId(4),
                    namespaceId = NamespaceId(5),
                    name = TriggerName("trigger-name"),
                    inputs = TriggerInputs(HotObject.builder().set("hamal", "rocks").build()),
                    cron = CronPattern("0 0 * * * *")
                )
            )

            with(res) {
                assertThat(id, equalTo(TriggerId(2)))
                assertThat(funcId, equalTo(FuncId(3)))
                assertThat(namespaceId, equalTo(NamespaceId(5)))
                assertThat(name, equalTo(TriggerName("trigger-name")))
                assertThat(inputs, equalTo(TriggerInputs(HotObject.builder().set("hamal", "rocks").build())))
                assertThat(cron, equalTo(CronPattern("0 0 * * * *")))
                assertThat(status, equalTo(Active))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Creates with same name but different namespace`() = runWith(TriggerRepository::class) {
            createCronTrigger(
                triggerId = TriggerId(1),
                namespaceId = NamespaceId(2),
                workspaceId = WorkspaceId(3),
                name = TriggerName("trigger-name"),
                cron = CronPattern("0 0 * * * *")
            )

            val result = create(
                CreateCronCmd(
                    id = CmdGen(),
                    triggerId = TriggerId(1111),
                    funcId = FuncId(4),
                    workspaceId = WorkspaceId(3),
                    namespaceId = NamespaceId(22),
                    name = TriggerName("trigger-name"),
                    inputs = TriggerInputs(),
                    cron = CronPattern("0 0 * * * *")
                )
            )

            with(result) {
                assertThat(id, equalTo(TriggerId(1111)))
                assertThat(funcId, equalTo(FuncId(4)))
                assertThat(workspaceId, equalTo(WorkspaceId(3)))
                assertThat(namespaceId, equalTo(NamespaceId(22)))
                assertThat(name, equalTo(TriggerName("trigger-name")))
                assertThat(inputs, equalTo(TriggerInputs()))
                assertThat(cron, equalTo(CronPattern("0 0 * * * *")))
                assertThat(status, equalTo(Active))
            }

            verifyCount(2)
        }

        @TestFactory
        fun `Tries to create but cmd with func id was already applied`() =
            runWith(TriggerRepository::class) {
                createCronTrigger(
                    cmdId = CmdId(23456),
                    triggerId = TriggerId(5),
                    namespaceId = NamespaceId(2),
                    workspaceId = WorkspaceId(3),
                    name = TriggerName("first-trigger-name")
                )


                val result = create(
                    CreateCronCmd(
                        id = CmdId(23456),
                        triggerId = TriggerId(5),
                        funcId = FuncId(8),
                        workspaceId = WorkspaceId(333),
                        namespaceId = NamespaceId(2222),
                        name = TriggerName("second-trigger-name"),
                        inputs = TriggerInputs(),
                        cron = CronPattern("0 0 * * * *")
                    )
                )

                with(result) {
                    assertThat(id, equalTo(TriggerId(5)))
                    assertThat(funcId, equalTo(FuncId(4)))
                    assertThat(workspaceId, equalTo(WorkspaceId(3)))
                    assertThat(namespaceId, equalTo(NamespaceId(2)))
                    assertThat(name, equalTo(TriggerName("first-trigger-name")))
                    assertThat(inputs, equalTo(TriggerInputs(HotObject.builder().set("hamal", "rocks").build())))
                    assertThat(cron, equalTo(CronPattern("0 0 * * * *")))
                    assertThat(status, equalTo(Active))
                }

                verifyCount(1)
            }
    }

    @Nested
    inner class CreateEndpoint {

        @TestFactory
        fun `Creates endpoint trigger`() = runWith(TriggerRepository::class) {
            val res = create(
                CreateEndpointCmd(
                    id = CmdGen(),
                    triggerId = TriggerId(2),
                    funcId = FuncId(3),
                    workspaceId = WorkspaceId(4),
                    namespaceId = NamespaceId(5),
                    name = TriggerName("trigger-name"),
                    inputs = TriggerInputs(HotObject.builder().set("hamal", "rocks").build()),
                    endpointId = EndpointId(6)
                )
            )

            with(res) {
                assertThat(id, equalTo(TriggerId(2)))
                assertThat(funcId, equalTo(FuncId(3)))
                assertThat(namespaceId, equalTo(NamespaceId(5)))
                assertThat(name, equalTo(TriggerName("trigger-name")))
                assertThat(inputs, equalTo(TriggerInputs(HotObject.builder().set("hamal", "rocks").build())))
                assertThat(endpointId, equalTo(EndpointId(6)))
                assertThat(status, equalTo(Active))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Creates with same name but different namespace`() = runWith(TriggerRepository::class) {
            createEndpointTrigger(
                triggerId = TriggerId(1),
                namespaceId = NamespaceId(2),
                workspaceId = WorkspaceId(3),
                name = TriggerName("trigger-name"),
                endpointId = EndpointId(4)
            )

            val result = create(
                CreateEndpointCmd(
                    id = CmdGen(),
                    triggerId = TriggerId(1111),
                    funcId = FuncId(5555),
                    workspaceId = WorkspaceId(3),
                    namespaceId = NamespaceId(22),
                    name = TriggerName("trigger-name"),
                    inputs = TriggerInputs(),
                    endpointId = EndpointId(4)
                )
            )

            with(result) {
                assertThat(id, equalTo(TriggerId(1111)))
                assertThat(funcId, equalTo(FuncId(5555)))
                assertThat(workspaceId, equalTo(WorkspaceId(3)))
                assertThat(namespaceId, equalTo(NamespaceId(22)))
                assertThat(name, equalTo(TriggerName("trigger-name")))
                assertThat(inputs, equalTo(TriggerInputs()))
                assertThat(endpointId, equalTo(EndpointId(4)))
                assertThat(status, equalTo(Active))
            }

            verifyCount(2)
        }

        @TestFactory
        fun `Tries to create but cmd with func id was already applied`() =
            runWith(TriggerRepository::class) {
                createEndpointTrigger(
                    cmdId = CmdId(23456),
                    triggerId = TriggerId(5),
                    namespaceId = NamespaceId(2),
                    workspaceId = WorkspaceId(3),
                    name = TriggerName("first-trigger-name")
                )


                val result = create(
                    CreateEndpointCmd(
                        id = CmdId(23456),
                        triggerId = TriggerId(5),
                        funcId = FuncId(8),
                        workspaceId = WorkspaceId(333),
                        namespaceId = NamespaceId(2222),
                        name = TriggerName("second-trigger-name"),
                        inputs = TriggerInputs(),
                        endpointId = EndpointId(4)
                    )
                )

                with(result) {
                    assertThat(id, equalTo(TriggerId(5)))
                    assertThat(funcId, equalTo(FuncId(4)))
                    assertThat(workspaceId, equalTo(WorkspaceId(3)))
                    assertThat(namespaceId, equalTo(NamespaceId(2)))
                    assertThat(name, equalTo(TriggerName("first-trigger-name")))
                    assertThat(inputs, equalTo(TriggerInputs(HotObject.builder().set("hamal", "rocks").build())))
                    assertThat(endpointId, equalTo(EndpointId(9)))
                    assertThat(status, equalTo(Active))
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
                namespaceId = NamespaceId(2),
                workspaceId = WorkspaceId(3),
                name = TriggerName("already-exists")
            )

            createFixedRateTrigger(
                triggerId = TriggerId(2),
                namespaceId = NamespaceId(2),
                workspaceId = WorkspaceId(3),
                name = TriggerName("to-update")
            )

            clear()
            verifyCount(0)
        }

    }

    @Nested
    inner class StatusTest {

        @TestFactory
        fun `Deactivates fixed rate trigger`() = runWith(TriggerRepository::class) {
            create(
                CreateFixedRateCmd(
                    id = CmdGen(),
                    triggerId = TriggerId(2),
                    funcId = FuncId(3),
                    workspaceId = WorkspaceId(4),
                    namespaceId = NamespaceId(5),
                    name = TriggerName("trigger-name"),
                    inputs = TriggerInputs(HotObject.builder().set("hamal", "rocks").build()),
                    duration = TriggerDuration(10.seconds.toIsoString())
                )
            )

            set(TriggerId(2), SetTriggerStatusCmd(CmdGen(), Inactive))

            with(get(TriggerId(2)) as Trigger.FixedRate) {
                assertThat(id, equalTo(TriggerId(2)))
                assertThat(funcId, equalTo(FuncId(3)))
                assertThat(namespaceId, equalTo(NamespaceId(5)))
                assertThat(name, equalTo(TriggerName("trigger-name")))
                assertThat(inputs, equalTo(TriggerInputs(HotObject.builder().set("hamal", "rocks").build())))
                assertThat(duration, equalTo(TriggerDuration("PT10S")))
                assertThat(status, equalTo(Inactive))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Activates fixed rate trigger twice`() = runWith(TriggerRepository::class) {
            create(
                CreateFixedRateCmd(
                    id = CmdGen(),
                    triggerId = TriggerId(2),
                    funcId = FuncId(3),
                    workspaceId = WorkspaceId(4),
                    namespaceId = NamespaceId(5),
                    name = TriggerName("trigger-name"),
                    inputs = TriggerInputs(HotObject.builder().set("hamal", "rocks").build()),
                    duration = TriggerDuration(10.seconds.toIsoString())
                )
            )

            repeat(5) {
                set(TriggerId(2), SetTriggerStatusCmd(CmdGen(), Active))
            }

            with(get(TriggerId(2)) as Trigger.FixedRate) {
                assertThat(id, equalTo(TriggerId(2)))
                assertThat(funcId, equalTo(FuncId(3)))


                assertThat(namespaceId, equalTo(NamespaceId(5)))
                assertThat(name, equalTo(TriggerName("trigger-name")))
                assertThat(inputs, equalTo(TriggerInputs(HotObject.builder().set("hamal", "rocks").build())))
                assertThat(duration, equalTo(TriggerDuration("PT10S")))
                assertThat(status, equalTo(Active))
            }

            verifyCount(1)
        }
    }

    @Nested
    inner class GetTest {
        @TestFactory
        fun `Get trigger by id`() = runWith(TriggerRepository::class) {
            createFixedRateTrigger(
                triggerId = TriggerId(1),
                namespaceId = NamespaceId(2),
                workspaceId = WorkspaceId(3),
                name = TriggerName("SomeTrigger")
            )

            with(get(TriggerId(1))) {
                require(this is Trigger.FixedRate)

                assertThat(id, equalTo(TriggerId(1)))
                assertThat(funcId, equalTo(FuncId(4)))
                assertThat(workspaceId, equalTo(WorkspaceId(3)))
                assertThat(namespaceId, equalTo(NamespaceId(2)))
                assertThat(name, equalTo(TriggerName("SomeTrigger")))
                assertThat(inputs, equalTo(TriggerInputs(HotObject.builder().set("hamal", "rocks").build())))
                assertThat(duration, equalTo(TriggerDuration("PT10S")))
                assertThat(status, equalTo(Active))
            }
        }

        @TestFactory
        fun `Tries to get trigger by id but does not exist`() = runWith(TriggerRepository::class) {
            createEventTrigger(
                triggerId = TriggerId(1),
                namespaceId = NamespaceId(2),
                workspaceId = WorkspaceId(3),
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
        fun `Find trigger by id`() = runWith(TriggerRepository::class) {
            createFixedRateTrigger(
                triggerId = TriggerId(1),
                namespaceId = NamespaceId(2),
                workspaceId = WorkspaceId(3),
                name = TriggerName("SomeTrigger")
            )

            with(find(TriggerId(1))) {
                require(this is Trigger.FixedRate)

                assertThat(id, equalTo(TriggerId(1)))
                assertThat(funcId, equalTo(FuncId(4)))
                assertThat(workspaceId, equalTo(WorkspaceId(3)))
                assertThat(namespaceId, equalTo(NamespaceId(2)))
                assertThat(name, equalTo(TriggerName("SomeTrigger")))
                assertThat(inputs, equalTo(TriggerInputs(HotObject.builder().set("hamal", "rocks").build())))
                assertThat(duration, equalTo(TriggerDuration("PT10S")))
                assertThat(status, equalTo(Active))
            }
        }

        @TestFactory
        fun `Tries to find trigger by id but does not exist`() = runWith(TriggerRepository::class) {
            createEventTrigger(
                triggerId = TriggerId(1),
                namespaceId = NamespaceId(2),
                workspaceId = WorkspaceId(3),
                name = TriggerName("SomeTrigger")
            )

            val result = find(TriggerId(111111))
            assertThat(result, nullValue())
        }
    }

    @Nested
    inner class ListAndCountTest {

        @TestFactory
        fun `With ids`() = runWith(TriggerRepository::class) {
            setup()

            val query = TriggerQuery(
                triggerIds = listOf(TriggerId(4), TriggerId(3)),
                limit = Limit(10)
            )

            assertThat(count(query), equalTo(Count(2)))
            val result = list(query)
            assertThat(result, hasSize(2))

            with(result[0]) {
                assertThat(id, equalTo(TriggerId(4)))
                assertThat(namespaceId, equalTo(NamespaceId(10)))
                assertThat(workspaceId, equalTo(WorkspaceId(5)))
                assertThat(name, equalTo(TriggerName("Trigger")))
            }

            with(result[1]) {
                assertThat(id, equalTo(TriggerId(3)))
                assertThat(namespaceId, equalTo(NamespaceId(4)))
                assertThat(workspaceId, equalTo(WorkspaceId(4)))
                assertThat(name, equalTo(TriggerName("Trigger")))
            }
        }

        @TestFactory
        fun `With workspace ids`() = runWith(TriggerRepository::class) {
            setup()

            val query = TriggerQuery(
                workspaceIds = listOf(WorkspaceId(5), WorkspaceId(4)),
                limit = Limit(10)
            )

            assertThat(count(query), equalTo(Count(2)))
            val result = list(query)
            assertThat(result, hasSize(2))

            with(result[0]) {
                assertThat(id, equalTo(TriggerId(4)))
                assertThat(namespaceId, equalTo(NamespaceId(10)))
                assertThat(workspaceId, equalTo(WorkspaceId(5)))
                assertThat(name, equalTo(TriggerName("Trigger")))
            }

            with(result[1]) {
                assertThat(id, equalTo(TriggerId(3)))
                assertThat(namespaceId, equalTo(NamespaceId(4)))
                assertThat(workspaceId, equalTo(WorkspaceId(4)))
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

            assertThat(count(query), equalTo(Count(2)))
            val result = list(query)
            assertThat(result, hasSize(2))

            with(result[0]) {
                require(this is Trigger.Event)
                assertThat(id, equalTo(TriggerId(2)))
            }

            with(result[1]) {
                require(this is Trigger.FixedRate)
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

            assertThat(count(query), equalTo(Count(1)))
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

            assertThat(count(query), equalTo(Count(1)))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(TriggerId(5)))
                assertThat(name, equalTo(TriggerName("hook-trigger-one")))
            }
        }

        @TestFactory
        fun `With endpoint ids`() = runWith(TriggerRepository::class) {
            setup()

            val query = TriggerQuery(
                endpointIds = listOf(EndpointId(2048)),
                limit = Limit(10)
            )

            assertThat(count(query), equalTo(Count(1)))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(TriggerId(9)))
                assertThat(name, equalTo(TriggerName("endpoint-trigger-one")))
            }
        }

        @TestFactory
        fun `With namespace ids`() = runWith(TriggerRepository::class) {
            setup()

            val query = TriggerQuery(
                namespaceIds = listOf(NamespaceId(11)),
                limit = Limit(10)
            )

            assertThat(count(query), equalTo(Count(1)))
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
                workspaceIds = listOf(),
                limit = Limit(10)
            )

            assertThat(count(query), equalTo(Count(4)))
            val result = list(query).reversed()
            assertThat(result, hasSize(4))

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
                workspaceIds = listOf(),
                limit = Limit(3)
            )

            assertThat(count(query), equalTo(Count(10)))
            val result = list(query)
            assertThat(result, hasSize(3))
        }

        @TestFactory
        fun `Skip and limit`() = runWith(TriggerRepository::class) {
            setup()

            val query = TriggerQuery(
                afterId = TriggerId(2),
                workspaceIds = listOf(),
                limit = Limit(1)
            )

            assertThat(count(query), equalTo(Count(1)))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(TriggerId(1)))
            }
        }

        private fun TriggerRepository.setup() {
            createFixedRateTrigger(
                triggerId = TriggerId(1),
                namespaceId = NamespaceId(2),
                workspaceId = WorkspaceId(3),
                name = TriggerName("Trigger"),
                funcId = FuncId(10)
            )

            createEventTrigger(
                triggerId = TriggerId(2),
                namespaceId = NamespaceId(3),
                workspaceId = WorkspaceId(3),
                name = TriggerName("Trigger"),
                funcId = FuncId(11)
            )

            createFixedRateTrigger(
                triggerId = TriggerId(3),
                namespaceId = NamespaceId(4),
                workspaceId = WorkspaceId(4),
                name = TriggerName("Trigger"),
                funcId = FuncId(12)
            )

            createEventTrigger(
                triggerId = TriggerId(4),
                namespaceId = NamespaceId(10),
                workspaceId = WorkspaceId(5),
                name = TriggerName("Trigger"),
                funcId = FuncId(13)
            )

            createHookTrigger(
                triggerId = TriggerId(5),
                namespaceId = NamespaceId(11),
                workspaceId = WorkspaceId(6),
                name = TriggerName("hook-trigger-one"),
                funcId = FuncId(14),
                hookId = HookId(512)
            )

            createHookTrigger(
                triggerId = TriggerId(6),
                namespaceId = NamespaceId(12),
                workspaceId = WorkspaceId(6),
                name = TriggerName("hook-trigger-two"),
                funcId = FuncId(15),
                hookId = HookId(1024)
            )

            createEventTrigger(
                triggerId = TriggerId(7),
                namespaceId = NamespaceId(12),
                workspaceId = WorkspaceId(6),
                name = TriggerName("event-trigger-one"),
                funcId = FuncId(15),
                topicId = TopicId(512)
            )

            createEventTrigger(
                triggerId = TriggerId(8),
                namespaceId = NamespaceId(12),
                workspaceId = WorkspaceId(6),
                name = TriggerName("event-trigger-two"),
                funcId = FuncId(15),
                topicId = TopicId(1024)
            )

            createEndpointTrigger(
                triggerId = TriggerId(9),
                namespaceId = NamespaceId(13),
                workspaceId = WorkspaceId(6),
                name = TriggerName("endpoint-trigger-one"),
                funcId = FuncId(14),
                endpointId = EndpointId(2048)
            )

            createEndpointTrigger(
                triggerId = TriggerId(10),
                namespaceId = NamespaceId(14),
                workspaceId = WorkspaceId(6),
                name = TriggerName("endpoint-trigger-two"),
                funcId = FuncId(15),
                endpointId = EndpointId(4096)
            )
        }
    }


    private fun TriggerRepository.createFixedRateTrigger(
        triggerId: TriggerId,
        namespaceId: NamespaceId,
        name: TriggerName,
        workspaceId: WorkspaceId,
        funcId: FuncId = FuncId(4),
        cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
    ) {
        create(
            CreateFixedRateCmd(
                id = cmdId,
                triggerId = triggerId,
                workspaceId = workspaceId,
                namespaceId = namespaceId,
                name = name,
                inputs = TriggerInputs(HotObject.builder().set("hamal", "rocks").build()),
                funcId = funcId,
                duration = TriggerDuration(10.seconds.toIsoString())
            )
        )
    }

    private fun TriggerRepository.createEventTrigger(
        triggerId: TriggerId,
        namespaceId: NamespaceId,
        name: TriggerName,
        workspaceId: WorkspaceId,
        funcId: FuncId = FuncId(4),
        topicId: TopicId = TopicId(9),
        cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
    ) {
        create(
            CreateEventCmd(
                id = cmdId,
                triggerId = triggerId,
                workspaceId = workspaceId,
                namespaceId = namespaceId,
                name = name,
                inputs = TriggerInputs(HotObject.builder().set("hamal", "rocks").build()),
                funcId = funcId,
                topicId = topicId
            )
        )
    }

    private fun TriggerRepository.createHookTrigger(
        cmdId: CmdId = CmdGen(),
        triggerId: TriggerId,
        namespaceId: NamespaceId,
        name: TriggerName,
        workspaceId: WorkspaceId,
        funcId: FuncId = FuncId(4),
        hookId: HookId = HookId(9),
        hookMethod: HookMethod = Post
    ) {
        create(
            CreateHookCmd(
                id = cmdId,
                triggerId = triggerId,
                workspaceId = workspaceId,
                namespaceId = namespaceId,
                name = name,
                inputs = TriggerInputs(HotObject.builder().set("hamal", "rocks").build()),
                funcId = funcId,
                hookId = hookId,
                hookMethod = hookMethod
            )
        )
    }

    private fun TriggerRepository.createCronTrigger(
        triggerId: TriggerId,
        namespaceId: NamespaceId,
        name: TriggerName,
        workspaceId: WorkspaceId,
        cron: CronPattern = CronPattern("0 0 * * * *"),
        funcId: FuncId = FuncId(4),
        cmdId: CmdId = CmdGen()
    ) {
        create(
            CreateCronCmd(
                id = cmdId,
                triggerId = triggerId,
                workspaceId = workspaceId,
                namespaceId = namespaceId,
                name = name,
                inputs = TriggerInputs(HotObject.builder().set("hamal", "rocks").build()),
                funcId = funcId,
                cron = cron
            )
        )
    }

    private fun TriggerRepository.createEndpointTrigger(
        triggerId: TriggerId,
        namespaceId: NamespaceId,
        name: TriggerName,
        workspaceId: WorkspaceId,
        endpointId: EndpointId = EndpointId(9),
        funcId: FuncId = FuncId(4),
        cmdId: CmdId = CmdGen()
    ) {
        create(
            CreateEndpointCmd(
                id = cmdId,
                triggerId = triggerId,
                workspaceId = workspaceId,
                namespaceId = namespaceId,
                name = name,
                inputs = TriggerInputs(HotObject.builder().set("hamal", "rocks").build()),
                funcId = funcId,
                endpointId = endpointId
            )
        )
    }


    private fun TriggerRepository.verifyCount(expected: Int) {
        verifyCount(expected) { }
    }

    private fun TriggerRepository.verifyCount(expected: Int, block: TriggerQuery.() -> Unit) {
        val counted = count(TriggerQuery(workspaceIds = listOf()).also(block))
        assertThat("number of trigger expected", counted, equalTo(Count(expected)))
    }
}
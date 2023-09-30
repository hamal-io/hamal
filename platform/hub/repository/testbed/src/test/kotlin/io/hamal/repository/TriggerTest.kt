package io.hamal.repository

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.repository.api.FixedRateTrigger
import io.hamal.repository.api.TriggerCmdRepository.CreateEventCmd
import io.hamal.repository.api.TriggerCmdRepository.CreateFixedRateCmd
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
                    namespaceId = NamespaceId(5),
                    name = TriggerName("fixed-rate-trigger"),
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
                assertThat(namespaceId, equalTo(NamespaceId(5)))
                assertThat(name, equalTo(TriggerName("fixed-rate-trigger")))
                assertThat(inputs, equalTo(TriggerInputs(MapType(mutableMapOf("hamal" to StringType("rocks"))))))
                assertThat(duration, equalTo(10.seconds))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to create but same name already exists in namespace`() =
            runWith(TriggerRepository::class) {

                createFixedRateTrigger(
                    triggerId = TriggerId(1),
                    namespaceId = NamespaceId(2),
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
                            namespaceId = NamespaceId(2),
                            name = TriggerName("first-trigger-name"),
                            inputs = TriggerInputs(),
                            duration = 100.seconds,
                        )
                    )
                }

                assertThat(
                    exception.message,
                    equalTo("TriggerName(first-trigger-name) already exists in namespace NamespaceId(2)")
                )

                verifyCount(1)
            }

        @TestFactory
        fun `Creates with same name but different namespace`() =
            runWith(TriggerRepository::class) {

                createFixedRateTrigger(
                    triggerId = TriggerId(1),
                    namespaceId = NamespaceId(2),
                    groupId = GroupId(3),
                    name = TriggerName("trigger-name")
                )

                val result = create(
                    CreateFixedRateCmd(
                        id = CmdId(2),
                        triggerId = TriggerId(1111),
                        funcId = FuncId(4),
                        groupId = GroupId(3),
                        namespaceId = NamespaceId(22),
                        name = TriggerName("trigger-name"),
                        inputs = TriggerInputs(),
                        duration = 10.hours
                    )
                )

                with(result) {
                    assertThat(id, equalTo(TriggerId(1111)))
                    assertThat(funcId, equalTo(FuncId(4)))
                    assertThat(groupId, equalTo(GroupId(3)))
                    assertThat(namespaceId, equalTo(NamespaceId(22)))
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
                    namespaceId = NamespaceId(2),
                    groupId = GroupId(3),
                    name = TriggerName("first-trigger-name")
                )


                val result = create(
                    CreateFixedRateCmd(
                        id = CmdId(23456),
                        triggerId = TriggerId(5),
                        funcId = FuncId(8),
                        groupId = GroupId(333),
                        namespaceId = NamespaceId(2222),
                        name = TriggerName("second-trigger-name"),
                        inputs = TriggerInputs(),
                        duration = 23.seconds,
                    )
                )

                with(result) {
                    assertThat(id, equalTo(TriggerId(5)))
                    assertThat(funcId, equalTo(FuncId(4)))
                    assertThat(groupId, equalTo(GroupId(3)))
                    assertThat(namespaceId, equalTo(NamespaceId(2)))
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
                    namespaceId = NamespaceId(5),
                    name = TriggerName("fixed-rate-trigger"),
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
                assertThat(namespaceId, equalTo(NamespaceId(5)))
                assertThat(name, equalTo(TriggerName("fixed-rate-trigger")))
                assertThat(inputs, equalTo(TriggerInputs(MapType(mutableMapOf("hamal" to StringType("rocks"))))))
                assertThat(topicId, equalTo(TopicId(9)))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to create but same name already exists in namespace`() =
            runWith(TriggerRepository::class) {

                createEventTrigger(
                    triggerId = TriggerId(1),
                    namespaceId = NamespaceId(2),
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
                            namespaceId = NamespaceId(2),
                            name = TriggerName("first-trigger-name"),
                            inputs = TriggerInputs(),
                            topicId = TopicId(9),
                        )
                    )
                }

                assertThat(
                    exception.message,
                    equalTo("TriggerName(first-trigger-name) already exists in namespace NamespaceId(2)")
                )

                verifyCount(1)
            }

        @TestFactory
        fun `Creates with same name but different namespace`() =
            runWith(TriggerRepository::class) {

                createEventTrigger(
                    triggerId = TriggerId(1),
                    namespaceId = NamespaceId(2),
                    groupId = GroupId(3),
                    name = TriggerName("trigger-name")
                )

                val result = create(
                    CreateEventCmd(
                        id = CmdId(2),
                        triggerId = TriggerId(1111),
                        funcId = FuncId(4),
                        groupId = GroupId(3),
                        namespaceId = NamespaceId(22),
                        name = TriggerName("trigger-name"),
                        inputs = TriggerInputs(),
                        topicId = TopicId(9)
                    )
                )

                with(result) {
                    assertThat(id, equalTo(TriggerId(1111)))
                    assertThat(funcId, equalTo(FuncId(4)))
                    assertThat(groupId, equalTo(GroupId(3)))
                    assertThat(namespaceId, equalTo(NamespaceId(22)))
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
                    namespaceId = NamespaceId(2),
                    groupId = GroupId(3),
                    name = TriggerName("first-trigger-name")
                )


                val result = create(
                    CreateEventCmd(
                        id = CmdId(23456),
                        triggerId = TriggerId(5),
                        funcId = FuncId(8),
                        groupId = GroupId(333),
                        namespaceId = NamespaceId(2222),
                        name = TriggerName("second-trigger-name"),
                        inputs = TriggerInputs(),
                        topicId = TopicId(999),
                    )
                )

                with(result) {
                    assertThat(id, equalTo(TriggerId(5)))
                    assertThat(funcId, equalTo(FuncId(4)))
                    assertThat(groupId, equalTo(GroupId(3)))
                    assertThat(namespaceId, equalTo(NamespaceId(2)))
                    assertThat(name, equalTo(TriggerName("first-trigger-name")))
                    assertThat(inputs, equalTo(TriggerInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
                    assertThat(topicId, equalTo(TopicId(9)))
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
                groupId = GroupId(3),
                name = TriggerName("already-exists")
            )

            createFixedRateTrigger(
                triggerId = TriggerId(2),
                namespaceId = NamespaceId(2),
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
                namespaceId = NamespaceId(2),
                groupId = GroupId(3),
                name = TriggerName("SomeTrigger")
            )

            with(get(TriggerId(1))) {
                require(this is FixedRateTrigger)

                assertThat(id, equalTo(TriggerId(1)))
                assertThat(funcId, equalTo(FuncId(4)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(namespaceId, equalTo(NamespaceId(2)))
                assertThat(name, equalTo(TriggerName("SomeTrigger")))
                assertThat(inputs, equalTo(TriggerInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
                assertThat(duration, equalTo(10.seconds))
            }
        }

        @TestFactory
        fun `Tries to get func by id but does not exist`() = runWith(TriggerRepository::class) {
            createEventTrigger(
                triggerId = TriggerId(1),
                namespaceId = NamespaceId(2),
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
                namespaceId = NamespaceId(2),
                groupId = GroupId(3),
                name = TriggerName("SomeTrigger")
            )

            with(find(TriggerId(1))) {
                require(this is FixedRateTrigger)

                assertThat(id, equalTo(TriggerId(1)))
                assertThat(funcId, equalTo(FuncId(4)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(namespaceId, equalTo(NamespaceId(2)))
                assertThat(name, equalTo(TriggerName("SomeTrigger")))
                assertThat(inputs, equalTo(TriggerInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
                assertThat(duration, equalTo(10.seconds))
            }
        }

        @TestFactory
        fun `Tries to find func by id but does not exist`() = runWith(TriggerRepository::class) {
            createEventTrigger(
                triggerId = TriggerId(1),
                namespaceId = NamespaceId(2),
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
                limit = io.hamal.lib.common.domain.Limit(10)
            )

            assertThat(count(query), equalTo(2UL))
            val result = list(query)
            assertThat(result, hasSize(2))

            with(result[0]) {
                assertThat(id, equalTo(TriggerId(4)))
                assertThat(namespaceId, equalTo(NamespaceId(10)))
                assertThat(groupId, equalTo(GroupId(5)))
                assertThat(name, equalTo(TriggerName("Trigger")))
            }

            with(result[1]) {
                assertThat(id, equalTo(TriggerId(3)))
                assertThat(namespaceId, equalTo(NamespaceId(4)))
                assertThat(groupId, equalTo(GroupId(4)))
                assertThat(name, equalTo(TriggerName("Trigger")))
            }
        }


        @TestFactory
        fun `Limit`() = runWith(TriggerRepository::class) {
            setup()

            val query = TriggerQuery(
                groupIds = listOf(),
                limit = io.hamal.lib.common.domain.Limit(3)
            )

            assertThat(count(query), equalTo(4UL))
            val result = list(query)
            assertThat(result, hasSize(3))
        }

        @TestFactory
        fun `Skip and limit`() = runWith(TriggerRepository::class) {
            setup()

            val query = TriggerQuery(
                afterId = TriggerId(2),
                groupIds = listOf(),
                limit = io.hamal.lib.common.domain.Limit(1)
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
                namespaceId = NamespaceId(2),
                groupId = GroupId(3),
                name = TriggerName("Trigger")
            )

            createEventTrigger(
                triggerId = TriggerId(2),
                namespaceId = NamespaceId(3),
                groupId = GroupId(3),
                name = TriggerName("Trigger")
            )

            createFixedRateTrigger(
                triggerId = TriggerId(3),
                namespaceId = NamespaceId(4),
                groupId = GroupId(4),
                name = TriggerName("Trigger")
            )

            createEventTrigger(
                triggerId = TriggerId(4),
                namespaceId = NamespaceId(10),
                groupId = GroupId(5),
                name = TriggerName("Trigger")
            )
        }
    }
}

private fun TriggerRepository.createFixedRateTrigger(
    triggerId: TriggerId,
    namespaceId: NamespaceId,
    name: TriggerName,
    groupId: GroupId,
    cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
) {
    create(
        CreateFixedRateCmd(
            id = cmdId,
            triggerId = triggerId,
            groupId = groupId,
            namespaceId = namespaceId,
            name = name,
            inputs = TriggerInputs(
                MapType(
                    mutableMapOf(
                        "hamal" to StringType("rockz")
                    )
                )
            ),
            funcId = FuncId(4),
            duration = 10.seconds
        )
    )
}

private fun TriggerRepository.createEventTrigger(
    triggerId: TriggerId,
    namespaceId: NamespaceId,
    name: TriggerName,
    groupId: GroupId,
    cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
) {
    create(
        CreateEventCmd(
            id = cmdId,
            triggerId = triggerId,
            groupId = groupId,
            namespaceId = namespaceId,
            name = name,
            inputs = TriggerInputs(
                MapType(
                    mutableMapOf(
                        "hamal" to StringType("rockz")
                    )
                )
            ),
            funcId = FuncId(4),
            topicId = TopicId(9)
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
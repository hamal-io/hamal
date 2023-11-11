package io.hamal.repository

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.GroupName
import io.hamal.repository.api.GroupCmdRepository.CreateCmd
import io.hamal.repository.api.GroupQueryRepository.GroupQuery
import io.hamal.repository.api.GroupRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import kotlin.math.abs
import kotlin.random.Random

internal class GroupRepositoryTest : AbstractUnitTest() {

    @Nested
    inner class CreateTest {
        @TestFactory
        fun `Creates Group`() = runWith(GroupRepository::class) {
            val result = create(
                CreateCmd(
                    id = CmdId(1),
                    groupId = GroupId(123),
                    name = GroupName("SomeGroup"),
                    creatorId = AccountId(234)
                )
            )

            with(result) {
                assertThat(id, equalTo(GroupId(123)))
                assertThat(name, equalTo(GroupName("SomeGroup")))
                assertThat(creatorId, equalTo(AccountId(234)))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to create but same name already exists`() =
            runWith(GroupRepository::class) {

                createGroup(
                    cmdId = CmdId(1),
                    groupId = GroupId(123),
                    name = GroupName("SomeGroup"),
                    creatorId = AccountId(234)
                )

                val exception = assertThrows<IllegalArgumentException> {
                    create(
                        CreateCmd(
                            id = CmdId(2),
                            groupId = GroupId(321),
                            creatorId = AccountId(234),
                            name = GroupName("SomeGroup")
                        )
                    )
                }

                assertThat(
                    exception.message,
                    equalTo("GroupName(SomeGroup) already exists")
                )

                verifyCount(1)
            }

        @TestFactory
        fun `Tries to create but cmd with group id was already applied`() =
            runWith(GroupRepository::class) {

                createGroup(
                    cmdId = CmdId(23456),
                    groupId = GroupId(2),
                    name = GroupName("SomeGroup"),
                    creatorId = AccountId(3)
                )

                val result = create(
                    CreateCmd(
                        id = CmdId(23456),
                        groupId = GroupId(2),
                        creatorId = AccountId(234),
                        name = GroupName("AnotherGroupName")
                    )
                )

                with(result) {
                    assertThat(id, equalTo(GroupId(2)))
                    assertThat(name, equalTo(GroupName("SomeGroup")))
                    assertThat(creatorId, equalTo(AccountId(3)))
                }

                verifyCount(1)
            }
    }

    @Nested
    inner class ClearTest {

        @TestFactory
        fun `Nothing to clear`() = runWith(GroupRepository::class) {
            clear()
            verifyCount(0)
        }

        @TestFactory
        fun `Clear table`() = runWith(GroupRepository::class) {
            createGroup(
                name = GroupName("already-exists"),
                groupId = GroupId(1),
                creatorId = AccountId(2)
            )

            createGroup(
                name = GroupName("to-update"),
                groupId = GroupId(3),
                creatorId = AccountId(4)
            )

            clear()
            verifyCount(0)
        }

    }

    @Nested
    inner class GetTest {
        @TestFactory
        fun `Get group by id`() = runWith(GroupRepository::class) {
            createGroup(
                groupId = GroupId(1),
                creatorId = AccountId(3),
                name = GroupName("SomeGroup")
            )

            with(get(GroupId(1))) {
                assertThat(id, equalTo(GroupId(1)))
                assertThat(creatorId, equalTo(AccountId(3)))
                assertThat(name, equalTo(GroupName("SomeGroup")))
            }
        }

        @TestFactory
        fun `Tries to get group by id but does not exist`() = runWith(GroupRepository::class) {
            createGroup(
                groupId = GroupId(1),
                creatorId = AccountId(3),
                name = GroupName("SomeGroup")
            )

            val exception = assertThrows<NoSuchElementException> {
                get(GroupId(111111))
            }
            assertThat(exception.message, equalTo("Group not found"))
        }
    }

    @Nested
    inner class FindTest {
        @TestFactory
        fun `Find group by id`() = runWith(GroupRepository::class) {
            createGroup(
                groupId = GroupId(1),
                creatorId = AccountId(3),
                name = GroupName("SomeGroup")
            )

            with(find(GroupId(1))!!) {
                assertThat(id, equalTo(GroupId(1)))
                assertThat(creatorId, equalTo(AccountId(3)))
                assertThat(name, equalTo(GroupName("SomeGroup")))
            }
        }

        @TestFactory
        fun `Tries to find group by id but does not exist`() = runWith(GroupRepository::class) {
            createGroup(
                groupId = GroupId(1),
                creatorId = AccountId(3),
                name = GroupName("SomeGroup")
            )

            val result = find(GroupId(111111))
            assertThat(result, nullValue())
        }
    }

    @Nested
    inner class ListAndCountTest {

        @TestFactory
        fun `By ids`() = runWith(GroupRepository::class) {
            setup()

            val result = list(listOf(GroupId(111111), GroupId(3)))
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(GroupId(3)))
                assertThat(creatorId, equalTo(AccountId(4)))
                assertThat(name, equalTo(GroupName("Group-Three")))
            }
        }

        @TestFactory
        fun `With group ids`() = runWith(GroupRepository::class) {
            setup()

            val query = GroupQuery(
                groupIds = listOf(GroupId(4), GroupId(3)),
                limit = Limit(10)
            )

            assertThat(count(query), equalTo(2UL))
            val result = list(query)
            assertThat(result, hasSize(2))

            with(result[0]) {
                assertThat(id, equalTo(GroupId(4)))
                assertThat(creatorId, equalTo(AccountId(5)))
                assertThat(name, equalTo(GroupName("Group-Four")))
            }

            with(result[1]) {
                assertThat(id, equalTo(GroupId(3)))
                assertThat(creatorId, equalTo(AccountId(4)))
                assertThat(name, equalTo(GroupName("Group-Three")))
            }
        }


        @TestFactory
        fun `Limit`() = runWith(GroupRepository::class) {
            setup()

            val query = GroupQuery(
                groupIds = listOf(),
                limit = Limit(3)
            )

            assertThat(count(query), equalTo(4UL))
            val result = list(query)
            assertThat(result, hasSize(3))
        }

        @TestFactory
        fun `Skip and limit`() = runWith(GroupRepository::class) {
            setup()

            val query = GroupQuery(
                afterId = GroupId(2),
                groupIds = listOf(),
                limit = Limit(1)
            )

            assertThat(count(query), equalTo(1UL))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(GroupId(1)))
            }
        }

        private fun GroupRepository.setup() {
            createGroup(
                groupId = GroupId(1),
                creatorId = AccountId(3),
                name = GroupName("Group-One")
            )

            createGroup(
                groupId = GroupId(2),
                creatorId = AccountId(3),
                name = GroupName("Group-Two")
            )

            createGroup(
                groupId = GroupId(3),
                creatorId = AccountId(4),
                name = GroupName("Group-Three")
            )

            createGroup(
                groupId = GroupId(4),
                creatorId = AccountId(5),
                name = GroupName("Group-Four")
            )
        }
    }
}

private fun GroupRepository.createGroup(
    name: GroupName,
    groupId: GroupId,
    creatorId: AccountId,
    cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
) {
    create(
        CreateCmd(
            id = cmdId,
            groupId = groupId,
            name = name,
            creatorId = creatorId
        )
    )
}

private fun GroupRepository.verifyCount(expected: Int) {
    verifyCount(expected) { }
}

private fun GroupRepository.verifyCount(expected: Int, block: GroupQuery.() -> Unit) {
    val counted = count(GroupQuery(groupIds = listOf()).also(block))
    assertThat("number of groups expected", counted, equalTo(expected.toULong()))
}
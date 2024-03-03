package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.TableLength
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTable
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TableSubTableGetTest : StateBaseTest() {

    @TestFactory
    fun `Get table from table`() = runTest { testInstance ->

        testInstance.tableCreate(
            KuaString("nested") to testInstance.tableCreate(
                KuaString("hamal") to KuaString("rocks")
            )
        )

        assertThat(testInstance.topGet(), equalTo(StackTop(2)))

        testInstance.tableSubTableGet(-1, KuaString("nested"))
        assertThat(testInstance.topGet(), equalTo(StackTop(3)))
        assertThat(testInstance.type(-1), equalTo(KuaTable::class))
        assertThat(testInstance.tableLength(-1), equalTo(TableLength(1)))

        testInstance.tableFieldGet(-1, KuaString("hamal"))
        assertThat(testInstance.topGet(), equalTo(StackTop(4)))
        assertThat(testInstance.stringGet(-1), equalTo(KuaString("rocks")))
    }

}
package io.hamal.lib.kua.state

import io.hamal.lib.kua.*
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.value.ValueFalse
import io.hamal.lib.value.ValueTrue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TableNextTest : StateBaseTest() {

    @TestFactory
    fun `Empty table`() = runTest { testInstance ->
        testInstance.tableCreate(0, 0)
        testInstance.nilPush()

        val result = testInstance.tableNext(-2)
        assertThat(result, equalTo(ValueFalse))
    }

    @TestFactory
    fun `Table with element`() = runTest { testInstance ->
        testInstance.tableCreate(0, 1)
        testInstance.stringPush(KuaString("key"))
        testInstance.stringPush(KuaString("value"))
        testInstance.tableRawSet(1)

        testInstance.nilPush()
        testInstance.tableNext(1).also { result -> assertThat(result, equalTo(ValueTrue)) }
        assertThat(testInstance.stringGet(-2), equalTo(KuaString("key")))
        assertThat(testInstance.stringGet(-1), equalTo(KuaString("value")))

        testInstance.topPop(1)
        testInstance.tableNext(1).also { result -> assertThat(result, equalTo(ValueFalse)) }
    }

}
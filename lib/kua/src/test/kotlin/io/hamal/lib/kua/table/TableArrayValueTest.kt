package io.hamal.lib.kua.table

import io.hamal.lib.kua.ClosableState
import io.hamal.lib.kua.ResourceLoader
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.value.FalseValue
import io.hamal.lib.kua.value.StringValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

internal class TableArrayValueTest {

    @TestFactory
    fun append(): List<DynamicTest> {
        val testInstance = TableProxy(TableProxyContext(1, state))
        return listOf(
            { testInstance.append(true) },
            { testInstance.append(FalseValue) },
            { testInstance.append(23.23) },
            { testInstance.append(23) },
            { testInstance.append(23L) },
            { testInstance.append(23.0f) },
            { testInstance.append("Hamal") },
            { testInstance.append(StringValue("Hamal")) }
        ).mapIndexed { idx, testFn ->
            dynamicTest("Test: ${(idx + 1)}") {
                bridge.tableCreate(0, 0)

                assertThat(testInstance.length(), equalTo(TableLength(0)))
                val result = testFn()
                assertThat(result, equalTo(TableLength(1)))

                bridge.pop(1)
                verifyStackIsEmpty()
            }
        }
    }

    private val state = run {
        ResourceLoader.load()
        ClosableState(Sandbox().bridge)
    }
    private val bridge = state.bridge
    private fun verifyStackIsEmpty() {
        assertThat("Stack is empty", state.isEmpty(), equalTo(true))
    }
}
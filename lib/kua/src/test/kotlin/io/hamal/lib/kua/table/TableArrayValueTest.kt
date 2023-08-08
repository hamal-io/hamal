package io.hamal.lib.kua.table

import io.hamal.lib.kua.ClosableState
import io.hamal.lib.kua.DefaultSandboxContext
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
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
        lateinit var testInstance: TableArrayValue
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
                testInstance = state.tableCreateArray()

                val result = testFn()
                assertThat(result, equalTo(TableLength(1)))
            }
        }
    }

    private val state = run {
        NativeLoader.load(Resources)
        ClosableState(Sandbox(DefaultSandboxContext()).native)
    }
}
package io.hamal.lib.kua.type

import io.hamal.lib.kua.*
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

internal class KuaTableTest {

    @TestFactory
    fun append(): List<DynamicTest> {
        lateinit var testInstance: KuaTable
        return listOf(
            KuaTrue to {}
//            Tuple3("Boolean(true)", { testInstance.append(KuaT) }, {}),
//            "KuaFalse" to { testInstance.append(KuaFalse) },
//            "Float(23.23f)" to { testInstance.append(23.23f) },
//            "Int(23)" to { testInstance.append(23) },
//            "Long(23)" to { testInstance.append(23L) },
//            "Double(23.32)" to { testInstance.append(23.32) },
//            "String('Hamal')" to { testInstance.append("Hamal") },
//            "KuaString('Hamal')" to { testInstance.append(KuaString("Hamal")) }
        ).map { (value, expect) ->
            dynamicTest(value.toString()) {
                testInstance = state.tableCreate()
                testInstance.append(value)
                    .also { result -> assertThat(result, equalTo(TableLength(1))) }
                assertThat(state.topGet(), equalTo(StackTop(1)))
                expect()
                state.topPop(1)
            }
        }
    }

    private val state = run {
        NativeLoader.load(Resources)
        Sandbox(SandboxContextNop)
    }

}
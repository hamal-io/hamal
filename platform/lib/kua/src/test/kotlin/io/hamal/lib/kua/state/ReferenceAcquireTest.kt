package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.type.KuaNumber
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.kua.type.getNumber
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class ReferenceAcquireTest : StateBaseTest() {

    @TestFactory
    fun `Acquires reference of table`() = runTest { testInstance ->
        testInstance.tableCreate(KuaString("answer") to KuaNumber(42.0))

        val reference = testInstance.referenceAcquire()
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        testInstance.referencePush(reference).also { type -> assertThat(type, equalTo(KuaTable::class)) }

        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.type(1), equalTo(KuaTable::class))

        KuaTable(1, testInstance).getNumber("answer").also { result ->
            assertThat(result, equalTo(KuaNumber(42.0)))
        }
    }

}
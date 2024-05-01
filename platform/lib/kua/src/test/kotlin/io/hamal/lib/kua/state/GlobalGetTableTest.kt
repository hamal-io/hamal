package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.topPop
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.value.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.isA
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal class GlobalGetTableTest : StateBaseTest() {

    @TestFactory
    fun `Tries to load global boolean as table`() = runTest { testInstance ->
        testInstance.globalSet(ValueString("answer"), ValueFalse)
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        assertThrows<IllegalStateException> { testInstance.globalGetTable(ValueString("answer")) }
            .also { exception -> assertThat(exception.message, equalTo("Expected type to be table but was boolean")) }

        assertThat(testInstance.topGet(), equalTo(StackTop(0)))
    }

    @TestFactory
    fun `Tries to load global decimal as table`() = runTest { testInstance ->
        testInstance.globalSet(ValueString("answer"), ValueDecimal(12.23))
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        assertThrows<IllegalStateException> { testInstance.globalGetTable(ValueString("answer")) }
            .also { exception -> assertThat(exception.message, equalTo("Expected type to be table but was decimal")) }

        assertThat(testInstance.topGet(), equalTo(StackTop(0)))
    }

    @TestFactory
    fun `Tries to load global error as table`() = runTest { testInstance ->
        testInstance.globalSet(ValueString("answer"), ValueError("Some Error Message"))
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        assertThrows<IllegalStateException> { testInstance.globalGetTable(ValueString("answer")) }
            .also { exception -> assertThat(exception.message, equalTo("Expected type to be table but was error")) }

        assertThat(testInstance.topGet(), equalTo(StackTop(0)))
    }

    @TestFactory
    fun `Tries to load global nil as table`() = runTest { testInstance ->
        testInstance.globalSet(ValueString("answer"), ValueNil)
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        assertThrows<IllegalStateException> { testInstance.globalGetTable(ValueString("answer")) }
            .also { exception -> assertThat(exception.message, equalTo("Expected type to be table but was nil")) }

        assertThat(testInstance.topGet(), equalTo(StackTop(0)))
    }

    @TestFactory
    fun `Tries to load global number as table`() = runTest { testInstance ->
        testInstance.globalSet(ValueString("answer"), ValueNumber(42.0))
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        assertThrows<IllegalStateException> { testInstance.globalGetTable(ValueString("answer")) }
            .also { exception -> assertThat(exception.message, equalTo("Expected type to be table but was number")) }

        assertThat(testInstance.topGet(), equalTo(StackTop(0)))
    }

    @TestFactory
    fun `Tries to load global string as table`() = runTest { testInstance ->
        testInstance.globalSet(ValueString("answer"), ValueString("hamal rocks"))
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        assertThrows<IllegalStateException> { testInstance.globalGetTable(ValueString("answer")) }
            .also { exception -> assertThat(exception.message, equalTo("Expected type to be table but was string")) }

        assertThat(testInstance.topGet(), equalTo(StackTop(0)))
    }

    @TestFactory
    fun `Gets global table onto stack`() = runTest { testInstance ->
        val table = testInstance.tableCreate(0, 0)
        testInstance.globalSet(ValueString("answer"), table)
        testInstance.topPop(1)
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        testInstance.globalGetTable(ValueString("answer")).also { result ->
            assertThat(result, isA(KuaTable::class.java))
        }
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
    }
}
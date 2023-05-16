package io.hamal.lib.script.api.value

import io.hamal.lib.script.api.value.ValueOperation.Type.Add
import io.hamal.lib.script.api.value.ValueOperation.Type.Sub
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ValueTest {

    @Nested
    @DisplayName("type()")
    inner class TypeTest() {
        @Test
        fun `Returns the type specified in the meta table`() {
            val result = testInstance.type()
            assertThat(result, equalTo("test-type"))
        }
    }

    @Nested
    @DisplayName("findInfixOperation()")
    inner class FindInfixOperationTest {
        @Test
        fun `Returns the operation`() {
            val result = testInstance.findInfixOperation(Add, "test-type")
            assertThat(result, equalTo(TestInfixOperation))
        }

        @Test
        fun `Returns null if operation type is different`() {
            val result = testInstance.findInfixOperation(Sub, "test-type")
            assertThat(result, nullValue())
        }

        @Test
        fun `Returns null if other type is different`() {
            val result = testInstance.findInfixOperation(Add, "different-type")
            assertThat(result, nullValue())
        }
    }

    @Nested
    @DisplayName("findPrefixOperation()")
    inner class FindPrefixOperationTest {
        @Test
        fun `Returns the operation`() {
            val result = testInstance.findPrefixOperation(Add)
            assertThat(result, equalTo(TestPrefixOperation))
        }

        @Test
        fun `Returns null if operation type is different`() {
            val result = testInstance.findPrefixOperation(Sub)
            assertThat(result, nullValue())
        }

    }

    private val testInstance = TestValue()
}

class TestValue : Value {
    override val metaTable = TestMetaTable
}


object TestMetaTable : MetaTable {

    override val type = "test-type"
    override val operations = listOf(TestPrefixOperation, TestInfixOperation)
}

object TestPrefixOperation : PrefixValueOperation {
    override val selfType = "test-type"
    override val operationType = Add
    override fun invoke(self: Value) = TODO("Not yet implemented")
}

object TestInfixOperation : InfixValueOperation {
    override val selfType = "test-type"
    override val otherType = "test-type"
    override val operationType = Add

    override fun invoke(self: Value, other: Value) = TODO("Not yet implemented")
}
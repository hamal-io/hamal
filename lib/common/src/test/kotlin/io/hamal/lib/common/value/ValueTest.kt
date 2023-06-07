package io.hamal.lib.common.value

import io.hamal.lib.common.value.ValueOperation.Type.Add
import io.hamal.lib.common.value.ValueOperation.Type.Sub
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
class TypeTest {
    @Test
    fun `Returns the type specified in the meta table`() {
        val result = testInstance.type()
        assertThat(result, equalTo("test-type"))
    }
}

@Nested
class FindInfixOperationTest {
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
class FindPrefixOperationTest {
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


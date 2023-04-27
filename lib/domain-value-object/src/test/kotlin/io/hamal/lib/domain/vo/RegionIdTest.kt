package io.hamal.lib.domain.vo

import io.hamal.lib.domain.vo.helper.SerializationTestHelper.generateTestCases
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

@Nested
class RegionIdTest {
    @Nested
    @DisplayName("RegionId()")
    inner class ConstructorTest {
        @Test
        fun `Tries to create a region id less than 0`() {
            val exception = assertThrows<IllegalArgumentException> {
                RegionId(-1)
            }
            assertThat(exception.message, containsString("RegionId must be in interval [0,1023]"))
        }

        @Test
        fun `Tries to create a region greater than 1023`() {
            val exception = assertThrows<IllegalArgumentException> {
                RegionId(1024)
            }
            assertThat(exception.message, containsString("RegionId must be in interval [0,1023]"))
        }

        @Test
        fun `Creates valid region id`() {
            RegionId(0)
            RegionId(23)
            RegionId(1023)
        }
    }

    @Nested
    @DisplayName("Serializer")
    inner class SerializerTest {
        @TestFactory
        fun tests() = generateTestCases(RegionId(234), "234")
    }
}

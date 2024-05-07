package io.hamal.lib.domain._enum

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class CodeValueTypeTest {
    @Test
    fun `Value does not exist`() {
        val exception = assertThrows<IllegalArgumentException> { CodeTypes.of(777) }
        assertThat(exception.message, equalTo("777 not mapped as a code type"))
    }
}
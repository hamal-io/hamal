package io.hamal.lib.kua

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class IntegerWidthTest {
    @Test
    fun `Loads integer width from lua`() {
        val result = testInstance.integerWidth()
        assertThat("64bit platform - 8 byte", result, equalTo(8))
    }
}

private val testInstance by lazy {
    ResourceLoader.load()
    State()
}
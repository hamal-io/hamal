package io.hamal.lib.util.copy

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
class CopyTest {
    @Nested
    @DisplayName("immutable classes")
    inner class ImmutableClassesTest {
        @Test
        fun `There are 0 immutable classes declared`() {
            assertThat(Copy.immutableClasses, hasSize(0))
        }
    }

    @Nested
    @DisplayName("Copy()")
    inner class CopyTest {

    }
}
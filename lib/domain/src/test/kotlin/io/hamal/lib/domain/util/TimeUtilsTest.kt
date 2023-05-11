package io.hamal.lib.domain.util

import io.hamal.lib.domain.util.TimeUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant

@Nested
@DisplayName("TimeUtils")
class TimeUtilsTest {

    @Nested
    @DisplayName("now()")
    inner class NowTest {
        @Test
        fun `Returns java_time_Instant_now() when not injected`() {
            assertThat(TimeUtils.now().epochSecond, equalTo(Instant.now().epochSecond))
        }

        @Test
        fun `Returns injected value when set`() {
            TimeUtils.injectedNow.set(Instant.ofEpochMilli(2810))
            assertThat(TimeUtils.now(), equalTo(Instant.ofEpochMilli(2810)))
        }
    }

    @Nested
    @DisplayName("withInstant()")
    inner class WithInstantTest {
        @Test
        fun `Injects instant and can be called multiple times with now()`() {
            TimeUtils.withInstant(Instant.ofEpochMilli(1212)) {
                assertThat(TimeUtils.now(), equalTo(Instant.ofEpochMilli(1212)))
                assertThat(TimeUtils.now(), equalTo(Instant.ofEpochMilli(1212)))
                assertThat(TimeUtils.now(), equalTo(Instant.ofEpochMilli(1212)))
                assertThat(TimeUtils.now(), equalTo(Instant.ofEpochMilli(1212)))
            }
        }

        @Test
        fun `Injected instant gone after out of scope`() {
            TimeUtils.withInstant(Instant.ofEpochMilli(1212)) {
                assertThat(TimeUtils.injectedNow.get(), equalTo(Instant.ofEpochMilli(1212)))
            }
            assertThat(TimeUtils.injectedNow.get(), nullValue())
        }

        @Test
        fun `Exception unsets instant`() {
            assertThrows<IllegalStateException> {
                TimeUtils.withInstant(Instant.ofEpochMilli(1212)) {
                    assertThat(TimeUtils.injectedNow.get(), equalTo(Instant.ofEpochMilli(1212)))
                    throw IllegalStateException()
                }
            }
            assertThat(TimeUtils.injectedNow.get(), nullValue())
        }
    }
}
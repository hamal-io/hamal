package io.hamal.lib.common.supplier

import io.hamal.lib.common.supplier.InstantSupplier
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.number.OrderingComparison
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Instant

@Nested
class InstantSupplierTest {

    @Nested
    
    inner class DefaultImplTest {
        @Test
        fun `Returns current instant`() {
            val testInstance = InstantSupplier.default()
            val now = Instant.now()
            assertThat(testInstance(), OrderingComparison.greaterThan(now))
        }
    }

    @Nested
    
    inner class FakeImplTest {
        @Test
        fun `Returns hardcoded instant`() {
            val testInstance = InstantSupplier.fake(Instant.ofEpochMilli(1000))
            val result = testInstance()
            assertThat(result, equalTo(Instant.ofEpochMilli(1000)))
        }
    }

}
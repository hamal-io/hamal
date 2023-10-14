package io.hamal.core.component

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class RetryTest {

    @Test
    fun `Nothing to retry`() {
        testInstance { }
        assertThat("No retry attempted", delayFake.counter, equalTo(0))
    }

    @Test
    fun `Succeeds after second retry`() {
        var counter = 0
        val result = testInstance {
            if (counter < 2) {
                counter++
                throw NoSuchElementException("TestElement not found")
            }
            "success"
        }

        assertThat(result, equalTo("success"))
        assertThat(delayFake.counter, equalTo(2))
    }

    @Test
    fun `Tries to use retry with maxAttempts being negative`() {
        val exception = assertThrows<IllegalArgumentException> {
            testInstance(-1) { 74656 }
        }
        assertThat(exception.message, equalTo("maxAttempts must not be negative"))
        assertThat(delayFake.counter, equalTo(0))
    }

    @Test
    fun `Tries to retry but exhaust maxAttempts`() {
        var counter = 0
        val exception = assertThrows<NoSuchElementException> {
            testInstance(2) {
                if (counter < 2) {
                    counter++
                    throw NoSuchElementException("TestElement not found")
                }
            }
        }
        assertThat(exception.message, equalTo("TestElement not found"))
        assertThat(delayFake.counter, equalTo(2))
    }

    @Test
    fun `Throws not NoSuchElementException`() {
        val exception = assertThrows<IllegalArgumentException> {
            testInstance {
                throw IllegalArgumentException("SomeExceptionMessage")
            }
        }

        assertThat(exception.message, equalTo("SomeExceptionMessage"))
        assertThat("No retry attempted", delayFake.counter, equalTo(0))
    }


    private val delayFake = DelayRetryFake()
    private val testInstance = Retry(delayFake)
}

private class DelayRetryFake : DelayRetry {
    override fun invoke(iteration: Int) {
        counter++
    }

    var counter = 0
}
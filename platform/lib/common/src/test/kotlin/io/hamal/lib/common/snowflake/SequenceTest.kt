package io.hamal.lib.common.snowflake

import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


internal class SequenceTest {
    @Test
    fun `Limited to 12 bits`() {
        Sequence(0)
        Sequence(4095)

        val exception = assertThrows<IllegalArgumentException> {
            Sequence(4096)
        }
        assertThat(exception.message, containsString("Sequence is limited to 12 bits - [0, 4095]"))
    }

    @Test
    fun `Sequence is not negative`() {
        val exception = assertThrows<IllegalArgumentException> {
            Sequence(-1)
        }
        assertThat(exception.message, containsString("Sequence must not be negative - [0, 4095]"))
    }
}


internal class SequenceSourceImplTest {

    @Test
    fun `Requires ElapsedSource to return monotonic time`() {
        val testInstance = SequenceSourceImpl()

        testInstance.next { Elapsed(0L) }
        testInstance.next { Elapsed(1L) }
        testInstance.next { Elapsed(2L) }
        testInstance.next { Elapsed(2L) }
        testInstance.next { Elapsed(3L) }

        val exception = assertThrows<IllegalStateException> {
            testInstance.next { Elapsed(1) }
        }
        assertThat(exception.message, containsString("Elapsed must be monotonic"))
    }

    @Test
    fun `Blocks if sequence is exhausted`() {
        val testInstance = SequenceSourceImpl()
        val elapsedSource = object : ElapsedSource {
            var counter = 0
            var blockingCounter = 0
            
            override fun elapsed(): Elapsed {
                counter++
                if (counter > 4096) {
                    blockingCounter++
                }
                if (blockingCounter > 1024) {
                    return Elapsed(2)
                }
                return Elapsed(1)
            }
        }

        for (i in 0 until 4096) {
            val (elapsed, seq) = testInstance.next(elapsedSource::elapsed)
            assertThat(elapsed, equalTo(Elapsed(1)))
            assertThat(seq, equalTo(Sequence(i)))
        }

        val (resultElapsed, resultSeq) = testInstance.next(elapsedSource::elapsed)
        assertThat(resultElapsed, equalTo(Elapsed(2)))
        assertThat(resultSeq, equalTo(Sequence(0)))
    }
}

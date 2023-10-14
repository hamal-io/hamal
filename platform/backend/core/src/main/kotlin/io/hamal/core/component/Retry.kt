package io.hamal.core.component


import org.springframework.stereotype.Component
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.min
import kotlin.math.pow
import kotlin.time.Duration

fun interface DelayRetry {
    operator fun invoke(iteration: Int)
}

class DelayRetryExponentialTime(
    private val base: Duration,
    private val maxBackOffTime: Duration
) : DelayRetry {
    private val rand = ThreadLocalRandom.current()

    override operator fun invoke(iteration: Int) {
        val pow: Double = base.inWholeMilliseconds.toDouble().pow(iteration.toDouble())
        val extraDelay = rand.nextInt(1000)
        Thread.sleep(min(pow * 1000 + extraDelay, maxBackOffTime.inWholeMilliseconds.toDouble()).toLong())
    }
}

class DelayRetryFixedTime(
    private val delay: Duration
) : DelayRetry {
    override operator fun invoke(iteration: Int) {
        Thread.sleep(delay.inWholeMilliseconds)
    }
}

object DelayRetryNoDelay : DelayRetry {
    override fun invoke(iteration: Int) {}
}


@Component
class Retry(private val delay: DelayRetry) {
    operator fun <T> invoke(maxAttempts: Int = 5, action: () -> T): T {
        require(maxAttempts >= 0) { "maxAttempts must not be negative" }
        val exceptionRef = AtomicReference<NoSuchElementException>(null)

        for (iteration in 0 until maxAttempts) {
            try {
                return action()
            } catch (e: java.util.NoSuchElementException) {
                exceptionRef.set(e)
                delay(iteration)
            } catch (t: Throwable) {
                throw t
            }
        }

        throw exceptionRef.get()
    }
}



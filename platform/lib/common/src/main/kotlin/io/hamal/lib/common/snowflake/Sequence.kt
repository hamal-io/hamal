package io.hamal.lib.common.snowflake

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

interface SequenceSource {
    /**
     * Blocks if sequence is exhausted until next millisecond
     */
    fun next(elapsedSource: () -> Elapsed): Pair<Elapsed, Sequence>

}

@JvmInline
value class Sequence(val value: Int) {

    init {
        require(value >= 0) { "Sequence must not be negative - [0, 65535]" }
        require(value <= 65535) { "Sequence is limited to 16 bits - [0, 65535]" }
    }
}

class SequenceSourceImpl : SequenceSource {

    override fun next(elapsedSource: () -> Elapsed): Pair<Elapsed, Sequence> {

        return lock.withLock {
            val elapsed = elapsedSource()

            when {
                elapsed > previousCalledAt -> {
                    previousCalledAt = elapsed
                    nextSequence = 1
                    Pair(previousCalledAt, Sequence(0))
                }

                elapsed == previousCalledAt -> {

                    if (nextSequence >= maxSequence) {
                        while (true) {
                            val currentElapsed = elapsedSource()
                            if (elapsed != currentElapsed) {
                                nextSequence = 0
                                previousCalledAt = currentElapsed
                                break
                            }
                        }
                    }

                    return Pair(previousCalledAt, Sequence(nextSequence)).also {
                        nextSequence += 1
                    }
                }

                else -> throw IllegalStateException("Elapsed must be monotonic")
            }
        }
    }

    private var previousCalledAt: Elapsed = Elapsed(0)
    private var nextSequence = 0
    private val maxSequence = 65535
    private val lock = ReentrantLock()
}

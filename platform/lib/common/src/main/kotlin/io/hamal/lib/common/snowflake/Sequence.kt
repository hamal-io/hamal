package io.hamal.lib.common.snowflake

interface SequenceSource {
    /**
     * Blocks if sequence is exhausted until next millisecond
     */
    fun next(elapsedSource: () -> Elapsed): Pair<Elapsed, Sequence>

}

@JvmInline
value class Sequence(val value: Short) {
    constructor(value: Int) : this(value.toShort())

    init {
        require(value >= 0) { "Sequence must not be negative - [0, 4095]" }
        require(value <= 4095) { "Sequence is limited to 12 bits - [0, 4095]" }
    }
}

class SequenceSourceImpl : SequenceSource {

    private var previousCalledAt: Elapsed = Elapsed(0)
    private var nextSequence = 0
    private val maxSequence = 4096

    override fun next(elapsedSource: () -> Elapsed): Pair<Elapsed, Sequence> {
        val elapsed = elapsedSource()
        check(elapsed >= previousCalledAt) { "Elapsed must be monotonic" }

        if (elapsed == previousCalledAt) {
            if (nextSequence >= maxSequence) {
                while (true) {
                    val currentElapsed = elapsedSource()
                    if (elapsed != currentElapsed) {
                        nextSequence = 0
                        previousCalledAt = currentElapsed
                        break
                    }
                }
            } else {
                nextSequence
                previousCalledAt = elapsed
            }

        } else {
            previousCalledAt = elapsed
            nextSequence = 0
        }

        return Pair(previousCalledAt, Sequence(nextSequence++))
    }
}

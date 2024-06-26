package io.hamal.lib.common.supplier

import io.hamal.lib.common.util.TimeUtils
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.toJavaDuration

interface InstantSupplier {

    operator fun invoke(): Instant

    object DefaultImpl : InstantSupplier {
        override operator fun invoke(): Instant = TimeUtils.now()
    }

    class FakeImpl(private var fakeValue: Instant) : InstantSupplier {
        override fun invoke(): Instant = fakeValue
        fun update(fakeValue: Instant) {
            this.fakeValue = fakeValue
        }

        fun plus(duration: Duration) = update(fakeValue.plus(duration.toJavaDuration()))
    }

    companion object {
        fun default() = DefaultImpl
        fun fake(fakeValue: Instant) = FakeImpl(fakeValue)
    }
}
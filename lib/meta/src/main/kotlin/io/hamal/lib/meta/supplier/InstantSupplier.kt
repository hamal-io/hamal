package io.hamal.lib.meta.supplier

import java.time.Instant

interface InstantSupplier {

    operator fun invoke(): Instant

    object DefaultImpl : InstantSupplier {
        override operator fun invoke(): Instant = Instant.now()
    }

    class FakeImpl(private val fakeValue: Instant) : InstantSupplier {
        override fun invoke(): Instant = fakeValue
    }

    companion object {
        fun default() = DefaultImpl
        fun fake(fakeValue: Instant) = FakeImpl(fakeValue)
    }
}
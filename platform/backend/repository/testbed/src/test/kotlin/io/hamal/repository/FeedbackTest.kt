package io.hamal.repository

import io.hamal.repository.api.FeedbackRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory

internal class FeedbackTest : AbstractUnitTest() {

    @Nested
    inner class CreateTest {
        @TestFactory
        fun `Creates Feedback`() = runWith(FeedbackRepository::class) {

        }
    }

    @Nested
    inner class GetTest {

    }

    @Nested
    inner class FindTest {

    }

    @Nested
    inner class ClearTest {

    }

    @Nested
    inner class ListTest {

    }
}
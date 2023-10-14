package io.hamal.repository

import io.hamal.lib.domain.vo.CodeType
import io.hamal.repository.api.CodeRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows


internal class CodeTypeTest : AbstractUnitTest() {

    @TestFactory
    fun `Code type id does not exist`() = runWith(CodeRepository::class) {
        MatcherAssert.assertThat(
            assertThrows<IllegalArgumentException> { CodeType.of(777) }.message,
            Matchers.equalTo("777 not mapped as a code type")
        )
    }
}

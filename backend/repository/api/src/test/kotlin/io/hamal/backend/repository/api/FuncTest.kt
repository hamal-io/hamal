package io.hamal.backend.repository.api

import io.hamal.backend.repository.api.FuncRequestRepository.Command.*
import io.hamal.backend.repository.api.FuncRequestRepository.Recorder
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.domain.vo.port.FixedTimeIdGeneratorAdapter
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RecorderTest {

    @Nested
    @DisplayName("createFunc()")
    inner class CreateFuncTest {

           @Test
        fun `Records creation of func with overwritten parameters`() {
            val testInstance = Recorder(FixedTimeIdGeneratorAdapter())

            val resultId = testInstance.createFunc {
                name = FuncName("some-func-ref")
                code = Code("local hamal_rocks = true")
            }
            assertThat(resultId, equalTo(FuncId(SnowflakeId(2199023255552))))

            val commands = testInstance.commands()
            assertThat(
                commands, equalTo(
                    listOf(
                        FuncToCreate(
                            funcId = FuncId(SnowflakeId(2199023255552)),
                            name = FuncName("some-func-ref"),
                            Code("local hamal_rocks = true")
                        )
                    )
                )
            )
        }
    }

}


package io.hamal.backend.repository.api

import io.hamal.backend.repository.api.JobDefinitionRepository.Command.*
import io.hamal.backend.repository.api.JobDefinitionRepository.Recorder
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.vo.JobDefinitionId
import io.hamal.lib.domain.vo.JobReference
import io.hamal.lib.domain.vo.port.FixedTimeIdGeneratorAdapter
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RecorderTest {

    @Nested
    @DisplayName("createJobDefinition()")
    inner class CreateJobDefinitionTest {

        @Test
        fun `Records creation of JobDefinition with default parameters`() {
            val testInstance = Recorder(FixedTimeIdGeneratorAdapter())

            val resultId = testInstance.createJobDefinition {}
            assertThat(resultId, equalTo(JobDefinitionId(SnowflakeId(2199023255552))))

            val commands = testInstance.commands()
            assertThat(
                commands, equalTo(
                    listOf(
                        JobDefinitionToCreate(
                            jobDefinitionId = JobDefinitionId(SnowflakeId(2199023255552)),
                            reference = JobReference("2199023255552-ref")
                        )
                    )
                )
            )
        }

        @Test
        fun `Records creation of JobDefinition with overwritten parameters`() {
            val testInstance = Recorder(FixedTimeIdGeneratorAdapter())

            val resultId = testInstance.createJobDefinition {
                reference = JobReference("some-job-ref")
            }
            assertThat(resultId, equalTo(JobDefinitionId(SnowflakeId(2199023255552))))

            val commands = testInstance.commands()
            assertThat(
                commands, equalTo(
                    listOf(
                        JobDefinitionToCreate(
                            jobDefinitionId = JobDefinitionId(SnowflakeId(2199023255552)),
                            reference = JobReference("some-job-ref")
                        )
                    )
                )
            )
        }
    }

}


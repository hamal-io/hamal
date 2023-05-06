package io.hamal.backend.repository.api

import io.hamal.backend.repository.api.JobDefinitionRepository.Command.*
import io.hamal.backend.repository.api.JobDefinitionRepository.Recorder
import io.hamal.lib.util.SnowflakeId
import io.hamal.lib.vo.JobDefinitionId
import io.hamal.lib.vo.JobReference
import io.hamal.lib.vo.port.FixedTimeIdGeneratorAdapter
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RecorderTest {

    @Nested
    @DisplayName("insertJobDefinition()")
    inner class InsertJobDefinitionTest {

        @Test
        fun `Records insertion of JobDefinition with default parameters`() {
            val testInstance = Recorder(FixedTimeIdGeneratorAdapter())

            val id = testInstance.createJobDefinition {}
            assertThat(id, equalTo(JobDefinitionId(SnowflakeId(2199023255552))))

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
        fun `Records insertion of JobDefinition with overwritten parameters`() {
            val testInstance = Recorder(FixedTimeIdGeneratorAdapter())

            val id = testInstance.createJobDefinition {
                reference = JobReference("some-job-ref")
            }
            assertThat(id, equalTo(JobDefinitionId(SnowflakeId(2199023255552))))

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


//        private val testInstance = Recorder()
    }

}


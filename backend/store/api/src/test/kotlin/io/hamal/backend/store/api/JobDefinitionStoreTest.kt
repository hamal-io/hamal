package io.hamal.backend.store.api

import io.hamal.backend.store.api.JobDefinitionStore.Command.*
import io.hamal.backend.store.api.JobDefinitionStore.Recorder
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

            val id = testInstance.insertJobDefinition {}
            assertThat(id, equalTo(JobDefinitionId(SnowflakeId(2199023255552))))

            val commands = testInstance.commands()
            assertThat(
                commands, equalTo(
                    listOf(
                        JobDefinitionToInsert(
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

            val id = testInstance.insertJobDefinition {
                reference = JobReference("some-job-ref")
            }
            assertThat(id, equalTo(JobDefinitionId(SnowflakeId(2199023255552))))

            val commands = testInstance.commands()
            assertThat(
                commands, equalTo(
                    listOf(
                        JobDefinitionToInsert(
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


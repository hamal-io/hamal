package metric

import io.hamal.backend.instance.event.InstanceEventEmitter
import io.hamal.backend.instance.event.events.ExecPlannedEvent
import io.hamal.backend.instance.event.events.ExecutionCompletedEvent
import io.hamal.backend.instance.event.events.InstanceEvent
import io.hamal.backend.instance.service.MetricService
import io.hamal.lib.common.Partition
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.DefaultDomainIdGenerator
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.repository.api.MetricRepository
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.memory.InMemoryMetrics
import io.hamal.repository.memory.log.MemoryBrokerRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationContext

class MetricServiceTest{

    private val metricRepository = InMemoryMetrics()
    private val service = MetricService(metricRepository)

    @Test
    fun serviceTest(){
        val g:GenerateDomainId = DefaultDomainIdGenerator(Partition(0))
        val b: BrokerRepository =  MemoryBrokerRepository()
        val e:InstanceEventEmitter = InstanceEventEmitter(g,b)
       // TODO val cmdId: CmdId = CmdId()
        //e.emit()
    }

}
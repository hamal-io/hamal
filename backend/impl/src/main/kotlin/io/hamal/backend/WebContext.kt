package io.hamal.backend

import io.hamal.backend.repository.api.domain.Tenant
import io.hamal.lib.common.Partition
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.vo.base.DomainId
import io.hamal.lib.domain.vo.port.GenerateDomainId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

data class WebContextData(
    val tenant: Tenant,
    val partition: Partition
)


@Component
class WebContext(
    @Autowired private val generateDomainId: GenerateDomainId
) {
    fun set(context: WebContextData) {
        holder.set(context)
    }

    fun tenantId() = get().tenant.id

    fun partition() = get().partition

    fun <ID : DomainId> generateDomainId(ctor: (SnowflakeId) -> ID) =
        generateDomainId(partition(), ctor)

    fun get(): WebContextData {
        return holder.get()
    }

    fun remove() {
        holder.remove()
    }

    companion object {
        private val holder: ThreadLocal<WebContextData> = ThreadLocal<WebContextData>()
    }
}
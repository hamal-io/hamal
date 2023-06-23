package io.hamal.backend

import io.hamal.backend.repository.api.domain.Tenant
import io.hamal.lib.common.Shard
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.vo.base.DomainId
import io.hamal.lib.domain.vo.port.GenerateDomainId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

data class WebContextData(
    val tenant: Tenant,
    val shard: Shard
)


@Component
class WebContext(
    @Autowired private val generateDomainId: GenerateDomainId
) {
    fun set(context: WebContextData) {
        holder.set(context)
    }

    fun tenantId() = get().tenant.id

    fun shard() = get().shard

    fun <ID : DomainId> generateDomainId(ctor: (SnowflakeId) -> ID) =
        generateDomainId(shard(), ctor)

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
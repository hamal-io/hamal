package io.hamal.core.adapter.func

import io.hamal.lib.domain.vo.FuncId
import io.hamal.repository.api.FuncDeployment
import io.hamal.repository.api.FuncQueryRepository
import org.springframework.stereotype.Component

fun interface FuncDeploymentListPort {
    fun funcDeploymentList(funcId: FuncId): List<FuncDeployment>
}

@Component
class FuncDeploymentListAdapter(
    private val funcGet: FuncGetPort,
    private val funcQueryRepository: FuncQueryRepository
) : FuncDeploymentListPort {
    override fun funcDeploymentList(funcId: FuncId): List<FuncDeployment> {
        funcGet(funcId)
        return funcQueryRepository.listDeployments(funcId)
    }

}
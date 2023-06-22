package io.hamal.backend.repository.memory.record

import io.hamal.backend.repository.api.FuncCmdRepository
import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.backend.repository.api.domain.Func
import io.hamal.lib.domain.vo.FuncId


object MemoryFuncRepository: FuncCmdRepository, FuncQueryRepository{
    override fun create(cmd: FuncCmdRepository.CreateCmd): Func {
        TODO("Not yet implemented")
    }

    override fun find(funcId: FuncId): Func? {
        TODO("Not yet implemented")
    }

    override fun list(afterId: FuncId, limit: Int): List<Func> {
        TODO("Not yet implemented")
    }
}
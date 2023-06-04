//package io.hamal.backend.repository.sqlite
//
//import io.hamal.backend.repository.api.ExecCmdRepository
//import io.hamal.backend.repository.api.domain.AdhocInvocation
//import io.hamal.lib.common.Shard
//import io.hamal.lib.domain.CommandId
//import io.hamal.lib.domain.Correlation
//import io.hamal.lib.domain.vo.*
//import io.hamal.lib.domain.vo.port.DefaultDomainIdGenerator
//import kotlin.io.path.Path
//
//fun main() {
//
//    val execRepository = SqliteExecRepository(
//        SqliteExecRepository.Config(
//            path = Path("/tmp/hamal"),
//            shard = Shard(1)
//        )
//    )
//
//
//    val planned = execRepository.plan(
//        CommandId(4), ExecCmdRepository.PlanCmd(
//            id = DefaultDomainIdGenerator(Shard(1), ::ExecId),
//            accountId = AccountId(2),
//            inputs = ExecInputs(listOf()),
//            secrets = ExecSecrets(listOf()),
//            code = Code("Code"),
//            invocation = AdhocInvocation(),
//            correlation = Correlation(
//                funcId = FuncId(234),
//                correlationId = CorrelationId("cor")
//            )
//
//        )
//    )
//
//    println(planned)
//    println(planned.id)
//
//    val scheduled = execRepository.schedule(CommandId(7), planned)
//    println(scheduled)
//}
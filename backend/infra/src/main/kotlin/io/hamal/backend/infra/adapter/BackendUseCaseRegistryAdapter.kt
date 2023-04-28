package io.hamal.backend.infra.adapter

import io.hamal.backend.application.trigger.InvokeManualTriggerUseCase
import io.hamal.lib.KeyedOnce
import io.hamal.lib.Tuple2
import io.hamal.lib.ddd.base.DomainObject
import io.hamal.lib.ddd.usecase.*
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import kotlin.reflect.KClass



//private fun ensureResultClass(useCaseOperation: UseCaseOperation<*, *>, resultClass: KClass<*>) {
//    val resultClassesMatch = useCaseOperation.resultClass == resultClass
//    val isAssignable = resultClass.java.isAssignableFrom(useCaseOperation.resultClass.java)
//    require(resultClassesMatch || isAssignable) {
//        IllegalArgumentException("result class(${resultClass.simpleName}) does not match with use case result class(${useCaseOperation.resultClass.simpleName})")
//    }
//}
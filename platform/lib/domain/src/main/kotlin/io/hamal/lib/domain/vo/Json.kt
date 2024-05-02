package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.serialization.HotModule
import io.hamal.lib.common.serialization.JsonAdapters
import io.hamal.lib.domain.State

object ValueVariableJsonModule : HotModule() {
    init {
        this[AccountId::class] = JsonAdapters.SnowflakeIdVariable(::AccountId)

        this[Email::class] = JsonAdapters.StringVariable(::Email)


        this[AuthId::class] = JsonAdapters.SnowflakeIdVariable(::AuthId)
        this[AuthToken::class] = JsonAdapters.StringVariable(::AuthToken)
        this[Password::class] = JsonAdapters.StringVariable(::Password)
        this[PasswordHash::class] = JsonAdapters.StringVariable(::PasswordHash)
        this[PasswordSalt::class] = JsonAdapters.StringVariable(::PasswordSalt)

        this[RecipeId::class] = JsonAdapters.SnowflakeIdVariable(::RecipeId)
        this[RecipeName::class] = JsonAdapters.StringVariable(::RecipeName)
        this[RecipeInputs::class] = JsonAdapters.ObjectVariable(::RecipeInputs)
        this[RecipeDescription::class] = JsonAdapters.StringVariable(::RecipeDescription)

        this[CmdId::class] = JsonAdapters.SnowflakeIdVariable(::CmdId)

        this[CodeId::class] = JsonAdapters.SnowflakeIdVariable(::CodeId)
        this[CodeVersion::class] = JsonAdapters.NumberVariable(::CodeVersion)

        this[CorrelationId::class] = JsonAdapters.StringVariable(::CorrelationId)

        this[CronPattern::class] = JsonAdapters.StringVariable(::CronPattern)

        this[ExecId::class] = JsonAdapters.SnowflakeIdVariable(::ExecId)
        this[ExecType::class] = JsonAdapters.StringVariable(::ExecType)
        this[ExecInputs::class] = JsonAdapters.ObjectVariable(::ExecInputs)
        this[ExecResult::class] = JsonAdapters.ObjectVariable(::ExecResult)
        this[ExecState::class] = JsonAdapters.ObjectVariable(::ExecState)

        this[ExecToken::class] = JsonAdapters.StringVariable(::ExecToken)
        this[ExecLogId::class] = JsonAdapters.SnowflakeIdVariable(::ExecLogId)
        this[ExecLogMessage::class] = JsonAdapters.StringVariable(::ExecLogMessage)
        this[ExecLogTimestamp::class] = JsonAdapters.InstantVariable(::ExecLogTimestamp)

        this[EndpointHeaders::class] = JsonAdapters.ObjectVariable(::EndpointHeaders)
        this[EndpointParameters::class] = JsonAdapters.ObjectVariable(::EndpointParameters)
        this[EndpointContent::class] = JsonAdapters.ObjectVariable(::EndpointContent)

        this[EventPayload::class] = JsonAdapters.ObjectVariable(::EventPayload)

        this[ExpiresAt::class] = JsonAdapters.InstantVariable(::ExpiresAt)

        this[ExtensionId::class] = JsonAdapters.SnowflakeIdVariable(::ExtensionId)
        this[ExtensionName::class] = JsonAdapters.StringVariable(::ExtensionName)

        this[FeedbackId::class] = JsonAdapters.SnowflakeIdVariable(::FeedbackId)
        this[FeedbackMessage::class] = JsonAdapters.StringVariable(::FeedbackMessage)

        this[NamespaceId::class] = JsonAdapters.SnowflakeIdVariable(::NamespaceId)
        this[NamespaceTreeId::class] = JsonAdapters.SnowflakeIdVariable(::NamespaceTreeId)
        this[NamespaceName::class] = JsonAdapters.StringVariable(::NamespaceName)
        this[NamespaceFeatures::class] = JsonAdapters.ObjectVariable(::NamespaceFeatures)


        this[FuncId::class] = JsonAdapters.SnowflakeIdVariable(::FuncId)
        this[FuncName::class] = JsonAdapters.StringVariable(::FuncName)
        this[FuncInputs::class] = JsonAdapters.ObjectVariable(::FuncInputs)

        this[DeployedAt::class] = JsonAdapters.InstantVariable(::DeployedAt)
        this[DeployMessage::class] = JsonAdapters.StringVariable(::DeployMessage)

        this[Limit::class] = JsonAdapters.NumberVariable(::Limit)
        this[Count::class] = JsonAdapters.NumberVariable(::Count)

        this[HookHeaders::class] = JsonAdapters.ObjectVariable(::HookHeaders)
        this[HookParameters::class] = JsonAdapters.ObjectVariable(::HookParameters)
        this[HookContent::class] = JsonAdapters.ObjectVariable(::HookContent)
        this[InvocationInputs::class] = JsonAdapters.ObjectVariable(::InvocationInputs)


        this[RequestId::class] = JsonAdapters.SnowflakeIdVariable(::RequestId)
        this[RequestClass::class] = JsonAdapters.StringVariable(::RequestClass)

        this[RunnerId::class] = JsonAdapters.SnowflakeIdVariable(::RunnerId)

        this[RunnerEnv::class] = JsonAdapters.ObjectVariable(::RunnerEnv)
        this[State::class] = JsonAdapters.ObjectVariable(::State)

        this[TopicId::class] = JsonAdapters.SnowflakeIdVariable(::TopicId)
        this[TopicName::class] = JsonAdapters.StringVariable(::TopicName)
        this[TopicEventId::class] = JsonAdapters.SnowflakeIdVariable(::TopicEventId)
        this[TopicEventPayload::class] = JsonAdapters.ObjectVariable(::TopicEventPayload)


        this[TriggerId::class] = JsonAdapters.SnowflakeIdVariable(::TriggerId)
        this[TriggerDuration::class] = JsonAdapters.StringVariable(::TriggerDuration)
        this[TriggerName::class] = JsonAdapters.StringVariable(::TriggerName)
        this[TriggerInputs::class] = JsonAdapters.ObjectVariable(::TriggerInputs)

        this[Web3Address::class] = JsonAdapters.StringVariable(::Web3Address)
        this[Web3Challenge::class] = JsonAdapters.StringVariable(::Web3Challenge)
        this[Web3Signature::class] = JsonAdapters.StringVariable(::Web3Signature)

        this[WorkspaceId::class] = JsonAdapters.SnowflakeIdVariable(::WorkspaceId)
        this[WorkspaceName::class] = JsonAdapters.StringVariable(::WorkspaceName)
    }
}
package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.serialization.HotModule
import io.hamal.lib.common.serialization.JsonAdapters
import io.hamal.lib.domain.State

object ValueVariableJsonModule : HotModule() {
    init {
        this[AccountId::class] = JsonAdapters.SnowflakeId(::AccountId)

        this[Email::class] = JsonAdapters.String(::Email)


        this[AuthId::class] = JsonAdapters.SnowflakeId(::AuthId)
        this[AuthToken::class] = JsonAdapters.String(::AuthToken)
        this[Password::class] = JsonAdapters.String(::Password)
        this[PasswordHash::class] = JsonAdapters.String(::PasswordHash)
        this[PasswordSalt::class] = JsonAdapters.String(::PasswordSalt)

        this[RecipeId::class] = JsonAdapters.SnowflakeId(::RecipeId)
        this[RecipeName::class] = JsonAdapters.String(::RecipeName)
        this[RecipeInputs::class] = JsonAdapters.Object(::RecipeInputs)
        this[RecipeDescription::class] = JsonAdapters.String(::RecipeDescription)

        this[CmdId::class] = JsonAdapters.SnowflakeId(::CmdId)

        this[CodeId::class] = JsonAdapters.SnowflakeId(::CodeId)
        this[CodeVersion::class] = JsonAdapters.Number(::CodeVersion)

        this[CorrelationId::class] = JsonAdapters.String(::CorrelationId)

        this[CronPattern::class] = JsonAdapters.String(::CronPattern)

        this[ExecId::class] = JsonAdapters.SnowflakeId(::ExecId)
        this[ExecType::class] = JsonAdapters.String(::ExecType)
        this[ExecInputs::class] = JsonAdapters.Object(::ExecInputs)
        this[ExecResult::class] = JsonAdapters.Object(::ExecResult)
        this[ExecState::class] = JsonAdapters.Object(::ExecState)

        this[ExecToken::class] = JsonAdapters.String(::ExecToken)
        this[ExecLogId::class] = JsonAdapters.SnowflakeId(::ExecLogId)
        this[ExecLogMessage::class] = JsonAdapters.String(::ExecLogMessage)
        this[ExecLogTimestamp::class] = JsonAdapters.Instant(::ExecLogTimestamp)

        this[EndpointHeaders::class] = JsonAdapters.Object(::EndpointHeaders)
        this[EndpointParameters::class] = JsonAdapters.Object(::EndpointParameters)
        this[EndpointContent::class] = JsonAdapters.Object(::EndpointContent)

        this[EventPayload::class] = JsonAdapters.Object(::EventPayload)

        this[ExpiresAt::class] = JsonAdapters.Instant(::ExpiresAt)

        this[ExtensionId::class] = JsonAdapters.SnowflakeId(::ExtensionId)
        this[ExtensionName::class] = JsonAdapters.String(::ExtensionName)

        this[FeedbackId::class] = JsonAdapters.SnowflakeId(::FeedbackId)
        this[FeedbackMessage::class] = JsonAdapters.String(::FeedbackMessage)

        this[NamespaceId::class] = JsonAdapters.SnowflakeId(::NamespaceId)
        this[NamespaceTreeId::class] = JsonAdapters.SnowflakeId(::NamespaceTreeId)
        this[NamespaceName::class] = JsonAdapters.String(::NamespaceName)
        this[NamespaceFeatures::class] = JsonAdapters.Object(::NamespaceFeatures)


        this[FuncId::class] = JsonAdapters.SnowflakeId(::FuncId)
        this[FuncName::class] = JsonAdapters.String(::FuncName)
        this[FuncInputs::class] = JsonAdapters.Object(::FuncInputs)

        this[DeployedAt::class] = JsonAdapters.Instant(::DeployedAt)
        this[DeployMessage::class] = JsonAdapters.String(::DeployMessage)

        this[Limit::class] = JsonAdapters.Number(::Limit)
        this[Count::class] = JsonAdapters.Number(::Count)

        this[HookHeaders::class] = JsonAdapters.Object(::HookHeaders)
        this[HookParameters::class] = JsonAdapters.Object(::HookParameters)
        this[HookContent::class] = JsonAdapters.Object(::HookContent)
        this[InvocationInputs::class] = JsonAdapters.Object(::InvocationInputs)


        this[RequestId::class] = JsonAdapters.SnowflakeId(::RequestId)
        this[RequestClass::class] = JsonAdapters.String(::RequestClass)

        this[RunnerId::class] = JsonAdapters.SnowflakeId(::RunnerId)

        this[RunnerEnv::class] = JsonAdapters.Object(::RunnerEnv)
        this[State::class] = JsonAdapters.Object(::State)

        this[TopicId::class] = JsonAdapters.SnowflakeId(::TopicId)
        this[TopicName::class] = JsonAdapters.String(::TopicName)
        this[TopicEventId::class] = JsonAdapters.SnowflakeId(::TopicEventId)
        this[TopicEventPayload::class] = JsonAdapters.Object(::TopicEventPayload)


        this[TriggerId::class] = JsonAdapters.SnowflakeId(::TriggerId)
        this[TriggerDuration::class] = JsonAdapters.String(::TriggerDuration)
        this[TriggerName::class] = JsonAdapters.String(::TriggerName)
        this[TriggerInputs::class] = JsonAdapters.Object(::TriggerInputs)

        this[Web3Address::class] = JsonAdapters.String(::Web3Address)
        this[Web3Challenge::class] = JsonAdapters.String(::Web3Challenge)
        this[Web3Signature::class] = JsonAdapters.String(::Web3Signature)

        this[WorkspaceId::class] = JsonAdapters.SnowflakeId(::WorkspaceId)
        this[WorkspaceName::class] = JsonAdapters.String(::WorkspaceName)
    }
}
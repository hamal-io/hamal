package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.serialization.*
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
        this[RecipeDescription::class] = JsonAdapters.String(::RecipeDescription)

        this[CmdId::class] = JsonAdapters.SnowflakeId(::CmdId)
        this[CodeId::class] = JsonAdapters.SnowflakeId(::CodeId)

        this[CorrelationId::class] = JsonAdapters.String(::CorrelationId)

        this[CronPattern::class] = JsonAdapters.String(::CronPattern)

        this[ExecId::class] = JsonAdapters.SnowflakeId(::ExecId)
        this[ExecType::class] = JsonAdapters.String(::ExecType)
        this[ExecToken::class] = JsonAdapters.String(::ExecToken)
        this[ExecLogId::class] = JsonAdapters.SnowflakeId(::ExecLogId)
        this[ExecLogMessage::class] = JsonAdapters.String(::ExecLogMessage)

        this[ExtensionId::class] = JsonAdapters.SnowflakeId(::ExtensionId)
        this[ExtensionName::class] = JsonAdapters.String(::ExtensionName)

        this[FeedbackId::class] = JsonAdapters.SnowflakeId(::FeedbackId)
        this[FeedbackMessage::class] = JsonAdapters.String(::FeedbackMessage)

        this[NamespaceId::class] = JsonAdapters.SnowflakeId(::NamespaceId)
        this[NamespaceTreeId::class] = JsonAdapters.SnowflakeId(::NamespaceTreeId)
        this[NamespaceName::class] = JsonAdapters.String(::NamespaceName)

        this[FuncId::class] = JsonAdapters.SnowflakeId(::FuncId)
        this[FuncName::class] = JsonAdapters.String(::FuncName)

        this[DeployMessage::class] = JsonAdapters.String(::DeployMessage)

        this[RequestId::class] = JsonAdapters.SnowflakeId(::RequestId)
        this[RequestClass::class] = JsonAdapters.String(::RequestClass)

        this[RunnerId::class] = JsonAdapters.SnowflakeId(::RunnerId)

        this[TopicId::class] = JsonAdapters.SnowflakeId(::TopicId)
        this[TopicName::class] = JsonAdapters.String(::TopicName)
        this[TopicEventId::class] = JsonAdapters.SnowflakeId(::TopicEventId)

        this[TriggerId::class] = JsonAdapters.SnowflakeId(::TriggerId)
        this[TriggerDuration::class] = JsonAdapters.String(::TriggerDuration)
        this[TriggerName::class] = JsonAdapters.String(::TriggerName)

        this[Web3Address::class] = JsonAdapters.String(::Web3Address)
        this[Web3Challenge::class] = JsonAdapters.String(::Web3Challenge)
        this[Web3Signature::class] = JsonAdapters.String(::Web3Signature)


        this[WorkspaceId::class] = JsonAdapters.SnowflakeId(::WorkspaceId)
        this[WorkspaceName::class] = JsonAdapters.String(::WorkspaceName)
    }
}

object ValueObjectJsonModule : HotModule() {
    init {
        this[Email::class] = JsonAdapters.String(::Email)
        this[Web3Address::class] = JsonAdapters.String(::Web3Address)
        this[Web3Challenge::class] = JsonAdapters.String(::Web3Challenge)
        this[Web3Signature::class] = JsonAdapters.String(::Web3Signature)


        this[AuthToken::class] = JsonAdapters.String(::AuthToken)
        this[Password::class] = JsonAdapters.String(::Password)
        this[PasswordHash::class] = JsonAdapters.String(::PasswordHash)
        this[PasswordSalt::class] = JsonAdapters.String(::PasswordSalt)


        this[RecipeName::class] = JsonAdapters.String(::RecipeName)
        this[RecipeInputs::class] = ValueObjectHotObjectAdapter(::RecipeInputs)
        this[RecipeDescription::class] = JsonAdapters.String(::RecipeDescription)


        this[CodeVersion::class] = ValueObjectIntAdapter(::CodeVersion)

        this[CorrelationId::class] = JsonAdapters.String(::CorrelationId)

        this[CronPattern::class] = JsonAdapters.String(::CronPattern)

        this[EndpointHeaders::class] = ValueObjectHotObjectAdapter(::EndpointHeaders)
        this[EndpointParameters::class] = ValueObjectHotObjectAdapter(::EndpointParameters)
        this[EndpointContent::class] = ValueObjectHotObjectAdapter(::EndpointContent)

        this[EventPayload::class] = ValueObjectHotObjectAdapter(::EventPayload)


        this[ExecType::class] = JsonAdapters.String(::ExecType)
        this[ExecInputs::class] = ValueObjectHotObjectAdapter(::ExecInputs)
        this[ExecResult::class] = ValueObjectHotObjectAdapter(::ExecResult)
        this[ExecState::class] = ValueObjectHotObjectAdapter(::ExecState)
        this[ExecToken::class] = JsonAdapters.String(::ExecToken)

        this[ExecLogTimestamp::class] = ValueObjectInstantAdapter(::ExecLogTimestamp)
        this[ExecLogMessage::class] = JsonAdapters.String(::ExecLogMessage)

        this[ExpiresAt::class] = ValueObjectInstantAdapter(::ExpiresAt)


        this[ExtensionName::class] = JsonAdapters.String(::ExtensionName)


        this[FeedbackMessage::class] = JsonAdapters.String(::FeedbackMessage)


        this[NamespaceName::class] = JsonAdapters.String(::NamespaceName)
        this[NamespaceFeatures::class] = ValueObjectHotObjectAdapter(::NamespaceFeatures)



        this[FuncInputs::class] = ValueObjectHotObjectAdapter(::FuncInputs)
        this[DeployMessage::class] = JsonAdapters.String(::DeployMessage)
        this[DeployedAt::class] = ValueObjectInstantAdapter(::DeployedAt)


        this[WorkspaceName::class] = JsonAdapters.String(::WorkspaceName)

        this[HookHeaders::class] = ValueObjectHotObjectAdapter(::HookHeaders)
        this[HookParameters::class] = ValueObjectHotObjectAdapter(::HookParameters)
        this[HookContent::class] = ValueObjectHotObjectAdapter(::HookContent)

        this[InvocationInputs::class] = ValueObjectHotObjectAdapter(::InvocationInputs)

        this[Limit::class] = ValueObjectIntAdapter(::Limit)
        this[Count::class] = ValueObjectLongAdapter(::Count)


        this[RequestClass::class] = JsonAdapters.String(::RequestClass)


        this[RunnerEnv::class] = ValueObjectHotObjectAdapter(::RunnerEnv)

        this[State::class] = ValueObjectHotObjectAdapter(::State)

        this[TopicName::class] = JsonAdapters.String(::TopicName)

        this[TopicEventPayload::class] = ValueObjectHotObjectAdapter(::TopicEventPayload)

        this[TriggerDuration::class] = JsonAdapters.String(::TriggerDuration)

        this[TriggerName::class] = JsonAdapters.String(::TriggerName)
        this[TriggerInputs::class] = ValueObjectHotObjectAdapter(::TriggerInputs)
    }
}

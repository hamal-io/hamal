package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.*
import io.hamal.lib.common.serialization.SerializationModule
import io.hamal.lib.common.value.ValueJsonAdapters.InstantVariable
import io.hamal.lib.common.value.ValueJsonAdapters.NumberVariable
import io.hamal.lib.common.value.ValueJsonAdapters.ObjectVariable
import io.hamal.lib.common.value.ValueJsonAdapters.SnowflakeIdVariable
import io.hamal.lib.common.value.ValueJsonAdapters.StringVariable
import io.hamal.lib.domain.State

object ValueVariableJsonModule : SerializationModule() {
    init {
        this[CreatedAt::class] = InstantVariable(::CreatedAt)
        this[UpdatedAt::class] = InstantVariable(::UpdatedAt)

        this[AccountId::class] = SnowflakeIdVariable(::AccountId)

        this[Email::class] = StringVariable(::Email)

        this[AuthId::class] = SnowflakeIdVariable(::AuthId)
        this[AuthToken::class] = StringVariable(::AuthToken)
        this[Password::class] = StringVariable(::Password)
        this[PasswordHash::class] = StringVariable(::PasswordHash)
        this[PasswordSalt::class] = StringVariable(::PasswordSalt)

        this[RecipeId::class] = SnowflakeIdVariable(::RecipeId)
        this[RecipeName::class] = StringVariable(::RecipeName)
        this[RecipeInputs::class] = ObjectVariable(::RecipeInputs)
        this[RecipeDescription::class] = StringVariable(::RecipeDescription)

        this[CmdId::class] = SnowflakeIdVariable(::CmdId)

        this[CodeId::class] = SnowflakeIdVariable(::CodeId)
        this[CodeVersion::class] = NumberVariable(::CodeVersion)

        this[CorrelationId::class] = StringVariable(::CorrelationId)

        this[CronPattern::class] = StringVariable(::CronPattern)

        this[ExecId::class] = SnowflakeIdVariable(::ExecId)
        this[ExecType::class] = StringVariable(::ExecType)
        this[ExecInputs::class] = ObjectVariable(::ExecInputs)
        this[ExecResult::class] = ObjectVariable(::ExecResult)
        this[ExecState::class] = ObjectVariable(::ExecState)

        this[ExecToken::class] = StringVariable(::ExecToken)
        this[ExecLogId::class] = SnowflakeIdVariable(::ExecLogId)
        this[ExecLogMessage::class] = StringVariable(::ExecLogMessage)
        this[ExecLogTimestamp::class] = InstantVariable(::ExecLogTimestamp)

        this[EndpointHeaders::class] = ObjectVariable(::EndpointHeaders)
        this[EndpointParameters::class] = ObjectVariable(::EndpointParameters)
        this[EndpointContent::class] = ObjectVariable(::EndpointContent)

        this[EventPayload::class] = ObjectVariable(::EventPayload)

        this[ExpiresAt::class] = InstantVariable(::ExpiresAt)

        this[ExtensionId::class] = SnowflakeIdVariable(::ExtensionId)
        this[ExtensionName::class] = StringVariable(::ExtensionName)

        this[FeedbackId::class] = SnowflakeIdVariable(::FeedbackId)
        this[FeedbackMessage::class] = StringVariable(::FeedbackMessage)

        this[NamespaceId::class] = SnowflakeIdVariable(::NamespaceId)
        this[NamespaceTreeId::class] = SnowflakeIdVariable(::NamespaceTreeId)
        this[NamespaceName::class] = StringVariable(::NamespaceName)
        this[NamespaceFeatures::class] = ObjectVariable(::NamespaceFeatures)


        this[FuncId::class] = SnowflakeIdVariable(::FuncId)
        this[FuncName::class] = StringVariable(::FuncName)
        this[FuncInputs::class] = ObjectVariable(::FuncInputs)

        this[DeployedAt::class] = InstantVariable(::DeployedAt)
        this[DeployMessage::class] = StringVariable(::DeployMessage)

        this[Limit::class] = NumberVariable(::Limit)
        this[Count::class] = NumberVariable(::Count)

        this[HookHeaders::class] = ObjectVariable(::HookHeaders)
        this[HookParameters::class] = ObjectVariable(::HookParameters)
        this[HookContent::class] = ObjectVariable(::HookContent)
        this[InvocationInputs::class] = ObjectVariable(::InvocationInputs)

        this[RequestId::class] = SnowflakeIdVariable(::RequestId)
        this[RequestClass::class] = StringVariable(::RequestClass)

        this[RunnerId::class] = SnowflakeIdVariable(::RunnerId)

        this[RunnerEnv::class] = ObjectVariable(::RunnerEnv)
        this[State::class] = ObjectVariable(::State)

        this[TopicId::class] = SnowflakeIdVariable(::TopicId)
        this[TopicName::class] = StringVariable(::TopicName)
        this[TopicEventId::class] = SnowflakeIdVariable(::TopicEventId)
        this[TopicEventPayload::class] = ObjectVariable(::TopicEventPayload)

        this[TriggerId::class] = SnowflakeIdVariable(::TriggerId)
        this[TriggerDuration::class] = StringVariable(::TriggerDuration)
        this[TriggerName::class] = StringVariable(::TriggerName)
        this[TriggerInputs::class] = ObjectVariable(::TriggerInputs)

        this[Web3Address::class] = StringVariable(::Web3Address)
        this[Web3Challenge::class] = StringVariable(::Web3Challenge)
        this[Web3Signature::class] = StringVariable(::Web3Signature)

        this[WorkspaceId::class] = SnowflakeIdVariable(::WorkspaceId)
        this[WorkspaceName::class] = StringVariable(::WorkspaceName)
    }
}
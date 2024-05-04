package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.*
import io.hamal.lib.common.serialization.SerdeModuleGeneric
import io.hamal.lib.common.value.serde.ValueVariableAdapters.Instant
import io.hamal.lib.common.value.serde.ValueVariableAdapters.Number
import io.hamal.lib.common.value.serde.ValueVariableAdapters.Object
import io.hamal.lib.common.value.serde.ValueVariableAdapters.SnowflakeId
import io.hamal.lib.common.value.serde.ValueVariableAdapters.String
import io.hamal.lib.domain.State

object SerdeModuleValueVariable : SerdeModuleGeneric() {
    init {
        this[CreatedAt::class] = Instant(::CreatedAt)
        this[UpdatedAt::class] = Instant(::UpdatedAt)

        this[AccountId::class] = SnowflakeId(::AccountId)

        this[Email::class] = String(::Email)

        this[AuthId::class] = SnowflakeId(::AuthId)
        this[AuthToken::class] = String(::AuthToken)
        this[Password::class] = String(::Password)
        this[PasswordHash::class] = String(::PasswordHash)
        this[PasswordSalt::class] = String(::PasswordSalt)

        this[RecipeId::class] = SnowflakeId(::RecipeId)
        this[RecipeName::class] = String(::RecipeName)
        this[RecipeInputs::class] = Object(::RecipeInputs)
        this[RecipeDescription::class] = String(::RecipeDescription)

        this[CmdId::class] = SnowflakeId(::CmdId)

        this[CodeId::class] = SnowflakeId(::CodeId)
        this[CodeVersion::class] = Number(::CodeVersion)

        this[CorrelationId::class] = String(::CorrelationId)

        this[CronPattern::class] = String(::CronPattern)

        this[ExecId::class] = SnowflakeId(::ExecId)
        this[ExecType::class] = String(::ExecType)
        this[ExecInputs::class] = Object(::ExecInputs)
        this[ExecResult::class] = Object(::ExecResult)
        this[ExecState::class] = Object(::ExecState)
        this[ExecScheduledAt::class] = Instant(::ExecScheduledAt)
        this[ExecQueuedAt::class] = Instant(::ExecQueuedAt)
        this[ExecCompletedAt::class] = Instant(::ExecCompletedAt)
        this[ExecFailedAt::class] = Instant(::ExecFailedAt)

        this[ExecToken::class] = String(::ExecToken)
        this[ExecLogId::class] = SnowflakeId(::ExecLogId)
        this[ExecLogMessage::class] = String(::ExecLogMessage)
        this[ExecLogTimestamp::class] = Instant(::ExecLogTimestamp)

        this[EndpointHeaders::class] = Object(::EndpointHeaders)
        this[EndpointParameters::class] = Object(::EndpointParameters)
        this[EndpointContent::class] = Object(::EndpointContent)

        this[EventPayload::class] = Object(::EventPayload)

        this[ExpiresAt::class] = Instant(::ExpiresAt)

        this[ExtensionId::class] = SnowflakeId(::ExtensionId)
        this[ExtensionName::class] = String(::ExtensionName)

        this[FeedbackId::class] = SnowflakeId(::FeedbackId)
        this[FeedbackMessage::class] = String(::FeedbackMessage)

        this[NamespaceId::class] = SnowflakeId(::NamespaceId)
        this[NamespaceTreeId::class] = SnowflakeId(::NamespaceTreeId)
        this[NamespaceName::class] = String(::NamespaceName)
        this[NamespaceFeatures::class] = Object(::NamespaceFeatures)


        this[FuncId::class] = SnowflakeId(::FuncId)
        this[FuncName::class] = String(::FuncName)
        this[FuncInputs::class] = Object(::FuncInputs)

        this[DeployedAt::class] = Instant(::DeployedAt)
        this[DeployMessage::class] = String(::DeployMessage)

        this[Limit::class] = Number(::Limit)
        this[Count::class] = Number(::Count)

        this[HookHeaders::class] = Object(::HookHeaders)
        this[HookParameters::class] = Object(::HookParameters)
        this[HookContent::class] = Object(::HookContent)
        this[InvocationInputs::class] = Object(::InvocationInputs)

        this[RequestId::class] = SnowflakeId(::RequestId)
        this[RequestClass::class] = String(::RequestClass)

        this[RunnerId::class] = SnowflakeId(::RunnerId)

        this[RunnerEnv::class] = Object(::RunnerEnv)
        this[State::class] = Object(::State)

        this[TopicId::class] = SnowflakeId(::TopicId)
        this[TopicName::class] = String(::TopicName)
        this[TopicEventId::class] = SnowflakeId(::TopicEventId)
        this[TopicEventPayload::class] = Object(::TopicEventPayload)

        this[TriggerId::class] = SnowflakeId(::TriggerId)
        this[TriggerDuration::class] = String(::TriggerDuration)
        this[TriggerName::class] = String(::TriggerName)
        this[TriggerInputs::class] = Object(::TriggerInputs)

        this[Web3Address::class] = String(::Web3Address)
        this[Web3Challenge::class] = String(::Web3Challenge)
        this[Web3Signature::class] = String(::Web3Signature)

        this[WorkspaceId::class] = SnowflakeId(::WorkspaceId)
        this[WorkspaceName::class] = String(::WorkspaceName)
    }
}
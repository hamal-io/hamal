package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.serialization.*
import io.hamal.lib.domain.State

object ValueObjectJsonModule : JsonModule() {
    init {
        this[AccountId::class] = ValueObjectIdAdapter(::AccountId)
        this[Email::class] = ValueObjectStringAdapter(::Email)
        this[Web3Address::class] = ValueObjectStringAdapter(::Web3Address)
        this[Web3Challenge::class] = ValueObjectStringAdapter(::Web3Challenge)
        this[Web3Signature::class] = ValueObjectStringAdapter(::Web3Signature)

        this[AuthId::class] = ValueObjectIdAdapter(::AuthId)
        this[AuthToken::class] = ValueObjectStringAdapter(::AuthToken)
        this[AuthTokenExpiresAt::class] = ValueObjectInstantAdapter(::AuthTokenExpiresAt)
        this[Password::class] = ValueObjectStringAdapter(::Password)
        this[PasswordHash::class] = ValueObjectStringAdapter(::PasswordHash)
        this[PasswordSalt::class] = ValueObjectStringAdapter(::PasswordSalt)

        this[BlueprintId::class] = ValueObjectIdAdapter(::BlueprintId)
        this[BlueprintName::class] = ValueObjectStringAdapter(::BlueprintName)
        this[BlueprintInputs::class] = ValueObjectHotObjectAdapter(::BlueprintInputs)

        this[CmdId::class] = ValueObjectStringAdapter(::CmdId)
        this[CodeId::class] = ValueObjectIdAdapter(::CodeId)
        this[CodeValue::class] = ValueObjectStringAdapter(::CodeValue)
        this[CodeVersion::class] = ValueObjectIntAdapter(::CodeVersion)

        this[CorrelationId::class] = ValueObjectStringAdapter(::CorrelationId)

        this[CronPattern::class] = ValueObjectStringAdapter(::CronPattern)

        this[EndpointId::class] = ValueObjectIdAdapter(::EndpointId)
        this[EndpointName::class] = ValueObjectStringAdapter(::EndpointName)
        this[EndpointHeaders::class] = ValueObjectHotObjectAdapter(::EndpointHeaders)
        this[EndpointParameters::class] = ValueObjectHotObjectAdapter(::EndpointParameters)
        this[EndpointContent::class] = ValueObjectHotObjectAdapter(::EndpointContent)

        this[EventPayload::class] = ValueObjectHotObjectAdapter(::EventPayload)

        this[ExecId::class] = ValueObjectIdAdapter(::ExecId)
        this[ExecType::class] = ValueObjectStringAdapter(::ExecType)
        this[ExecInputs::class] = ValueObjectHotObjectAdapter(::ExecInputs)
        this[ExecResult::class] = ValueObjectHotObjectAdapter(::ExecResult)
        this[ExecState::class] = ValueObjectHotObjectAdapter(::ExecState)
        this[ExecToken::class] = ValueObjectStringAdapter(::ExecToken)
        this[ExecLogId::class] = ValueObjectIdAdapter(::ExecLogId)

        this[ExtensionId::class] = ValueObjectIdAdapter(::ExtensionId)
        this[ExtensionName::class] = ValueObjectStringAdapter(::ExtensionName)

        this[FeedbackId::class] = ValueObjectIdAdapter(::FeedbackId)
        this[FeedbackMessage::class] = ValueObjectStringAdapter(::FeedbackMessage)

        this[FlowId::class] = ValueObjectIdAdapter(::FlowId)
        this[FlowName::class] = ValueObjectStringAdapter(::FlowName)
        this[FlowType::class] = ValueObjectStringAdapter(::FlowType)
        this[FlowInputs::class] = ValueObjectHotObjectAdapter(::FlowInputs)

        this[FuncId::class] = ValueObjectIdAdapter(::FuncId)
        this[FuncName::class] = ValueObjectStringAdapter(::FuncName)
        this[FuncInputs::class] = ValueObjectHotObjectAdapter(::FuncInputs)
        this[DeployMessage::class] = ValueObjectStringAdapter(::DeployMessage)
        this[DeployedAt::class] = ValueObjectInstantAdapter(::DeployedAt)

        this[GroupId::class] = ValueObjectIdAdapter(::GroupId)
        this[GroupName::class] = ValueObjectStringAdapter(::GroupName)

        this[HookId::class] = ValueObjectIdAdapter(::HookId)
        this[HookName::class] = ValueObjectStringAdapter(::HookName)
        this[HookHeaders::class] = ValueObjectHotObjectAdapter(::HookHeaders)
        this[HookParameters::class] = ValueObjectHotObjectAdapter(::HookParameters)
        this[HookContent::class] = ValueObjectHotObjectAdapter(::HookContent)

        this[Invocation::class] = Invocation.Adapter
        this[InvocationType::class] = ValueObjectStringAdapter(::InvocationType)
        this[InvocationInputs::class] = ValueObjectHotObjectAdapter(::InvocationInputs)

        this[RequestId::class] = ValueObjectIdAdapter(::RequestId)
        this[RequestType::class] = ValueObjectStringAdapter(::RequestType)
        this[RunnerEnv::class] = ValueObjectHotObjectAdapter(::RunnerEnv)

        this[State::class] = ValueObjectHotObjectAdapter(::State)
        this[TopicId::class] = ValueObjectIdAdapter(::TopicId)
        this[TopicName::class] = ValueObjectStringAdapter(::TopicName)
        this[TopicEntryId::class] = ValueObjectIdAdapter(::TopicEntryId)
        this[TopicEntryPayload::class] = ValueObjectHotObjectAdapter(::TopicEntryPayload)

        this[TriggerId::class] = ValueObjectIdAdapter(::TriggerId)
        this[TriggerName::class] = ValueObjectStringAdapter(::TriggerName)
        this[TriggerInputs::class] = ValueObjectHotObjectAdapter(::TriggerInputs)
    }
}

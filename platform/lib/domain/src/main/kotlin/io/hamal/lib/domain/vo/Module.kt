package io.hamal.lib.domain.vo

import io.hamal.lib.common.serialization.*
import io.hamal.lib.domain.request.Requested
import io.hamal.lib.domain.request.RequestedTypeAdapter

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

        this[CodeId::class] = ValueObjectIdAdapter(::CodeId)
        this[CodeValue::class] = ValueObjectStringAdapter(::CodeValue)
        this[CodeVersion::class] = ValueObjectIntAdapter(::CodeVersion)

        this[CorrelationId::class] = ValueObjectStringAdapter(::CorrelationId)

        this[CronPattern::class] = ValueObjectStringAdapter(::CronPattern)

        this[EndpointId::class] = ValueObjectIdAdapter(::EndpointId)
        this[EndpointName::class] = ValueObjectStringAdapter(::EndpointName)

        this[ExecId::class] = ValueObjectIdAdapter(::ExecId)
        this[ExecToken::class] = ValueObjectStringAdapter(::ExecToken)
        this[ExecLogId::class] = ValueObjectIdAdapter(::ExecLogId)

        this[ExtensionId::class] = ValueObjectIdAdapter(::ExtensionId)
        this[ExtensionName::class] = ValueObjectStringAdapter(::ExtensionName)

        this[FeedbackId::class] = ValueObjectIdAdapter(::FeedbackId)
        this[FeedbackMessage::class] = ValueObjectStringAdapter(::FeedbackMessage)

        this[FlowId::class] = ValueObjectIdAdapter(::FlowId)
        this[FlowName::class] = ValueObjectStringAdapter(::FlowName)
        this[FlowType::class] = ValueObjectStringAdapter(::FlowType)

        this[FuncId::class] = ValueObjectIdAdapter(::FuncId)
        this[FuncName::class] = ValueObjectStringAdapter(::FuncName)
        this[DeployMessage::class] = ValueObjectStringAdapter(::DeployMessage)
        this[DeployedAt::class] = ValueObjectInstantAdapter(::DeployedAt)

        this[GroupId::class] = ValueObjectIdAdapter(::GroupId)
        this[GroupName::class] = ValueObjectStringAdapter(::GroupName)

        this[HookId::class] = ValueObjectIdAdapter(::HookId)
        this[HookName::class] = ValueObjectStringAdapter(::HookName)

        this[RequestId::class] = ValueObjectIdAdapter(::RequestId)
        this[RequestType::class] = ValueObjectStringAdapter(::RequestType)
        this[Requested::class] = RequestedTypeAdapter()

        this[TopicId::class] = ValueObjectIdAdapter(::TopicId)
        this[TopicName::class] = ValueObjectStringAdapter(::TopicName)
        this[TopicEntryId::class] = ValueObjectIdAdapter(::TopicEntryId)

        this[TriggerId::class] = ValueObjectIdAdapter(::TriggerId)
        this[TriggerName::class] = ValueObjectStringAdapter(::TriggerName)
    }

}
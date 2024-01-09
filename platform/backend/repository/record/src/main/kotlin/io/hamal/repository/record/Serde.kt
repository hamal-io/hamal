package io.hamal.repository.record

import io.hamal.lib.common.serialization.JsonFactoryBuilder
import kotlin.reflect.KClass

val gsonInstance = JsonFactoryBuilder
//    .register(AccountId::class.java, ValueObjectIdAdapter(::AccountId))
//    .register(Email::class.java, ValueObjectStringAdapter(::Email))
//    .register(Web3Address::class.java, ValueObjectStringAdapter(::Web3Address))
//    .register(Web3Challenge::class.java, ValueObjectStringAdapter(::Web3Challenge))
//    .register(Web3Signature::class.java, ValueObjectStringAdapter(::Web3Signature))
//
//    .register(AuthId::class.java, ValueObjectIdAdapter(::AuthId))
//    .register(AuthToken::class.java, ValueObjectStringAdapter(::AuthToken))
//    .register(AuthTokenExpiresAt::class.java, ValueObjectInstantAdapter(::AuthTokenExpiresAt))
//    .register(Password::class.java, ValueObjectStringAdapter(::Password))
//    .register(PasswordHash::class.java, ValueObjectStringAdapter(::PasswordHash))
//    .register(PasswordSalt::class.java, ValueObjectStringAdapter(::PasswordSalt))
//
//    .register(BlueprintId::class.java, ValueObjectIdAdapter(::BlueprintId))
//    .register(BlueprintName::class.java, ValueObjectStringAdapter(::BlueprintName))
//
//    .register(CodeId::class.java, ValueObjectIdAdapter(::CodeId))
//    .register(CodeValue::class.java, ValueObjectStringAdapter(::CodeValue))
//    .register(CodeVersion::class.java, ValueObjectIntAdapter(::CodeVersion))
//
//    .register(CorrelationId::class.java, ValueObjectStringAdapter(::CorrelationId))
//
//    .register(CronPattern::class.java, ValueObjectStringAdapter(::CronPattern))
//
//    .register(EndpointId::class.java, ValueObjectIdAdapter(::EndpointId))
//    .register(EndpointName::class.java, ValueObjectStringAdapter(::EndpointName))
//
//    .register(ExecId::class.java, ValueObjectIdAdapter(::ExecId))
//    .register(ExecToken::class.java, ValueObjectStringAdapter(::ExecToken))
//    .register(ExecLogId::class.java, ValueObjectIdAdapter(::ExecLogId))
//
//    .register(ExtensionId::class.java, ValueObjectIdAdapter(::ExtensionId))
//    .register(ExtensionName::class.java, ValueObjectStringAdapter(::ExtensionName))
//
//    .register(FeedbackId::class.java, ValueObjectIdAdapter(::FeedbackId))
//    .register(FeedbackMessage::class.java, ValueObjectStringAdapter(::FeedbackMessage))
//
//    .register(FlowId::class.java, ValueObjectIdAdapter(::FlowId))
//    .register(FlowName::class.java, ValueObjectStringAdapter(::FlowName))
//    .register(FlowType::class.java, ValueObjectStringAdapter(::FlowType))
//
//    .register(FuncId::class.java, ValueObjectIdAdapter(::FuncId))
//    .register(FuncName::class.java, ValueObjectStringAdapter(::FuncName))
//    .register(DeployMessage::class.java, ValueObjectStringAdapter(::DeployMessage))
//    .register(DeployedAt::class.java, ValueObjectInstantAdapter(::DeployedAt))
//
//    .register(GroupId::class.java, ValueObjectIdAdapter(::GroupId))
//    .register(GroupName::class.java, ValueObjectStringAdapter(::GroupName))
//
//    .register(HookId::class.java, ValueObjectIdAdapter(::HookId))
//    .register(HookName::class.java, ValueObjectStringAdapter(::HookName))
//
//    .register(RequestId::class.java, ValueObjectIdAdapter(::RequestId))
//    .register(RequestType::class.java, ValueObjectStringAdapter(::RequestType))
//    .register(Requested::class.java, RequestedTypeAdapter())
//
//    .register(TopicId::class.java, ValueObjectIdAdapter(::TopicId))
//    .register(TopicName::class.java, ValueObjectStringAdapter(::TopicName))
//    .register(TopicEntryId::class.java, ValueObjectIdAdapter(::TopicEntryId))
//
//    .register(TriggerId::class.java, ValueObjectIdAdapter(::TriggerId))
//    .register(TriggerName::class.java, ValueObjectStringAdapter(::TriggerName))
//
//// KUA
//    .register(KuaAny::class.java, KuaAny.Serde)
//    .register(KuaArray::class.java, KuaArray.Serde)
//    .register(KuaDecimal::class.java, KuaDecimal.Serde)
//    .register(KuaBoolean::class.java, KuaBoolean.Serde)
//    .register(KuaMap::class.java, KuaMap.Serde)
//    .register(KuaNil::class.java, KuaNil.Serde)
//    .register(KuaType::class.java, KuaType.Serde)
//
//    .register(AccountRecord::class.java, AccountRecord.Serde)

    .build()

object RecordJson {

    fun <TYPE : Any> serialize(src: TYPE): String {
        return gsonInstance.toJson(src)
    }

    fun <TYPE : Any> serializeAndCompress(src: TYPE): ByteArray {
        val json = serialize(src)
        return json.toByteArray()
    }

    fun <TYPE : Any> decompressAndDeserialize(clazz: KClass<TYPE>, bytes: ByteArray): TYPE {
        return gsonInstance.fromJson(String(bytes), clazz.java)
    }

}
package io.hamal.lib.domain

import com.google.gson.reflect.TypeToken
import io.hamal.lib.common.serialization.*
import io.hamal.lib.domain.request.Requested
import io.hamal.lib.domain.request.RequestedTypeAdapter
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.*
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.reflect.KClass

val gsonInstance = GsonFactoryBuilder
    .registerTypeAdapter(AccountId::class.java, ValueObjectIdAdapter(::AccountId))
    .registerTypeAdapter(Email::class.java, ValueObjectStringAdapter(::Email))
    .registerTypeAdapter(Web3Address::class.java, ValueObjectStringAdapter(::Web3Address))
    .registerTypeAdapter(Web3Challenge::class.java, ValueObjectStringAdapter(::Web3Challenge))
    .registerTypeAdapter(Web3Signature::class.java, ValueObjectStringAdapter(::Web3Signature))

    .registerTypeAdapter(AuthId::class.java, ValueObjectIdAdapter(::AuthId))
    .registerTypeAdapter(AuthToken::class.java, ValueObjectStringAdapter(::AuthToken))
    .registerTypeAdapter(AuthTokenExpiresAt::class.java, ValueObjectInstantAdapter(::AuthTokenExpiresAt))
    .registerTypeAdapter(Password::class.java, ValueObjectStringAdapter(::Password))
    .registerTypeAdapter(PasswordHash::class.java, ValueObjectStringAdapter(::PasswordHash))
    .registerTypeAdapter(PasswordSalt::class.java, ValueObjectStringAdapter(::PasswordSalt))

    .registerTypeAdapter(BlueprintId::class.java, ValueObjectIdAdapter(::BlueprintId))
    .registerTypeAdapter(BlueprintName::class.java, ValueObjectStringAdapter(::BlueprintName))

    .registerTypeAdapter(CodeId::class.java, ValueObjectIdAdapter(::CodeId))
    .registerTypeAdapter(CodeValue::class.java, ValueObjectStringAdapter(::CodeValue))
    .registerTypeAdapter(CodeVersion::class.java, ValueObjectIntAdapter(::CodeVersion))

    .registerTypeAdapter(CorrelationId::class.java, ValueObjectStringAdapter(::CorrelationId))

    .registerTypeAdapter(CronPattern::class.java, ValueObjectStringAdapter(::CronPattern))

    .registerTypeAdapter(EndpointId::class.java, ValueObjectIdAdapter(::EndpointId))
    .registerTypeAdapter(EndpointName::class.java, ValueObjectStringAdapter(::EndpointName))

    .registerTypeAdapter(ExecId::class.java, ValueObjectIdAdapter(::ExecId))
    .registerTypeAdapter(ExecToken::class.java, ValueObjectStringAdapter(::ExecToken))
    .registerTypeAdapter(ExecLogId::class.java, ValueObjectIdAdapter(::ExecLogId))

    .registerTypeAdapter(ExtensionId::class.java, ValueObjectIdAdapter(::ExtensionId))
    .registerTypeAdapter(ExtensionName::class.java, ValueObjectStringAdapter(::ExtensionName))

    .registerTypeAdapter(FeedbackId::class.java, ValueObjectIdAdapter(::FeedbackId))
    .registerTypeAdapter(FeedbackMessage::class.java, ValueObjectStringAdapter(::FeedbackMessage))

    .registerTypeAdapter(FlowId::class.java, ValueObjectIdAdapter(::FlowId))
    .registerTypeAdapter(FlowName::class.java, ValueObjectStringAdapter(::FlowName))
    .registerTypeAdapter(FlowType::class.java, ValueObjectStringAdapter(::FlowType))

    .registerTypeAdapter(FuncId::class.java, ValueObjectIdAdapter(::FuncId))
    .registerTypeAdapter(FuncName::class.java, ValueObjectStringAdapter(::FuncName))
    .registerTypeAdapter(DeployMessage::class.java, ValueObjectStringAdapter(::DeployMessage))
    .registerTypeAdapter(DeployedAt::class.java, ValueObjectInstantAdapter(::DeployedAt))

    .registerTypeAdapter(GroupId::class.java, ValueObjectIdAdapter(::GroupId))
    .registerTypeAdapter(GroupName::class.java, ValueObjectStringAdapter(::GroupName))

    .registerTypeAdapter(HookId::class.java, ValueObjectIdAdapter(::HookId))
    .registerTypeAdapter(HookName::class.java, ValueObjectStringAdapter(::HookName))

    .registerTypeAdapter(RequestId::class.java, ValueObjectIdAdapter(::RequestId))
    .registerTypeAdapter(RequestType::class.java, ValueObjectStringAdapter(::RequestType))
    .registerTypeAdapter(Requested::class.java, RequestedTypeAdapter())

    .registerTypeAdapter(TopicId::class.java, ValueObjectIdAdapter(::TopicId))
    .registerTypeAdapter(TopicName::class.java, ValueObjectStringAdapter(::TopicName))
    .registerTypeAdapter(TopicEntryId::class.java, ValueObjectIdAdapter(::TopicEntryId))

    .registerTypeAdapter(TriggerId::class.java, ValueObjectIdAdapter(::TriggerId))
    .registerTypeAdapter(TriggerName::class.java, ValueObjectStringAdapter(::TriggerName))

    // KUA
    .registerTypeAdapter(KuaAny::class.java, KuaAny.Serde)
    .registerTypeAdapter(KuaArray::class.java, KuaArray.Serde)
    .registerTypeAdapter(KuaDecimal::class.java, KuaDecimal.Serde)
    .registerTypeAdapter(KuaBoolean::class.java, KuaBoolean.Serde)
    .registerTypeAdapter(KuaMap::class.java, KuaMap.Serde)
    .registerTypeAdapter(KuaNil::class.java, KuaNil.Serde)
    .registerTypeAdapter(KuaType::class.java, KuaType.Serde)

    .build()


object Serde {

    fun <TYPE : Any> serialize(src: TYPE): String {
        return gsonInstance.toJson(src)
    }

    fun <TYPE : Any> serializeAndCompress(src: TYPE): ByteArray {
        val json = serialize(src)
        return json.toByteArray()
    }

    fun <TYPE : Any> deserialize(clazz: KClass<TYPE>, content: String): TYPE {
        return gsonInstance.fromJson(content, clazz.java)
    }

    fun <TYPE : Any> deserialize(clazz: KClass<TYPE>, stream: InputStream): TYPE {
        return gsonInstance.fromJson(InputStreamReader(stream), clazz.java)
    }

    fun <TYPE : Any> deserialize(typeToken: TypeToken<TYPE>, stream: InputStream): TYPE {
        return gsonInstance.fromJson(InputStreamReader(stream), typeToken)
    }

    fun <TYPE : Any> decompressAndDeserialize(clazz: KClass<TYPE>, bytes: ByteArray): TYPE {
        return gsonInstance.fromJson(String(bytes), clazz.java)
    }

}
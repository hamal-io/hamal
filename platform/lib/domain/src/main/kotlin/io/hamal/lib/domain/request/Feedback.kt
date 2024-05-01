package io.hamal.lib.domain.request

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.domain._enum.FeedbackMood
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.AccountId.Companion.AccountId
import io.hamal.lib.domain.vo.FeedbackMessage.Companion.FeedbackMessage
import java.lang.reflect.Type

data class FeedbackCreateRequest(
    val mood: FeedbackMood,
    val message: FeedbackMessage,
    val accountId: AccountId?
) {
    object Adapter : JsonAdapter<FeedbackCreateRequest> {
        override fun serialize(
            src: FeedbackCreateRequest,
            typeOfSrc: Type,
            context: JsonSerializationContext
        ): JsonElement {
            val obj = JsonObject()
            obj.addProperty("mood", src.mood.value)
            obj.addProperty("message", src.message.value.stringValue)
            if (src.accountId != null) {
                obj.addProperty("accountId", src.accountId.stringValue)
            }
            return obj
        }

        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): FeedbackCreateRequest {
            val obj = json.asJsonObject
            val mood = FeedbackMood.of(obj.get("mood").asInt)
            val message = FeedbackMessage(obj.get("message").asString)
            val accountId = obj.get("accountId")?.let { AccountId(it.asString) }
            return FeedbackCreateRequest(mood, message, accountId)
        }
    }
}

data class FeedbackCreateRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: FeedbackId,
    val mood: FeedbackMood,
    val message: FeedbackMessage,
    val accountId: AccountId?
) : Requested()
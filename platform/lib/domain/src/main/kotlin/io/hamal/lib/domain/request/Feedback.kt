package io.hamal.lib.domain.request

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.domain._enum.FeedbackMood
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.FeedbackId
import io.hamal.lib.domain.vo.FeedbackMessage
import io.hamal.lib.domain.vo.RequestId
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
            return context.serialize(src)
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
    override val id: RequestId,
    override var status: RequestStatus,
    val feedbackId: FeedbackId,
    val mood: FeedbackMood,
    val message: FeedbackMessage,
    val accountId: AccountId?
) : Requested()
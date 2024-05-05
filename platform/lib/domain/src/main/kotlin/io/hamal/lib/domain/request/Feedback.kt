package io.hamal.lib.domain.request

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.*

data class FeedbackCreateRequest(
    val mood: FeedbackMood,
    val message: FeedbackMessage,
    val accountId: AccountId?
) {
    /*object Adapter : AdapterJson<FeedbackCreateRequest> {
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
            val mood = FeedbackMoods.of(obj.get("mood").asInt)
            val message = FeedbackMessage(obj.get("message").asString)
            val accountId = obj.get("accountId")?.let { AccountId(it.asString) }
            return FeedbackCreateRequest(mood, message, accountId)
        }
    }*/
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
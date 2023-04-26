package io.hamal.application.config

import io.hamal.lib.ddd.base.ValueObject
import io.hamal.lib.domain.vo.base.Id
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter

@Configuration
class WebConfig {
    @OptIn(InternalSerializationApi::class)
    @Bean
    fun messageConverter(): KotlinSerializationJsonHttpMessageConverter {
        return KotlinSerializationJsonHttpMessageConverter(Json {
            ignoreUnknownKeys = false
            serializersModule = SerializersModule {
//                contextual(Id::class, ValueObjectSerializer(Id.Value.serializer()))
//                contextual(Id.Value::class, IdValueSerializer())
//                contextual(Id::class, IdSerializer())
            }
        })
    }

    class IdSerializer : KSerializer<Id>{
        override val descriptor: SerialDescriptor
            get() = TODO("Not yet implemented")

        override fun deserialize(decoder: Decoder): Id {
            TODO("Not yet implemented")
        }

        override fun serialize(encoder: Encoder, value: Id) {
            encoder.encodeString(value.value.value)
        }

    }

    class IdValueSerializer : KSerializer<Id.Value> {
        override val descriptor: SerialDescriptor
            get() = TODO("Not yet implemented")

        override fun deserialize(decoder: Decoder): Id.Value {
            TODO("Not yet implemented")
        }

        override fun serialize(encoder: Encoder, value: Id.Value) {
            encoder.encodeString(value.value)
        }

    }

    class ValueObjectSerializer<T : Any>(
        private val dataSerializer: KSerializer<T>
    ) : KSerializer<ValueObject<T>> {

        override val descriptor: SerialDescriptor
            get() = dataSerializer.descriptor

        override fun deserialize(decoder: Decoder) = TODO()
        override fun serialize(encoder: Encoder, value: ValueObject<T>) = dataSerializer.serialize(encoder, value.value)

    }
}
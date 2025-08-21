package com.example.hotel_mobile.services.kSerializeChanger

import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import java.util.UUID

object UUIDListKserialize: KSerializer<List<UUID>> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: List<UUID>){
        val listSerialize = ListSerializer(String.serializer())
        encoder.encodeSerializableValue(listSerialize,value.map { it.toString() })
    }

    override fun deserialize(decoder: Decoder): List<UUID> {
        val listSerialize = ListSerializer(String.serializer())
    return    decoder.decodeSerializableValue(listSerialize).map { UUID.fromString(it) }
    }
}
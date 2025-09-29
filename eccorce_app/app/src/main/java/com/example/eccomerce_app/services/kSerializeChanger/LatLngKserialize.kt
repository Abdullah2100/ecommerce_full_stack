package com.example.hotel_mobile.services.kSerializeChanger

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import java.util.UUID

object LatLngKserialize : KSerializer<LatLng> {
    override val descriptor = PrimitiveSerialDescriptor("LatLng", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: LatLng) =
        encoder.encodeString("${value.latitude},${value.latitude}")

    override fun deserialize(decoder: Decoder): LatLng {
        val result = decoder.decodeString()
        val arrayResult = result.split(",")
        val lat = arrayResult[0] as Double
        val lng = arrayResult[1] as Double

        return LatLng(lat,lng)
    }
}
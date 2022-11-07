package krafana

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

interface SerializableAsString {
    fun serialize(): String
}

object AsStringSerializer : KSerializer<SerializableAsString> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("AsStringSerializer", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): SerializableAsString {
        throw UnsupportedOperationException()
    }

    override fun serialize(encoder: Encoder, value: SerializableAsString) {
        encoder.encodeString(value.serialize())
    }
}


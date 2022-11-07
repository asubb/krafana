package krafana

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

interface SerializableAsInt {
    fun serialize(): Int
}

object AsIntSerializer : KSerializer<SerializableAsInt> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("AsIntSerializer", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): SerializableAsInt {
        throw UnsupportedOperationException()
    }

    override fun serialize(encoder: Encoder, value: SerializableAsInt) {
        encoder.encodeInt(value.serialize())
    }
}


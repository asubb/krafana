package krafana

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.math.absoluteValue
import kotlin.random.Random

@Serializable
data class Templating(
    var list: MutableList<Template> = mutableListOf(),
)

@Serializable
data class Template(
    val name: String,
    val datasource: DataSource,
    var definition: Expr = Expr(""),
    var query: Query = Query(),
    var multi: Boolean = false,
    var type: String = "query",
    var includeAll: Boolean = false,
    var refresh: TemplateRefresh = TemplateRefresh.OnDashboardLoad,
    @Serializable(with = RegexSerializer::class)
    var regex: Regex? = null,
)

object RegexSerializer : KSerializer<Regex> {
    override val descriptor: SerialDescriptor
        get() = AsStringSerializer.descriptor

    override fun deserialize(decoder: Decoder): Regex {
        return decoder.decodeString().toRegex()
    }

    override fun serialize(encoder: Encoder, value: Regex) {
        encoder.encodeString(value.pattern)
    }

}

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(AsIntSerializer::class)
enum class TemplateRefresh(private val value: Int) : SerializableAsInt {
    OnDashboardLoad(1),
    OnTimeRangeChanged(2);

    override fun serialize(): Int {
        return value
    }
}

@Serializable
data class Query(
    var query: Expr = Expr(""),
    var refId: String = Random.nextLong().absoluteValue.toString(36),
)

fun DashboardParams.templating(builder: Pair<DataSource, Templating>.() -> Unit) {
    builder(Pair(this.datasource, this.dashboard.templating))
}

fun Pair<DataSource, Templating>.template(name: Var, builder: Template.() -> Unit) {
    val template = Template(name.name, this.first)
    this.second.list += template
    builder(template)
}

var Template.expr
    get() = this.query.query
    set(value) {
        query = Query(value)
        definition = value
    }

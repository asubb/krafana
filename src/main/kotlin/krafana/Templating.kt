package krafana

import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue
import kotlin.random.Random

@Serializable
data class Templating(
    var list: MutableList<Template> = mutableListOf()
)

@Serializable
data class Template(
    val name: String,
    val dataSource: DataSource,
    var definition: Expr = Expr(""),
    var query: Query = Query(),
    var multi: Boolean = false,
    var type: String = "query",
    var includeAll: Boolean = false,
    var refresh: TemplateRefresh = TemplateRefresh.OnDashboardLoad
)

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
    var refId: String = Random.nextLong().absoluteValue.toString(36)
)

fun Pair<Dashboard, DataSource>.templating(builder: Pair<DataSource, Templating>.() -> Unit) {
    builder(Pair(this.second, this.first.templating))
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

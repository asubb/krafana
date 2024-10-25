package krafana

interface DataPanel<out O: Options>: Panel<O> {
    val targets: MutableList<Target>
    val fieldConfig: FieldConfig
}

interface Panel<out O: Options> {
    val type: String
    var title: String
    var gridPos: GridPos
    val datasource: DataSource
    var repeat: Expr?
    var repeatDirection: RepeatDirection?
    val options: O
}

enum class RepeatDirection {
    h, v
}

fun Panel<*>.repeatHorizontal(e: Expr) {
    repeat = e
    repeatDirection = RepeatDirection.h
}

fun Panel<*>.repeatVertical(e: Expr) {
    repeat = e
    repeatDirection = RepeatDirection.v
}

var DataPanel<*>.measure
    get() = fieldConfig.defaults.unit
    set(value) {
        this.fieldConfig.defaults.unit = value
    }

var DataPanel<*>.colorMode
    get() = fieldConfig.defaults.color.mode
    set(value) {
        this.fieldConfig.defaults.color.mode = value
    }

package krafana

interface Panel {
    val type: String
    var title: String
    var gridPos: GridPos
    val datasource: DataSource
    val targets: MutableList<Target>
    val fieldConfig: FieldConfig
    var repeat: Expr?
    var repeatDirection: RepeatDirection?
}

enum class RepeatDirection {
    h, v
}

fun Panel.repeatHorizontal(e: Expr) {
    repeat = e
    repeatDirection = RepeatDirection.h
}

fun Panel.repeatVertical(e: Expr) {
    repeat = e
    repeatDirection = RepeatDirection.v
}

var Panel.measure
    get() = fieldConfig.defaults.unit
    set(value) {
        this.fieldConfig.defaults.unit = value
    }

var Panel.colorMode
    get() = fieldConfig.defaults.color.mode
    set(value) {
        this.fieldConfig.defaults.color.mode = value
    }

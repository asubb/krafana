package krafana

interface Panel {
    val type: String
    var title: String
    var gridPos: GridPos
    val datasource: DataSource
    val targets: MutableList<Target>
    val fieldConfig: FieldConfig
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

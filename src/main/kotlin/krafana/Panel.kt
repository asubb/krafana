package krafana

interface Panel {
    val type: String
    var title: String
    var gridPos: GridPos
}
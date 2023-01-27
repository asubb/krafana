package krafana

import kotlinx.serialization.Serializable

@Serializable
data class GridPos(
    var x: Int,
    var y: Int,
    var w: Int,
    var h: Int
)

interface GridPosSequence {
    fun next(): GridPos
}

fun constant(width: Int = 12, height: Int = 10): GridPosSequence {
    return object : GridPosSequence {
        override fun next(): GridPos = GridPos(0, 0, width, height)
    }
}

fun tile(width: Int = 12, height: Int = 10): GridPosSequence {
    val maxWidth = 24
    require(width in 1..maxWidth) { "Width should be greater than zero but less than $maxWidth, but found: $width" }
    require(height > 0) { "Height should be greater that zero" }
    var row = 0
    var col = 0
    return object : GridPosSequence {
        override fun next(): GridPos {
            val gridPos = GridPos(col, row, width, height)
            col += width
            if (col >= maxWidth) {
                row += height
                col = 0
            }
            return gridPos
        }
    }
}
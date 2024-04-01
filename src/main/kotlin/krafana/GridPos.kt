package krafana

import kotlinx.serialization.Serializable
import java.lang.Integer.max

@Serializable
data class GridPos(
    var x: Int,
    var y: Int,
    var w: Int,
    var h: Int,
)

interface GridPosSequence {
    fun next(w: Int? = null, h: Int? = null): GridPos
    fun closeRow()
}

fun DashboardParams.fullWidth(height: Int = 10): GridPos = gridPosSequence.next(24, height)

fun constant(width: Int = 24, height: Int = 10): GridPosSequence {
    return object : GridPosSequence {
        override fun next(w: Int?, h: Int?): GridPos = GridPos(0, 0, w ?: width, h ?: height)
        override fun closeRow() {
            // nothing to do
        }
    }
}

private const val maxWidth = 24

fun tileOneThird(height: Int = 10): GridPosSequence {
    return tile(width = maxWidth / 3, height)
}

fun tileOneForth(height: Int = 10): GridPosSequence {
    return tile(width = maxWidth / 4, height)
}

fun tile(width: Int = maxWidth / 2, height: Int = 10): GridPosSequence {
    require(width in 1..maxWidth) { "Width should be greater than zero but less than $maxWidth, but found: $width" }
    require(height > 0) { "Height should be greater that zero" }
    var row = 0
    var col = 0
    var prevH = height
    return object : GridPosSequence {
        override fun next(w: Int?, h: Int?): GridPos {
            val gridPos = GridPos(col, row, w ?: width, h ?: height)
            if (h != null) prevH = max(h, height)
            col += w ?: width
            if (col >= maxWidth) {
                row += prevH
                prevH = height
                col = 0
            }
            return gridPos
        }

        override fun closeRow() {
            row += prevH
            prevH = height
            col = 0
        }
    }
}
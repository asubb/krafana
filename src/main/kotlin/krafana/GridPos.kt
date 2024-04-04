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

const val defaultGridHeight = 10
const val gridMaxWidth = 24

fun DashboardParams.fullWidth(height: Int = defaultGridHeight): GridPos = gridPosSequence.next(24, height)

fun constant(width: Int = 24, height: Int = defaultGridHeight): GridPosSequence {
    return object : GridPosSequence {
        override fun next(w: Int?, h: Int?): GridPos = GridPos(0, 0, w ?: width, h ?: height)
        override fun closeRow() {
            // nothing to do
        }
    }
}

fun tileOneThird(height: Int = defaultGridHeight): GridPosSequence {
    return tile(width = gridMaxWidth / 3, height)
}

fun tileOneForth(height: Int = defaultGridHeight): GridPosSequence {
    return tile(width = gridMaxWidth / 4, height)
}

fun tile(width: Int = gridMaxWidth / 2, height: Int = defaultGridHeight): GridPosSequence {
    require(width in 1..gridMaxWidth) { "Width should be greater than zero but less than $gridMaxWidth, but found: $width" }
    require(height > 0) { "Height should be greater that zero" }
    var row = 0
    var col = 0
    var prevH = height
    return object : GridPosSequence {
        override fun next(w: Int?, h: Int?): GridPos {
            val gridPos = GridPos(col, row, w ?: width, h ?: height)
            if (h != null) prevH = max(h, height)
            col += w ?: width
            if (col >= gridMaxWidth) {
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
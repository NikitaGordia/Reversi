interface Board {

    fun getAvailableTurns(state: BoardState): List<Point>

    fun makeTurn(state: BoardState, turn: Point)

    data class Point(val x: Int, val y: Int) : Comparable<Point> {

        override fun compareTo(other: Point): Int =
            x.compareTo(other.x).takeIf { it != 0 } ?: y.compareTo(other.y)
    }

    interface BoardState {

        companion object {

            const val MY_POINT = 1.toByte()
            const val ENEMY_POINT = 2.toByte()
            const val EMPTY_POINT = 3.toByte()
        }

        fun takePoint(x: Int, y: Int)

        fun get(x: Int, y: Int): Byte

        fun getScore(): Point

        fun inverseState()

        fun display()

        fun copyState(): BoardState
    }
}
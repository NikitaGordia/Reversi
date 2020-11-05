import java.util.*

class BoardImpl(private val blackHole: Board.Point) : Board {

    private val dirV: List<Board.Point> = listOf(
        -1 to -1,
        -1 to 0,
        -1 to 1,
        0 to 1,
        1 to 1,
        1 to 0,
        1 to -1,
        0 to -1
    ).map {
        Board.Point(it.first, it.second)
    }

    override fun getAvailableTurns(state: Board.BoardState): List<Board.Point> {
        val turns = LinkedList<Board.Point>()
        for (i in 0 until 8)
            for (j in 0 until 8)
                if (checkPoint(i, j) && state.get(i, j) == Board.BoardState.EMPTY_POINT) {
                    for (k in 0 until 8)
                        runSearch(state, i, j, k, turns)
                }
        return turns
    }

    override fun makeTurn(state: Board.BoardState, turn: Board.Point) {
        state.takePoint(turn.x, turn.y)
        for (k in 0 until 8)
            runOccupationSearch(state, turn.x, turn.y, k)
    }

    private fun runOccupationSearch(state: Board.BoardState, x: Int, y: Int, dir: Int) {
        var posX = x + dirV[dir].x
        var posY = y + dirV[dir].y
        if (!checkPoint(posX, posY) || state.get(posX, posY) != Board.BoardState.ENEMY_POINT) return
        do {
            posX += dirV[dir].x
            posY += dirV[dir].y
        } while (checkPoint(posX, posY) &&
            state.get(posX, posX) == Board.BoardState.ENEMY_POINT
        )
        if (checkPoint(posX, posY) && state.get(posX, posY) == Board.BoardState.MY_POINT) {
            occupate(state, x, y, dir)
        }
    }

    private fun occupate(state: Board.BoardState, x: Int, y: Int, dir: Int) {
        var posX = x + dirV[dir].x
        var posY = y + dirV[dir].y
        do {
            state.takePoint(posX, posY)
            posX += dirV[dir].x
            posY += dirV[dir].y
        } while (checkPoint(posX, posY) &&
            state.get(posX, posX) == Board.BoardState.ENEMY_POINT
        )
    }

    private fun runSearch(state: Board.BoardState, x: Int, y: Int, dir: Int, turns: MutableList<Board.Point>) {
        var posX = x + dirV[dir].x
        var posY = y + dirV[dir].y
        if (!checkPoint(posX, posY) || state.get(posX, posY) != Board.BoardState.ENEMY_POINT) return
        do {
            posX += dirV[dir].x
            posY += dirV[dir].y
        } while (checkPoint(posX, posY) &&
            state.get(posX, posX) == Board.BoardState.ENEMY_POINT
        )
        if (checkPoint(posX, posY) && state.get(posX, posY) == Board.BoardState.MY_POINT) {
            turns.add(Board.Point(x, y))
        }
    }

    private fun checkPoint(x: Int, y: Int) =
        x >= 0 && x < 8 && y >= 0 && y < 8 && !(x == blackHole.x && y == blackHole.y)

    data class BoardStateImpl(
        private var matrix: Long = 68853694464,
        private var enemyMatrix: Long = 34628173824
    ) : Board.BoardState {

        companion object {

            const val MY_POINT = 1.toByte()
            const val ENEMY_POINT = 2.toByte()
            const val EMPTY_POINT = 3.toByte()
        }

        override fun takePoint(x: Int, y: Int) {
            matrix = matrix or (1L shl ((x shl 3) + y))
            if (get(enemyMatrix, x, y) > 0)
                enemyMatrix = enemyMatrix xor (1L shl ((x shl 3) + y))
        }

        override fun get(x: Int, y: Int): Byte = when {
            get(matrix, x, y) > 0 -> MY_POINT
            get(enemyMatrix, x, y) > 0 -> ENEMY_POINT
            else -> EMPTY_POINT
        }

        override fun getScore(): Board.Point {
            var x = 0
            var y = 0
            for (i in 0 until 8)
                for (j in 0 until 8) {
                    x += if (get(matrix, i, j) > 0) 1 else 0
                    y += if (get(enemyMatrix, i, j) > 0) 1 else 0
                }
            return Board.Point(x, y)
        }

        override fun mirrorState(): Board.BoardState =
            BoardStateImpl(enemyMatrix, matrix)

        override fun display() {
            println()
            for (i in 0 until 8)
                for (j in 0 until 8) {
                    val ch = when {
                        get(matrix, i, j) > 0 -> 'B'
                        get(enemyMatrix, i, j) > 0 -> 'W'
                        else -> '.'
                    }
                    if (j == 7) println(ch) else print(ch)
                }
            println()
        }

        private fun get(matrix: Long, x: Int, y: Int): Long =
            ((matrix shr ((x shl 3) + y)) and 0b01)
    }
}
///01234567
//0XXXXXXXX
//1XXXXXXXX
//2XXXXXXXX
//3XX.XX.XX
//4XXXXXXXX
//5XXXXXXXX
//6XXXXXXXX
//7XXXXXXXX
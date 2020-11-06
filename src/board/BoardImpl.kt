package board

import java.util.*
import kotlin.collections.ArrayDeque

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

    override fun getAvailableTurns(state: Board.BoardState): List<Board.Point>? {
        val turns = ArrayDeque<Board.Point>()
        var emptyPoints = 0
        for (i in 0 until 8)
            for (j in 0 until 8)
                if (checkPoint(i, j) && state.get(i, j) == Board.BoardState.EMPTY_POINT) {
                    emptyPoints++
                    for (k in 0 until 8)
                        runSearch(state, i, j, k, turns)
                }
        val uniq = TreeSet<Board.Point>(turns)
        return uniq.toList().takeIf { emptyPoints > 0 }
    }

    override fun makeTurn(state: Board.BoardState, turn: Board.Point) {
        if (state.get(turn.x, turn.y) != Board.BoardState.EMPTY_POINT) return
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
            state.get(posX, posY) == Board.BoardState.ENEMY_POINT
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
            state.get(posX, posY) == Board.BoardState.ENEMY_POINT
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
            state.get(posX, posY) == Board.BoardState.ENEMY_POINT
        )
        if (checkPoint(posX, posY) && state.get(posX, posY) == Board.BoardState.MY_POINT) {
            turns.add(Board.Point(x, y))
        }
    }

    private fun checkPoint(x: Int, y: Int) =
        x >= 0 && x < 8 && y >= 0 && y < 8 && !(x == blackHole.x && y == blackHole.y)

    data class BoardStateImpl(
        private val blackHole: Board.Point,
        private var matrix: Long = 68853694464,
        private var enemyMatrix: Long = 34628173824
    ) : Board.BoardState {

        var color = 'B'
        var enemyColor = 'W'

        companion object {

            const val MY_POINT = 1.toByte()
            const val ENEMY_POINT = 2.toByte()
            const val EMPTY_POINT = 3.toByte()
        }

        override fun takePoint(x: Int, y: Int) {
            val pos = (x shl 3) + y
            matrix = matrix or (1L shl pos)
            enemyMatrix = enemyMatrix and (1L shl pos).inv()
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

        override fun inverseState() {
            val tm = matrix
            matrix = enemyMatrix
            enemyMatrix = tm

            val tc = color
            color = enemyColor
            enemyColor = tc
        }

        override fun display() {
            println("B: $matrix, W: $enemyMatrix")
            for (i in 0 until 8)
                for (j in 0 until 8) {
                    val ch = when {
                        i == blackHole.x && j == blackHole.y -> 'O'
                        get(matrix, i, j) > 0 -> color
                        get(enemyMatrix, i, j) > 0 -> enemyColor
                        else -> '.'
                    }
                    if (j == 7) println(ch) else print(ch)
                }
            println()
        }

        override fun copyState(): Board.BoardState = BoardStateImpl(blackHole, matrix, enemyMatrix)

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
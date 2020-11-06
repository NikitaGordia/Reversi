package board

import board.Board.BoardState.Companion.EMPTY_POINT
import board.Board.BoardState.Companion.ENEMY_POINT
import board.Board.BoardState.Companion.MY_POINT
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.math.max
import kotlin.math.round

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

            private const val C = 6 //6
            private const val B = 3 //4
            private const val G = 1 //1
            private const val R = 2 //2

            private const val BACK_PENALTIES = 0.2 //0.3 - 99, 98, 99; 0,2 - 99; 0.1 -


            private val PENALTIES = arrayOf(
                //            0  1  2  3  4  5  6  7
                /*0*/ arrayOf(C, B, B, B, B, B, B, C),
                /*1*/ arrayOf(B, B, R, R, R, R, B, B),
                /*2*/ arrayOf(B, R, B, G, G, B, R, B),
                /*3*/ arrayOf(B, R, G, B, B, G, R, B),
                /*4*/ arrayOf(B, R, G, B, B, G, R, B),
                /*5*/ arrayOf(B, R, B, G, G, B, R, B),
                /*6*/ arrayOf(B, B, R, R, R, R, B, B),
                /*7*/ arrayOf(C, B, B, B, B, B, B, C)
            )
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
            var b = 0
            var w = 0
            for (i in 0 until 8)
                for (j in 0 until 8) {
                    val score = PENALTIES[i][j]
                    if (get(matrix, i, j) > 0) {
                        b += score
                        w -= (BACK_PENALTIES * score).toInt()
                    }
                    if (get(enemyMatrix, i, j) > 0) {
                        w += score
                        b += (BACK_PENALTIES * score).toInt()
                    }
                }
            return Board.Point(b, max(w, 1))
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

        override fun isEqual(state: Board.BoardState): Boolean {
            if (state is BoardStateImpl) {
                return (state.matrix == matrix && state.enemyMatrix == enemyMatrix) || (state.matrix == enemyMatrix && state.enemyMatrix == matrix)
            } else return false
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
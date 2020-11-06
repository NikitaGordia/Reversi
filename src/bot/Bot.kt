package bot

import board.Board

class Bot (
    private val board: Board,
    private val recDepth: Byte
): IBot {

    override fun makeTurn(state: Board.BoardState) {

        // Get available turns for bot.Bot
        val availableTurns = board.getAvailableTurns(state) ?: throw GameOverException()

        when {
            availableTurns.isEmpty() -> {
                return
            }
            availableTurns.size == 1 -> {
                board.makeTurn(state, availableTurns[0])
            }
        }

        // Evaluate turns using minimax
        val evaluations = ByteArray(availableTurns.size) {
            availableTurns[it].evaluateTurn(
                (recDepth - 1).toByte(),
                state = state.copyState(),
                min = Byte.MAX_VALUE,
                max = Byte.MIN_VALUE
            )
        }

        // Make the best evaluated turn
        val imin = evaluations.indexOfMin()
        if (imin == -1) {
            imin
            evaluations.indexOfMin()
        }
        board.makeTurn(state, availableTurns[imin])
    }

    /**
     * Evaluate the player's turn with regards to the given board matrices
     */
    fun Board.Point.evaluateTurn(
        depth: Byte,
        state: Board.BoardState, // state.copyState()
        min: Byte,
        max: Byte
    ): Byte {
        board.makeTurn(state, this)

        state.inverseState()

        return minimax(
            depth,
            state,
            min,
            max
        )
    }

    /**
     * Use recursive minimax algorithm
     */
    fun minimax(
        depth: Byte,
        state: Board.BoardState,
        min: Byte,
        max: Byte
    ): Byte {
        val availableTurns: List<Board.Point>? = board.getAvailableTurns(state)
        when {
            availableTurns == null || depth == 0.toByte() -> {
                val score = state.getScore()
                return if ((recDepth - depth) % 2 == 0) score.x.toByte() else score.y.toByte()
            }
            (recDepth - depth) % 2 == 0 -> {
                var maxEval = Byte.MIN_VALUE
                for (i in availableTurns.indices) {
                    val evaluation = availableTurns[i].evaluateTurn(
                        (depth - 1).toByte(),
                        state = state.copyState(),
                        min = min,
                        max = max
                    )
                    maxEval = maxOf(maxEval, evaluation)
                    val newMax = maxOf(max, maxEval)
                    if (min <= newMax) {
                        break
                    }
                }
                return maxEval
            }
            (recDepth - depth) % 2 == 1 -> {
                var minEval = Byte.MAX_VALUE
                for (i in availableTurns.indices) {
                    val evaluation = availableTurns[i].evaluateTurn(
                        (depth - 1).toByte(),
                        state = state.copyState(),
                        min = min,
                        max = max
                    )
                    minEval = minOf(minEval, evaluation)
                    val newMin = minOf(min, minEval)
                    if (max >= newMin) {
                        break
                    }
                }
                return minEval
            }
            else -> throw MinimaxNotMetException()
        }
    }

    /**
     * Find the index of the minimal element of the array
     */
    private fun ByteArray.indexOfMin(): Int {
        var minI = if (size == 0) {
            -1
        } else {
            0
        }
        var min = Byte.MAX_VALUE

        forEachIndexed { i, elem ->
            if (elem < min) {
                min = elem
                minI = i
            }
        }

        return minI
    }

    class MinimaxNotMetException(
        message: String = "Minimax didn't meet any of the requirements to continue the computation"
    ) : RuntimeException(message)

    class GameOverException(
        message: String = "Game over"
    ) : RuntimeException(message)
}
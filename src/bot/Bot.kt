package bot

import board.Board

class Bot (
    private val isBlack: Boolean,
    private val board: Board,
    private val state: Board.BoardState,
    private val depth: Byte
) {

    fun makeTurn() {

        val state = state.copyState()

        if (!isBlack) {
            state.inverseState()
        }

        // Init board

        // Get available turns for bot.Bot
        val availableTurns = board.getAvailableTurns(state)

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
                (depth - 1).toByte(),
                isMaximizing = true,
                state = state.copyState().apply { inverseState() },
                min = Byte.MAX_VALUE,
                max = Byte.MIN_VALUE,
                isGameOver = false
            )
        }

        // Make the best evaluated turn
        board.makeTurn(state, availableTurns[evaluations.indexOfMin()])
    }

    /**
     * Evaluate the player's turn with regards to the given board matrices
     */
    fun Board.Point.evaluateTurn(
        depth: Byte,
        isMaximizing: Boolean,
        state: Board.BoardState,
        min: Byte,
        max: Byte,
        isGameOver: Boolean
    ): Byte {
        board.makeTurn(state, this)

        return minimax(
            depth,
            board,
            isMaximizing,
            state.copyState().apply { inverseState() },
            min,
            max,
            isGameOver
        )
    }

    /**
     * Use recursive minimax algorithm
     */
    fun minimax(
        depth: Byte,
        board: Board,
        isMaximizing: Boolean,
        state: Board.BoardState,
        min: Byte,
        max: Byte,
        isGameOver: Boolean
    ): Byte {
        when {
            depth == 0.toByte() || isGameOver -> {
                return state.getScore().x.toByte()
            }
            isMaximizing -> {
                var maxEval = Byte.MIN_VALUE
                val availableTurns = board.getAvailableTurns(state)
                for (i in availableTurns.indices) {
                    val evaluation = availableTurns[i].evaluateTurn(
                        (depth - 1).toByte(),
                        !isMaximizing,
                        state = state.copyState().apply { inverseState() },
                        min = min,
                        max = max,
                        isGameOver = false
                    )
                    maxEval = maxOf(maxEval, evaluation)
                    val newMax = maxOf(max, maxEval)
                    if (min <= newMax) {
                        break
                    }
                }
                return maxEval
            }
            !isMaximizing -> {
                var minEval = Byte.MAX_VALUE
                val availableTurns = board.getAvailableTurns(state)
                for (i in availableTurns.indices) {
                    val evaluation = availableTurns[i].evaluateTurn(
                        (depth - 1).toByte(),
                        !isMaximizing,
                        state = state.copyState().apply { inverseState() },
                        min = min,
                        max = max,
                        isGameOver = false
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

    class MinimaxNotMetException(
        message: String = "Minimax didn't meet any of the requirements to continue the computation"
    ) : RuntimeException(message)

    /**
     * Find the index of the minimal element of the array
     */
    private fun ByteArray.indexOfMin(): Int {
        var minI = -1
        var min = Byte.MAX_VALUE

        forEachIndexed { i, elem ->
            if (elem < min) {
                min = elem
                minI = i
            }
        }

        return minI
    }
}
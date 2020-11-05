import util.evaluateMatrix
import util.indexOfMin

class Bot (
    private val isBlack: Boolean,
    private val board: Board,
    private val white: Array<BooleanArray>,
    private val black: Array<BooleanArray>,
    private val depth: Byte
) {

    fun makeTurn() {

        // Init board
        val botMatrix = if (isBlack) black else white
        val humanMatrix = if (isBlack) white else black

        // Get available turns for Bot
        val availableTurns = board.getAvailableTurns(botMatrix, humanMatrix)

        when {
            availableTurns.isEmpty() -> {
                pass()
            }
            availableTurns.size == 1 -> {
                board.makeTurn(
                    playerMatrix = botMatrix,
                    enemyMatrix = humanMatrix,
                    playerTurn = availableTurns[0]
                )
            }
        }

        System.gc()

        // Evaluate turns using minimax
        val evaluations = ByteArray(availableTurns.size) {
            availableTurns[it].evaluateTurn(
                (depth - 1).toByte(),
                isMaximizing = true,
                playerMatrix = botMatrix,
                enemyMatrix = humanMatrix,
                min = Byte.MAX_VALUE,
                max = Byte.MIN_VALUE,
                isGameOver = false
            )
        }

        // Make the best evaluated turn
        board.makeTurn(
            playerMatrix = botMatrix,
            enemyMatrix = humanMatrix,
            playerTurn = availableTurns[evaluations.indexOfMin()]
        )
    }

    /**
     * Do nothing
     */
    private fun pass() {}

    /**
     * Evaluate the player's turn with regards to the given board matrices
     */
    fun Board.Point.evaluateTurn(
        depth: Byte,
        isMaximizing: Boolean,
        playerMatrix: Array<BooleanArray>,
        enemyMatrix: Array<BooleanArray>,
        min: Byte,
        max: Byte,
        isGameOver: Boolean
    ): Byte {
        val (playerMatrixAfterTurn, enemyMatrixAfterTurn) = board.makeTurn(
            playerMatrix,
            enemyMatrix,
            this
        )

        return minimax(
            depth,
            board,
            isMaximizing,
            enemyMatrixAfterTurn,
            playerMatrixAfterTurn,
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
        playerMatrix: Array<BooleanArray>,
        enemyMatrix: Array<BooleanArray>,
        min: Byte,
        max: Byte,
        isGameOver: Boolean
    ): Byte {
        when {
            depth == 0.toByte() || isGameOver -> {
                return playerMatrix.evaluateMatrix()
            }
            isMaximizing -> {
                var maxEval = Byte.MIN_VALUE
                val availableTurns = board.getAvailableTurns(playerMatrix, enemyMatrix)
                for (i in availableTurns.indices) {
                    val evaluation = availableTurns[i].evaluateTurn(
                        (depth - 1).toByte(),
                        !isMaximizing,
                        playerMatrix = enemyMatrix,
                        enemyMatrix = playerMatrix,
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
                val availableTurns = board.getAvailableTurns(playerMatrix, enemyMatrix)
                for (i in availableTurns.indices) {
                    val evaluation = availableTurns[i].evaluateTurn(
                        (depth - 1).toByte(),
                        !isMaximizing,
                        playerMatrix = enemyMatrix,
                        enemyMatrix = playerMatrix,
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
}
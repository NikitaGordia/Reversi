package bot

import board.Board
import java.lang.Exception
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class Bot(
    private val board: Board,
    private val recDepth: Byte
) {

    private var minValue: Int = Int.MAX_VALUE
    private var minValuePosition: Byte = 0

    fun makeTurn(state: Board.BoardState, debugTime: Long = System.currentTimeMillis()) {
        minValue = Int.MAX_VALUE
        minValuePosition = 0

        val availableTurns = board.getAvailableTurns(state) ?: throw GameOverException()

        runLongJob(job = {
            // Get available turns for bot.Bot
            when {
                availableTurns.isEmpty() -> {
                    //return@suspend Result<Board.Point>(null)
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
                    max = Byte.MIN_VALUE,
                    turnsPosition = it.toByte()
                )
            }

            Result(availableTurns[evaluations.indexOfMin()])
        }, onTimeLimit = {
            Result(availableTurns[minValuePosition.toInt()])
        }, onResult = {
            board.makeTurn(state, it.value ?: return@runLongJob)
            println(System.currentTimeMillis() - debugTime)
        })
    }

    /**
     * Evaluate the player's turn with regards to the given board matrices
     */
    fun Board.Point.evaluateTurn(
        depth: Byte,
        state: Board.BoardState, // state.copyState()
        min: Byte,
        max: Byte,
        turnsPosition: Byte
    ): Byte {
        board.makeTurn(state, this)

        //state.display()

        return minimax(
            depth,
            state.apply { inverseState() },
            min,
            max,
            turnsPosition
        )
    }

    /**
     * Use recursive minimax algorithm
     */
    fun minimax(
        depth: Byte,
        state: Board.BoardState,
        min: Byte,
        max: Byte,
        turnsPosition: Byte
    ): Byte {
        val availableTurns: List<Board.Point>? = board.getAvailableTurns(state)
        when {
            availableTurns == null || depth == 0.toByte() -> {
                return state.getScore().x.toByte()
            }
            (recDepth - depth) % 2 == 0 -> {
                val score = state.getScore()
                if (score.x - score.y > minValue) {
                    minValue = score.x - score.y
                    minValuePosition = turnsPosition
                }

                var maxEval = Byte.MIN_VALUE
                for (i in availableTurns.indices) {
                    val evaluation = availableTurns[i].evaluateTurn(
                        (depth - 1).toByte(),
                        state = state.copyState(),
                        min = min,
                        max = max,
                        turnsPosition = turnsPosition
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
                val score = state.getScore()
                if (score.y - score.x > minValue) {
                    minValue = score.y - score.x
                    minValuePosition = turnsPosition
                }

                var minEval = Byte.MAX_VALUE
                for (i in availableTurns.indices) {
                    val evaluation = availableTurns[i].evaluateTurn(
                        (depth - 1).toByte(),
                        state = state.copyState(),
                        min = min,
                        max = max,
                        turnsPosition = turnsPosition
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

    class MinimaxNotMetException(
        message: String = "Minimax didn't meet any of the requirements to continue the computation"
    ) : RuntimeException(message)

    class GameOverException(
        message: String = "Game over"
    ) : RuntimeException(message)

    private fun runLongJob(job: () -> Result<Board.Point>, onTimeLimit: () -> Result<Board.Point>, onResult: (Result<Board.Point>) -> Unit) {
        val executor = Executors.newSingleThreadExecutor()
        val future: Future<Result<Board.Point>> = executor.submit<Result<Board.Point>>(job)

        onResult(try {
            future.get(1980, TimeUnit.MILLISECONDS).also {
                println("#GOODTIME")
            }
        } catch (e: Exception) {
            println("#TIMEOUT")
            onTimeLimit()
        })
    }

    data class Result<T>(val value: T? = null)
}

// 0B 2 3
// 1W 3 2
// 2B 2 3
// 3W 3 2
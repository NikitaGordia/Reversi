import board.Board
import board.BoardImpl
import bot.Bot
import bot.IBot
import bot.RandomBot
import java.lang.Exception

fun main() {

    val list = arrayListOf<Char>()
    val blackHole = Board.Point(2, 4)

    repeat(100) {
        try {

            val board = BoardImpl(blackHole)
            val state = BoardImpl.BoardStateImpl(blackHole)

            var turn = 0
            fun isBlackTurn() = turn % 2 == 0

            val botBlack: IBot = Bot(board, 4)
            val botWhite: IBot = RandomBot(board)

            var prevState = state.copyState()
            var prevStateCounter = 3
            runGame {
                (if (isBlackTurn()) botBlack else botWhite).makeTurn(state)

                if (prevState.isEqual(state)) {
                    prevStateCounter--
                    if (prevStateCounter <= 0) throw Bot.GameOverException()
                } else {
                    prevStateCounter = 3
                }
                prevState = state.copyState()
                val score = state.getScore()
                if (score.x == 0 || score.y == 0) throw Bot.GameOverException()

                turn++
                state.inverseState()
            }

            val score = state.getScore()
            val scoreB = if (isBlackTurn()) score.x else score.y
            val scoreW = if (isBlackTurn()) score.y else score.x

            val str = when {
                scoreB == scoreW -> "Draw"
                scoreB < scoreW -> "B won".also { list += 'B' }
                else -> "W won".also { list += 'W' }
            }
            println("${(list.count { it == 'B' }.toFloat() / list.size) * 100}%, ${list.count { it == 'B' }} / ${list.count { it == 'W' }}, $str")
        } catch (e: Exception) {

        }
    }
}

fun runGame(action: () -> Unit) {
    var isGameOver = false
    do {
        try {
            action()
        } catch (e: Bot.GameOverException) {
            isGameOver = true
        }
    } while (!isGameOver)
}
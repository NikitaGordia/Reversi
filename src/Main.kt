import board.Board
import board.BoardImpl
import bot.Bot

fun main() {
    val blackHole = Board.Point(2, 4)
    val board = BoardImpl(blackHole)
    val state = BoardImpl.BoardStateImpl(blackHole)

    var turn = 0
    fun isBlackTurn() = turn % 2 == 1

    val botBlack = Bot(board, 7)
    val botWhite = Bot(board, 7)

    runGame {
        turn++
        (if (isBlackTurn()) botBlack else botWhite).makeTurn(state)
        println("Turn #$turn")
        state.display()

        state.inverseState()
    }
}

fun runGame(action: () -> Unit) {
    var isGameOver = false
    do {
        try {
            action()
        } catch (e: Bot.GameOverException) {
            println("GAME IS OVER")
            isGameOver = true
        }
    } while (!isGameOver)
}
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
            fun isBlackTurn() = turn % 2 == 1

            val botBlack: IBot = RandomBot(board)
            val botWhite: IBot = Bot(board, 2)

            runGame {
                turn++
                (if (isBlackTurn()) botBlack else botWhite).makeTurn(state)
                //println("Turn #$turn")
                //state.display()

                state.display()

                state.inverseState()
            }

            val scoreX = state.getScore().x
            val colorX = state.color
            val scoreY = state.getScore().y
            val colorY = state.enemyColor

            when {
                scoreX == scoreY -> "Draw"
                scoreX < scoreY -> "$colorX won".also { list += colorX }
                else -> "$colorY won".also { list += colorY }
            }
        } catch (e: Exception) {

        }
    }

    println(list.size)
    println(list.count { it == 'W' })
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
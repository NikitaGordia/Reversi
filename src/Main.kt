import board.Board
import board.BoardImpl
import bot.Bot

fun main() {
    val blackHole = Board.Point(2, 4)
    val board = BoardImpl(blackHole)
    val state = BoardImpl.BoardStateImpl(blackHole)
    val bot = Bot(true, board, 5)
    bot.makeTurn(state)
    state.display()
}
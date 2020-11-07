import board.Board
import board.BoardImpl
import bot.Bot
import bot.IBot

class CommandLineWorker {

    private val letterToNumber = arrayOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H')

    fun runGame() {
        val blackHole = convertToPoint(readLine() ?: throw Exception("Invalid input"))
        val color = readLine()?.let {
            when (it) {
                "black" -> 0
                "white" -> 1
                else -> null
            }
        } ?: throw Exception("Invalid input")

        val board = BoardImpl(blackHole)
        val state = BoardImpl.BoardStateImpl(blackHole)
        val bot: IBot = Bot(board, 4)

        if (color == 1) {
            state.display()
            board.makeTurn(state, convertToPoint(readLine() ?: throw Exception("Invalid input")))
            state.display()
            state.inverseState()
        }
        var shouldGo = true
        do {
            try {
                val res = bot.makeTurn(state)
                println(res?.toLetter() ?: "pass")
                state.display()
                state.inverseState()

                board.makeTurn(state, convertToPoint(readLine() ?: throw Exception("Invalid input")))
                state.inverseState()
                state.display()
            } catch (e: Exception) {
                shouldGo = false
            }
        } while (shouldGo)
    }

    fun convertToPoint(str: String): Board.Point {
        if (str.length != 2) throw Exception("Invalid input")
        val first = letterToNumber.indexOfFirst {
            it.toUpperCase() == str.first()
        }.takeIf {
            it != -1
        } ?: throw Exception("Invalid input")
        val second = Integer.parseInt(str[1].toString()) - 1
        return Board.Point(first, second)
    }

    fun Board.Point.toLetter() =
        "${letterToNumber[x]}${y + 1}"

}
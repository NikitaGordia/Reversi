package bot

import board.Board

class RandomBot(private val board: Board) : IBot {

    override fun makeTurn(state: Board.BoardState) {
        val availableTurns = board.getAvailableTurns(state) ?: throw Bot.GameOverException()

        when {
            availableTurns.isEmpty() -> {
                return
            }
            availableTurns.size == 1 -> {
                board.makeTurn(state, availableTurns[0])
            }
        }

        board.makeTurn(state, availableTurns.random())
    }
}
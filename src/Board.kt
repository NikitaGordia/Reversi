interface Board {

    fun getAvailableTurns(playerMatrix: Array<BooleanArray>, enemyMatrix: Array<BooleanArray>): Array<Point>

    fun makeTurn(playerMatrix: Array<BooleanArray>, enemyMatrix: Array<BooleanArray>, playerTurn: Point): BoardState

    data class Point(val x: Byte, val y: Byte)

    data class BoardState(val player: Array<BooleanArray>, val enemy: Array<BooleanArray>)
}

class BoardMock: Board {

    override fun getAvailableTurns(
        playerMatrix: Array<BooleanArray>,
        enemyMatrix: Array<BooleanArray>
    ): Array<Board.Point> {
        return arrayOf()
    }

    override fun makeTurn(
        playerMatrix: Array<BooleanArray>,
        enemyMatrix: Array<BooleanArray>,
        playerTurn: Board.Point
    ): Board.BoardState {
        return Board.BoardState(arrayOf(), arrayOf())
    }
}
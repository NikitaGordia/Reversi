
fun main() {
    val board = BoardImpl(Board.Point(2, 4))
    val state = BoardImpl.BoardStateImpl()
    state.display()
    val turns = board.getAvailableTurns(state)
    print(turns)
//    board.makeTurn(state, turns[0])
//    state.display()
//
//    board.makeTurn(state, turns[1])
//    state.display()
}
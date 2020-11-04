interface Board {

    fun getInfo(): BoardInfo

    fun turn(to: Point)

    data class BoardInfo(
        val board: String, // ....BW./n...WWW./n..BBWB../n
        val playerTurn: Int, // 1/2
        val availableTurns: List<AvailableTurn>,
        val blackHole: Point
    ) {

        data class AvailableTurn(val to: Point)
    }

    data class Point(val x: Int, val y: Int)
}
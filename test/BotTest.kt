import org.junit.Test
import util.evaluateMatrix
import util.indexOfMax
import util.indexOfMin
import kotlin.random.Random
import kotlin.test.assertEquals

class BotTest {

    @Test fun `test indexOfMin()`() {
        val byteArray = byteArrayOf(123, 5, 22, 1, 1, 67, 4, 126, 126, 2)
        assertEquals(byteArray.indexOf(byteArray.min()!!), byteArray.indexOfMin())
    }

    @Test fun `test indexOfMax()`() {
        val byteArray = byteArrayOf(123, 5, 22, 1, 1, 67, 4, 126, 126, 2)
        assertEquals(byteArray.indexOf(byteArray.max()!!), byteArray.indexOfMax())
    }

    @Test fun `test evaluateMatrix()`() {
        fun randomBooleanArray() = BooleanArray(8) { Random.nextBoolean() }

        fun BooleanArray.countTrue() = count { it }

        val row11 = randomBooleanArray()
        val row12 = randomBooleanArray()
        val row13 = randomBooleanArray()
        val byteMatrix1 = arrayOf(row11, row12, row13)
        
        assertEquals((row11 + row12 + row13).countTrue(), byteMatrix1.evaluateMatrix().toInt())

        val row21 = randomBooleanArray()
        val row22 = randomBooleanArray()
        val row23 = randomBooleanArray()
        val byteMatrix2 = arrayOf(row21, row22, row23)

        assertEquals((row21 + row22 + row23).countTrue(), byteMatrix2.evaluateMatrix().toInt())
    }
}
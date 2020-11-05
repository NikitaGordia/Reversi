package util


/**
 * Find the index of the minimal element of the array
 */
fun ByteArray.indexOfMin(): Int {
    var minI = -1
    var min = Byte.MAX_VALUE

    forEachIndexed { i, elem ->
        if (elem < min) {
            min = elem
            minI = i
        }
    }

    return minI
}

/**
 * Find the index of the maximal element of the array
 */
fun ByteArray.indexOfMax(): Int {
    var maxI = -1
    var max = Byte.MIN_VALUE

    forEachIndexed { i, elem ->
        if (elem > max) {
            max = elem
            maxI = i
        }
    }

    return maxI
}

/**
 * Count filled cells (N^2)
 */
fun Array<BooleanArray>.evaluateMatrix(): Byte {
    var count: Byte = 0
    for (row in this) {
        for (boolValue in row) {
            if (boolValue) {
                count++
            }
        }
    }
    return count
}
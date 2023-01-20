import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


fun main() = runBlocking<Unit> {

    simple1().withLatestFrom(simple2()) { a, b -> "$a  $b" }.collect { value -> // collect and print
        println("myCustomOperator PRINTS $value ")
    }

}

fun <T1, T2, R> Flow<T1>.withLatestFrom(other: Flow<T2>, transform: suspend (T1, T2) -> R): Flow<R> = flow {
    coroutineScope {

        val ch1 = Channel<T1>()
        val ch2 = Channel<T2>(CONFLATED)

        launch {
            this@withLatestFrom.collect {
                ch1.send(it)
            }
            ch1.close()
        }

        launch {
            other.collect {
                ch2.send(it)
            }
            ch2.close()
        }
        var t2: T2? = null
        for (t1 in ch1) {
            if (!ch2.isEmpty && !ch2.isClosedForReceive) {
                t2 = ch2.receive()
            }
            if (t2 != null) {
                emit(transform(t1, t2))
            }
        }
        println("finished")
    }
}



fun simple1(): Flow<Int> = flow {
    emit(1)
    delay(1000)
    emit(2)
    delay(2000)
    emit(3)
    delay(500)
    emit(4)
    delay(700)
    emit(5)
}

fun simple2(): Flow<String> = flow {
    delay(400)
    emit("A")
    delay(900)
    emit("B")
    delay(1000)
    emit("C")
    delay(300)
    emit("D")
}


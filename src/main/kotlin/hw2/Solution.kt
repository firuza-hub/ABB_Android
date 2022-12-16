import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*

val state = MutableStateFlow("empty") // flow to update UI (in our case just print to logcat)

fun main() {
        runSync()
        runAsync()


    CoroutineScope(Dispatchers.IO).launch {
        state.collect { println(it.toString()) }
    }
    readln()
}

fun runSync() {
    CoroutineScope(Dispatchers.IO).launch {
        println("runSync method.")
        for (i in 1..1000) {
            doWork(i.toString())
        }
    }
}

 fun runAsync() {
    println("runAsync method.")
        for( i in 1..1000){
            CoroutineScope(Dispatchers.IO).launch {  doWork(i.toString())}
        }
}

private suspend fun doWork(name: String) {
    delay(500)
    state.update { "$name completed." }
}
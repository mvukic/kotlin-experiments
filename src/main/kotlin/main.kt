enum class State {
    START, STOP, END
}

fun main() {
    println("main")
    for (n in 2..<10) {
        println(n)
    }
    println(State.entries)
}

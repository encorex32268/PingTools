import sun.misc.Signal
import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*


const val TIMER_DELAY = 1000L
const val TIMER_PERIOD = 1000L
var loop = 1

val runtime = Runtime.getRuntime()
lateinit var process : Process

var file = File("./pingResult_${getDateString()}.txt")

val runnable = Runnable {
    val command = "ping $pingAddress -t "
    runSystemCommand(command)
}
var pingAddress  = "8.8.8.8"
var pingSeconds = 120
var pingWaitTime = 30
var counter = 0
var wait = 0


fun main(args: Array<String>) {
   PingIntit()
}

private fun PingIntit() {
    println(">>>>>>>>>>Ping Tools<<<<<<<<<<")
    print("PingAddress(8.8.8.8) : ")
    var inputPingAddress = readLine().toString()
    if (inputPingAddress.isNotEmpty()){
        pingAddress = inputPingAddress
    }
    print("Ping Seconds(120) : ")
    var inputSeconds = readLine().toString()
    if (inputSeconds.isNotEmpty()){
        pingSeconds = inputSeconds.toInt()
    }
    print("After Ping Wait Time(30) :")
    val inputWaitTime = readLine().toString()
    if (inputWaitTime.isNotEmpty()){
        pingWaitTime = inputWaitTime.toInt()
    }
    startConsole()
    var thread = Thread(runnable)
    thread.start()

    val timer = Timer()
    val timerTask = object : TimerTask() {
        override fun run() {
            counter++
            if (counter >= pingSeconds) {
                if (process.isAlive) {
                    thread.interrupt()
                    process.destroyForcibly()
                    println(">>> $pingSeconds Ping Finish <<<")
                }
                wait++
            }
            if (wait > pingWaitTime) {
                resetCounters()
                thread = Thread(runnable).apply { start() }
            }

        }


    }
    timer.schedule(timerTask, TIMER_DELAY, TIMER_PERIOD)
}

private fun startConsole() {
    val stringBuffer = StringBuffer()
    stringBuffer.apply {
        append("------------------Settings------------------ \n")
        append("Start Ping     -> $pingAddress \n")
        append("PingAddress    -> $pingAddress  \n")
        append("Seconds        -> $pingSeconds \n")
        append("WaitTimes      -> $pingWaitTime \n")
        append("FileName -> ${file.name} \n")
        append("------------------Settings------------------ \n")

    }
    println(stringBuffer.toString())
    val fileWriter = FileWriter(file,true)
    fileWriter.write(stringBuffer.toString())
    fileWriter.flush()
}

fun runSystemCommand(command: String?) {
    try {
        process =  runtime.exec(command)
        val inputStream = BufferedReader(
            InputStreamReader(process.inputStream)
        )
        var s: String? = ""
        // reading output stream of the command
        val fileWriter = FileWriter(file,true)
        fileWriter.write("Ping Loop : $loop ")
        fileWriter.flush()
        while (inputStream.readLine().also { s = it } != null) {
            fileWriter.write(s+"\n")
            fileWriter.flush()
            println(s)
        }
        loop++
        fileWriter.close()

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun getDateString() : String {
    val date = System.currentTimeMillis()
    val pattern = "MM_dd_HH-mm-ss"
    val simpleDateFormat = SimpleDateFormat(pattern)
    return simpleDateFormat.format(Date(date))
}
fun resetCounters() {
    wait = 0
    counter = 0
}
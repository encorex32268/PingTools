import jodd.io.StreamGobbler
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


/**
 * Send CTRL-C to the process using a given PID
 * @param processID
 */
class SendCTRLC {
    fun sendCTRLC(processID: Int) {
        println(" Sending CTRL+C to PID$processID")
        try {
            val p = Runtime.getRuntime().exec("cmd /c ext\\\\SendSignalC.exe$processID")
//        StreamGobbler.StreamGobblerLOGProcess(p)
        StreamGobbler(p.inputStream).start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    /**
     * Get all PIDs for a given name and send CTRL-C to all
     * @param processName
     * @return
     */
    fun sendCTRLC(processName: String): List<String>? {
        // get all ProcessIDs for the processName
        val processIDs: List<String> = getProcessIDs(processName)!!
        println("" + processIDs.size + " PIDs found for " + processName + ": " + processIDs.toString())
        for (pid in processIDs) {
            // close it
            sendCTRLC(pid.toInt())
        }
        return processIDs
    }

    /**
     * Get List of PIDs for a given process name
     * @param processName
     * @return
     */
    fun getProcessIDs(processName: String?): List<String>? {
        val processIDs: MutableList<String> = ArrayList()
        try {
            var line: String
            val p = Runtime.getRuntime().exec("tasklist /v /fo csv")
            val input = BufferedReader(InputStreamReader(p.inputStream))
            while (input.readLine().also { line = it } != null) {
                if (line.trim { it <= ' ' } != "") {
                    // Pid is after the 1st ", thus it's argument 3 after splitting
                    val currentProcessName = line.split("\"").toTypedArray()[1]
                    // Pid is after the 3rd ", thus it's argument 3 after splitting
                    val currentPID = line.split("\"").toTypedArray()[3]
                    if (currentProcessName.equals(processName, ignoreCase = true)) {
                        processIDs.add(currentPID)
                    }
                }
            }
            input.close()
        } catch (err: Exception) {
            err.printStackTrace()
        }
        return processIDs
    }
}

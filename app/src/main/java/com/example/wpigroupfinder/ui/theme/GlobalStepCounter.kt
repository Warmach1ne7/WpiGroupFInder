import android.content.Context

object GlobalStepCounter {
    lateinit var stepCounter: StepCounter

    fun init(context: Context) {
        if (!::stepCounter.isInitialized) {
            stepCounter = StepCounter(context.applicationContext)
            stepCounter.startListen()
        }
    }
}
package orionedutech.`in`.lmstrainerapp.interfaces

class profilebooleantoggle private constructor() {
    private var capturelistener : capture? = null
    interface capture {
        fun capturepic(boolean: Boolean)
    }
    fun setListener(listener: capture) {
        capturelistener=listener
    }
    companion object{
        private  var mInstance : profilebooleantoggle? = null
        val theRealInstance : profilebooleantoggle
            get(){
                if (mInstance == null) {
                    mInstance = profilebooleantoggle()
                }
                return mInstance!!
            }
    }


    fun toggle(boolean: Boolean) {
        if (capturelistener != null) {
            notifyStateChange(boolean)
        }
    }

    private fun notifyStateChange(boolean: Boolean) {
        capturelistener!!.capturepic(boolean)
    }
}
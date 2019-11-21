package orionedutech.`in`.lmstrainerapp.interfaces

class flashtoggle private constructor() {
    private var capturelistener : capture? = null
    interface capture {
        fun flash()
    }
    fun setListener(listener: capture) {
        capturelistener=listener
    }
    companion object{
        private  var mInstance : flashtoggle? = null
        val theRealInstance : flashtoggle
            get(){
                if (mInstance == null) {
                    mInstance = flashtoggle()
                }
                return mInstance!!
            }
    }


    fun flashtoggle() {
        if (capturelistener != null) {
            notifyStateChange()
        }
    }

    private fun notifyStateChange() {
        capturelistener!!.flash()
    }
}
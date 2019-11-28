package orionedutech.`in`.lmstrainerapp.interfaces

class flashtoggle private constructor() {
    private var capturelistener : capture? = null
    interface capture {
        fun flash(path: String)
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


    fun flashtoggle(path :String) {
        if (capturelistener != null) {
            notifyStateChange(path)
        }
    }

    private fun notifyStateChange(path: String) {
        capturelistener!!.flash(path)
    }
}
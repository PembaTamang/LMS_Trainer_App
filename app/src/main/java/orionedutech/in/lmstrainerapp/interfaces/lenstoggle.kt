package orionedutech.`in`.lmstrainerapp.interfaces

class lenstoggle private constructor() {
    private var capturelistener : capture? = null
    interface capture {
        fun capturepic()
    }
    fun setListener(listener: capture) {
        capturelistener=listener
    }
    companion object{
        private  var mInstance : lenstoggle? = null
        val theRealInstance : lenstoggle
            get(){
                if (mInstance == null) {
                    mInstance = lenstoggle()
                }
                return mInstance!!
            }
    }


    fun lenstog() {
        if (capturelistener != null) {
            notifyStateChange()
        }
    }

    private fun notifyStateChange() {
        capturelistener!!.capturepic()
    }
}
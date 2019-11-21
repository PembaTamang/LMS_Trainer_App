package orionedutech.`in`.lmstrainerapp.interfaces

class CaptureInterface private constructor() {
    private var capturelistener : capture? = null
    interface capture {
        fun capturepic()
        fun toggle1ens()
        fun flash1()
    }
    fun setListener(listener: capture) {
        capturelistener=listener
    }
    companion object{
        private  var mInstance : CaptureInterface? = null
        val theRealInstance : CaptureInterface
            get(){
                if (mInstance == null) {
                    mInstance = CaptureInterface()
                }
                return mInstance!!
            }
    }


    fun capture() {
        if (capturelistener != null) {
            capturelistener!!.capturepic()
        }
    }
    fun toggleflash() {
        if (capturelistener != null) {
            capturelistener!!.flash1()
        }
    }
    fun togglelens() {
        if (capturelistener != null) {
            capturelistener!!.toggle1ens()
        }
    }

    private fun notifyStateChange() {

    }
}
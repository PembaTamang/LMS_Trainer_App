package orionedutech.`in`.lmstrainerapp.interfaces

class MoveNavBar private constructor() {
    private var movelistener : move? = null
    interface move {
        fun movebar()
    }
    fun setListener(listener: move) {
        movelistener=listener
    }
    companion object{
     private  var mInstance : MoveNavBar? = null
        val theRealInstance : MoveNavBar
        get(){
            if (mInstance == null) {
                mInstance = MoveNavBar()
            }
            return mInstance!!
        }
    }


    fun refresh() {
        if (movelistener != null) {
            notifyStateChange()
        }
    }

    private fun notifyStateChange() {
        movelistener!!.movebar()
    }
}
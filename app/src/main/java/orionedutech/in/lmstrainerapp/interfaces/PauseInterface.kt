package orionedutech.`in`.lmstrainerapp.interfaces

class PauseInterface private constructor() {
    companion object{
        var classInstance : PauseInterface? =null
        get() {
            if (field == null) {
                classInstance = PauseInterface()
            }
            return field
        }
    }

        //define interface
        interface Pause{
            fun pause()
        }
        private var pauseInterface : Pause? = null
        fun setListener(listener : Pause){
            pauseInterface = listener
        }

        fun pauseVideo(){
            if(pauseInterface!=null){
            pauseInterface!!.pause()
            }
        }


}
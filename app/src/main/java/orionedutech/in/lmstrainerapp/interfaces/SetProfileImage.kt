package orionedutech.`in`.lmstrainerapp.interfaces

class SetProfileImage private constructor() {
    companion object{
        var classInstance : SetProfileImage? =null
        get() {
            if(field ==null){
                classInstance = SetProfileImage()
            }
            return field
        }
    }

    private var interfaceInstance : Image? = null

    interface Image {
        fun setImage()
    }
    fun setListener(listener : Image){
        interfaceInstance = listener
    }

    fun set(){
       interfaceInstance!!.setImage()
    }
}
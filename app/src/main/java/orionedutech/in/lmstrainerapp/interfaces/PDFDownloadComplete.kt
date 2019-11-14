package orionedutech.`in`.lmstrainerapp.interfaces

class PDFDownloadComplete private constructor() {
    private var mListener: complete? = null

    interface complete {
        fun refreshData()
    }

    fun setListener(listener: complete) {
        mListener = listener
    }

    fun refresh() {
        if (mListener != null) {
            notifyStateChange()
        }
    }

    private fun notifyStateChange() {
        mListener!!.refreshData()
    }

    companion object {

        private var mInstance: PDFDownloadComplete? = null

        val instance: PDFDownloadComplete
            get() {
                if (mInstance == null) {
                    mInstance = PDFDownloadComplete()
                }
                return mInstance!!
            }
    }
}


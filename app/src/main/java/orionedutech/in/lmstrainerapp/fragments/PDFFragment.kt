package orionedutech.`in`.lmstrainerapp.fragments


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_pdf.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mToast
import orionedutech.`in`.lmstrainerapp.network.downloader.MDownloader
import java.io.*


/**
 * A simple [Fragment] subclass.
 */

class PDFFragment : BaseFragment() {
    lateinit var name: TextView
    lateinit var pdfView: PDFView
    var currentPage: Int = 0
    var path = ""
    lateinit var destinationFile : File
    lateinit var sourceFile : File
    lateinit var pdfPref: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pdf, container, false)
        name = view.pdfName
        pdfView = view.pdfView
        pdfPref = activity!!.getSharedPreferences("pdf", Context.MODE_PRIVATE)
        path = arguments!!.getString("path")!!
        sourceFile = File(path)
        val naam = arguments!!.getString("name")

        name.text = naam

            pdfView.fromFile(sourceFile)
                .scrollHandle(DefaultScrollHandle(context))
                .defaultPage(pdfPref.getInt(path, 0)).onPageError { page, t ->
                    run {
                        mToast.showToast(context, "error loading pdf")
                    }
                }.onPageChange { page, pageCount ->
                    currentPage = page

                }.load()



        view.export.setOnClickListener {
            MaterialAlertDialogBuilder(context).setTitle("Alert")
                .setMessage("Do you want to export the file to storage?")
                .setPositiveButton("export"){dialogInterface, i ->
                    dialogInterface.dismiss()
                     destinationFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/"+sourceFile.name)
                    mLog.i(mLog.TAG,"destination path : ${destinationFile.absolutePath}")
                    destinationFile.parentFile!!.mkdirs()
                    destinationFile.createNewFile()
                    CoroutineScope(Dispatchers.IO).launch {

                        copyToExternalStorage(path)
                    }
                }.setNegativeButton("cancel"){dialogInterface, i ->
                    dialogInterface.dismiss()
                }.create().show()
        }

        return view
    }

    override fun onStop() {
        pdfPref.edit().putInt(path, currentPage).apply()
        super.onStop()
    }

    private suspend fun copyToExternalStorage(path:String) {
        val sourceFile = File(path)
        if(sourceFile.exists()){

            val inp = FileInputStream(sourceFile)
            val out = FileOutputStream(destinationFile)
            withContext(Dispatchers.Main){
                mToast.showToast(context,"please wait")
            }
            inp.copyTo(out){ current, _ ->
                /*  percentage = ((current.toFloat()/totalBytes.toFloat())*100).toLong()
                  notificationBuilder.setProgress(100,percentage.toInt(),false)*/

            }
            MDownloader.showNotification(
                context!!,
                "${destinationFile.name} has been copied to ${destinationFile.absolutePath} ",
                "",
                true
            )
            activity?.runOnUiThread {
                MaterialAlertDialogBuilder(context).setTitle("Alert")
                    .setMessage("The file has been copied to ${destinationFile.absolutePath}")
                    .setPositiveButton("Ok"){dialogInterface, i ->
                        dialogInterface.dismiss()
                    }.create().show()
            }


            mLog.i(mLog.TAG,"copy complete")

        }else{
            mLog.i(mLog.TAG,"file does not exist")
        }

    }
    fun InputStream.copyTo(out: OutputStream, onCopy: (Long, Int) -> Unit): Long {
        var bytesCopied: Long = 0
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var bytes = read(buffer)
        while (bytes >= 0) {
            out.write(buffer, 0, bytes)
            bytesCopied += bytes
            onCopy(bytesCopied, bytes)
            bytes = read(buffer)
        }
        return bytesCopied
    }
}

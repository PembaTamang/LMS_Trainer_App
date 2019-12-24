package orionedutech.`in`.lmstrainerapp.fragments


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_manual.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.database.MDatabase
import orionedutech.`in`.lmstrainerapp.interfaces.PDFDownloadComplete
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.mToast
import orionedutech.`in`.lmstrainerapp.mToast.showToast
import orionedutech.`in`.lmstrainerapp.network.NetworkOps
import orionedutech.`in`.lmstrainerapp.network.Urls
import orionedutech.`in`.lmstrainerapp.network.downloader.MActions
import orionedutech.`in`.lmstrainerapp.network.downloader.MDownloader
import orionedutech.`in`.lmstrainerapp.network.downloader.MDownloaderService
import orionedutech.`in`.lmstrainerapp.network.response
import java.io.*


/**
 * A simple [Fragment] subclass.
 */

class ManualFragment : BaseFragment(), PDFDownloadComplete.complete {

    lateinit var pdfView: PDFView
    lateinit var animation: LottieAnimationView
    lateinit var waitText: TextView
    var serverURL = ""
    var path = ""
    var currentPage : Int = 0
    lateinit var pdfPref : SharedPreferences
    lateinit var refresh : SwipeRefreshLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        activity!!.window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        val view = inflater.inflate(R.layout.fragment_manual, container, false)
        view.back.setOnClickListener {
            activity!!.onBackPressed()
        }
        pdfView = view.pdfView
        animation = view.animation1
        waitText = view.wait
        refresh = view.swipe
        view.export.setOnClickListener {
            MaterialAlertDialogBuilder(context).setTitle("Alert")
                .setMessage("Do you want to export the file to storage?")
                .setPositiveButton("export"){dialogInterface, i ->
                    dialogInterface.dismiss()
                    CoroutineScope(IO).launch {
                        copyToExternalStorage(path)
                    }
                }.setNegativeButton("cancel"){dialogInterface, i ->
                dialogInterface.dismiss()
                    }.create().show()

        }

        pdfPref = activity?.getSharedPreferences("pdf",Context.MODE_PRIVATE)!!

        if(pdfPref.getString("lastURL","")==""){
            getData()
        }else{
            CoroutineScope(IO).launch {
                context?.let {
                    val dao = MDatabase(it).getFilesDao()
                    path = dao.getInternalPath(pdfPref.getString("lastURL","")!!)
                    if(path.isEmpty()){
                        getData()
                    }else{
                        withContext(Main){
                            showPDF(path)
                        }
                    }

               }


            }
        }
        refresh.setOnRefreshListener {
            refresh.isRefreshing = false
            getData()
        }
        PDFDownloadComplete.instance.setListener(this)

        return view
    }

    private fun getData() {
        waitText.text = "checking file..."
        waitText.visibility = VISIBLE
        pdfView.visibility = GONE
        val json = "{\"user_type\":\"3\"}"
        NetworkOps.post(Urls.manualUrl, json, context, object : response {
            override fun onrespose(string: String?) {
                val jsonval = JSONObject(string!!)
                if (jsonval.getString("success") == "1") {
                     serverURL = jsonval.getString("response")
                    checkValue(serverURL)
                } else {
                    onFailure()
                }
            }

            override fun onfailure() {
                onFailure()
            }

            override fun onInternetfailure() {
                onFailure()
                mToast.noInternetSnackBar(activity!!)
            }

        }) { _, _, _ ->

        }
    }

    private fun checkValue(serverURL: String) {
        launch {
            context?.let {
                val dao = MDatabase(it).getFilesDao()
                val hasFile = dao.urlExists(serverURL)
                mLog.i(TAG, "hasFile $hasFile")
                if (hasFile) {
                    //show pdf
                    if(activity==null){
                        return@let
                    }
                    activity!!.runOnUiThread {
                        mToast.showToast(context,"pdf is up to date")
                    }
                    mLog.i(TAG, "showing pdf")
                     pdfPref.edit().putString("lastURL",serverURL).apply()
                     path = dao.getInternalPath(serverURL)
                     showPDF(path)

                } else {
                    //start download
                    activity?.runOnUiThread {
                        waitText.visibility = VISIBLE
                        pdfView.visibility = GONE
                        waitText.text = "please wait while the pdf is being downloaded..."
                        mToast.showToast(context, "download start")
                        animation.visibility = VISIBLE
                        animation.playAnimation()
                    }

                    val intent = Intent(context, MDownloaderService::class.java)
                    intent.putExtra("url", serverURL)
                    intent.putExtra("name", "manual.pdf")
                    intent.putExtra("pdf", true)
                    intent.action = MActions.start
                    context!!.startService(intent)

                }
            }
        }
    }

    private fun showPDF(path: String) {
        pdfView.visibility = VISIBLE
        pdfView.fromFile(File(path))
            .scrollHandle(DefaultScrollHandle(context))
            .defaultPage(pdfPref.getInt(path,0)).onError{
                mToast.showToast(context,"corrupted pdf")
                MaterialAlertDialogBuilder(context).setTitle("Alert")
                    .setMessage("The downloaded pdf is missing or corrupted. Would you like to download again?")
                    .setPositiveButton("download"){dialogInterface, i ->
                        dialogInterface.dismiss()
                    directDownload()
                    }.setNegativeButton("cancel"){dialogInterface, i ->
                        dialogInterface.dismiss()
                    }.create().show()

            }.onPageChange { page, _ ->
                currentPage = page
            }
            .load().let {
                activity?.runOnUiThread {
                    waitText.visibility = GONE
                    animation.cancelAnimation()
                    animation.visibility = GONE
                }
            }

    }

    private fun directDownload() {
        waitText.text = "checking file..."
        waitText.visibility = VISIBLE
        pdfView.visibility = GONE
        val json = "{\"user_type\":\"3\"}"
        NetworkOps.post(Urls.manualUrl, json, context, object : response {
            override fun onrespose(string: String?) {
                val jsonval = JSONObject(string!!)
                if (jsonval.getString("success") == "1") {
                    serverURL = jsonval.getString("response")
                    activity?.runOnUiThread {
                        waitText.visibility = VISIBLE
                        pdfView.visibility = GONE
                        waitText.text = "please wait while the pdf is being downloaded..."
                        mToast.showToast(context, "download start")
                        animation.visibility = VISIBLE
                        animation.playAnimation()
                    }

                    val intent = Intent(context, MDownloaderService::class.java)
                    intent.putExtra("url", serverURL)
                    intent.putExtra("name", "manual.pdf")
                    intent.putExtra("pdf", true)
                    intent.action = MActions.start
                    context!!.startService(intent)
                } else {
                    onFailure()
                }
            }

            override fun onfailure() {
                onFailure()
            }

            override fun onInternetfailure() {
                onFailure()
                mToast.noInternetSnackBar(activity!!)
            }

        }) { _, _, _ ->

        }
    }

    private fun onFailure() {
        activity?.runOnUiThread {
            mToast.showToast(context, "failed")
        }
    }

    override fun refreshData() {
        mLog.i(TAG, "refresh called")
       if( this.isVisible){
         checkValue(serverURL)
       }
    }
    private suspend fun copyToExternalStorage(path:String) {
        val sourceFile = File(path)
        if(sourceFile.exists()){
            val destinationFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/"+sourceFile.name)
            mLog.i(TAG,"destination path : ${destinationFile.absolutePath}")
            destinationFile.parentFile!!.mkdirs()
            destinationFile.createNewFile()
            val inp = FileInputStream(sourceFile)
            val out = FileOutputStream(destinationFile)
            withContext(Main){
               showToast(context,"the pdf is being copied")
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
                showToast(context,"copy complete")
                MaterialAlertDialogBuilder(context).setTitle("Alert")
                    .setMessage("The file has been copied to ${destinationFile.absolutePath}")
                    .setPositiveButton("dismiss"){dialogInterface, i ->
                        dialogInterface.dismiss()

                    }/*.setNegativeButton("view"){dialogInterface, i ->
                        dialogInterface.dismiss()
                        val packageManager = activity!!.packageManager
                        val testintent = Intent(Intent.ACTION_VIEW)
                        testintent.type = "application/pdf"
                        val list  = packageManager.queryIntentActivities(testintent,PackageManager.MATCH_DEFAULT_ONLY)
                        if(list.size>0){
                            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                                val file  = File(destinationFile.path)
                                val selectedUri: Uri = FileProvider.getUriForFile(context!!,"orionedutech.in.lmstrainerapp.fileprovider",file)
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.setDataAndType(selectedUri, "application/pdf")
                                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                startActivity(intent)
                            }else{
                                val selectedUri: Uri =
                                    Uri.parse(destinationFile.absolutePath)
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.setDataAndType(selectedUri, "application/pdf")
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                        }else{
                            showToast(context,"There is no pdf reader available")
                        }


                    }*/.create().show()
            }


            mLog.i(TAG,"copy complete")

        }else{
            mLog.i(TAG,"file does not exist")
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

    override fun onStop() {
        if(path!= ""){
           pdfPref.edit().putInt(path,currentPage).apply()
        }
        activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        super.onStop()
    }
}

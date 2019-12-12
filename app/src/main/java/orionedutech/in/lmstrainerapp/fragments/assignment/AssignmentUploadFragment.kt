package orionedutech.`in`.lmstrainerapp.fragments.assignment


import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import com.developer.filepicker.model.DialogConfigs
import com.developer.filepicker.model.DialogProperties
import com.developer.filepicker.view.FilePickerDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_assignment_upload.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import orionedutech.`in`.lmstrainerapp.PathUtil
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.adapters.spinners.BatchSpinAdapter
import orionedutech.`in`.lmstrainerapp.adapters.spinners.CourseSpinAdapter
import orionedutech.`in`.lmstrainerapp.database.MDatabase
import orionedutech.`in`.lmstrainerapp.database.entities.Batch
import orionedutech.`in`.lmstrainerapp.fragments.BaseFragment
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.mToast.noInternetSnackBar
import orionedutech.`in`.lmstrainerapp.mToast.showToast
import orionedutech.`in`.lmstrainerapp.network.NetworkOps
import orionedutech.`in`.lmstrainerapp.network.Urls
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCCourse
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCCourseList
import orionedutech.`in`.lmstrainerapp.network.response
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.roundToInt

/**
 * A simple [Fragment] subclass.
 */
class AssignmentUploadFragment : BaseFragment() {
    lateinit var fileNameTV: TextView

    private val PICKFILE_REQUEST_CODE = 11
    val PDF = "application/pdf"
    val DOC = "application/msword"
    val DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    var filepath = ""

    var courseListSpinner = ArrayList<DCCourse>()
    lateinit var courseAdapter: CourseSpinAdapter
    lateinit var courseSpinner: Spinner


    var batchListSpinner = ArrayList<Batch>()
    lateinit var batchAdapter: BatchSpinAdapter
    lateinit var batchSpinner: Spinner

    lateinit var selectedBatchID: String
    lateinit var selectedCourseID: String

    lateinit var nameET: EditText
    lateinit var descET: EditText

    var userID: String = ""
    var userProfileID: String = ""
    var userAdminID: String = ""
    var assignmentName: String = ""
    var assignmentDesc: String = ""

    lateinit var progress: ProgressBar
    lateinit var progressText: TextView
    lateinit var browseButton: AppCompatButton
    lateinit var uploadButon: AppCompatImageButton
    val FILE_REQUEST_CODE = 121
    var views: ArrayList<View> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_assignment_upload, container, false)
        courseSpinner = view.course
        fileNameTV = view.fileName
        nameET = view.name
        descET = view.description
        progress = view.prog
        progressText = view.progressText
        batchSpinner = view.batch
        browseButton = view.browse
        uploadButon = view.upload
        views = arrayListOf(nameET, descET, batchSpinner, courseSpinner, browseButton, uploadButon)

        courseAdapter = CourseSpinAdapter(
            context!!,
            android.R.layout.simple_list_item_1,
            courseListSpinner
        )

        courseSpinner.adapter = courseAdapter



        batchAdapter = BatchSpinAdapter(
            context!!,
            android.R.layout.simple_list_item_1,
            batchListSpinner
            , 0
        )
        batchSpinner.adapter = batchAdapter


        batchSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val batch = batchListSpinner[p2]
                selectedBatchID = batch.batch_id.toString()
                CoroutineScope(IO).launch {
                    getCourseData(selectedBatchID)
                }
            }

        }
        courseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val course = courseListSpinner[p2]
                selectedCourseID = course.course_id
            }

        }
        nameET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                assignmentName = p0.toString()
            }

        })

        descET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                assignmentDesc = p0.toString()
            }

        })


        CoroutineScope(IO).launch {
            context?.let {
                val dao = MDatabase(it).getBatchDao()
                val userDao = MDatabase(it).getUserDao()
                userID = userDao.getUserID()
                userProfileID = userDao.getProfileID()
                userAdminID = userDao.getAdminID()
                if (dao.batchDataExists()) {
                    batchListSpinner.addAll(dao.getAllBatches())
                    withContext(Dispatchers.Main) {
                        batchAdapter.notifyDataSetChanged()
                    }
                }

            }

        }




        browseButton.setOnClickListener {
            //browse code

            val properties = DialogProperties()
            properties.selection_mode = DialogConfigs.SINGLE_MODE
            properties.selection_type = DialogConfigs.FILE_SELECT
            properties.root =    File(DialogConfigs.DEFAULT_DIR)
            properties.error_dir =  File(DialogConfigs.DEFAULT_DIR)
            properties.offset =  File(DialogConfigs.DEFAULT_DIR)
            properties.extensions = arrayOf("pdf")
            properties.show_hidden_files = false
                val dialogue = FilePickerDialog(activity!!,properties,R.style.mAlertDialogTheme)
            dialogue.setTitle("Choose a pdf")
            dialogue.setDialogSelectionListener {
                mLog.i(TAG,"path ${it[0]}")
                filepath = it[0]
                fileNameTV.text = filepath.substring(filepath.lastIndexOf("/")+1)
            }
        dialogue.show()
        }


        uploadButon.setOnClickListener {
            //upload code
            if (assignmentName == "" || assignmentDesc == "") {
                showToast(context, "please fill both fields")
                return@setOnClickListener
            }
            if (filepath == "") {
                showToast(context, "please choose a file")
                return@setOnClickListener
            }

            val json = JSONObject()
            json.put("user_id", userID)
            json.put("userprofileid", userProfileID)
            json.put("user_admin_id", userAdminID)
            json.put("assignment_course", selectedCourseID)
            json.put("assignment_batch", selectedBatchID)
            json.put("assignment_name", assignmentName)
            json.put("assignment_desc", assignmentDesc)

            val hashmap: HashMap<String, String> = HashMap()
            hashmap["data_json"] = json.toString()
            hashmap["file_path"] = filepath
            if (activity == null) {
                return@setOnClickListener
            }
            activity!!.runOnUiThread {
                progress.visibility = VISIBLE
                progress.max = 100
                progressText.visibility = VISIBLE
                disableViews()
            }
            showAnimation()
            NetworkOps.postMultipart(Urls.assignmentUploadUrl, hashmap, context, object : response {
                override fun onrespose(string: String?) {

                    mLog.i(TAG, "response $string ")
                    if (JSONObject(string!!).optString("success") == "1") {
                        if (activity == null) {
                            return
                        }
                        activity!!.runOnUiThread {
                            hideAnimations()
                            MaterialAlertDialogBuilder(context).setTitle("Alert")
                                .setMessage("Assessment Uploaded Successfully")
                                .setCancelable(false)
                                .setPositiveButton("ok"){
                                    dialogInterface, i ->
                                    dialogInterface.dismiss()
                                    activity!!.onBackPressed()
                                }.create().show()

                        }
                    }
                }

                override fun onfailure() {
                    mLog.i(TAG, "failed")
                    if (activity == null) {
                        return
                    }
                    activity!!.runOnUiThread {
                        showToast(context, "failed to submit")
                        enableViews()
                    }
                }

                override fun onInternetfailure() {
                    if (activity == null) {
                        return
                    }
                    activity!!.runOnUiThread {
                        enableViews()
                        noInternetSnackBar(activity!!)
                    }
                }

            }) { pg, speed, secs ->
                mLog.i(TAG, "progress $progress speed $speed secs $secs ")
                if (activity == null) {
                    return@postMultipart
                }
                activity!!.runOnUiThread {
                    progress.isIndeterminate = false
                    progress.progress = pg.roundToInt()
                    val string = String.format(
                        "Progress: %s%% Time and Speed: %s / %s ",
                        pg,
                        getSpeed(speed),
                        getSecs(secs)
                    )
                    progressText.text = string
                }
            }

        }

        return view
    }

    fun getCustomFileChooserIntent(vararg types: String): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        // Filter to only show results that can be "opened"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, types)
        return intent
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICKFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val uri = data!!.data
            val uriString = uri!!.toString()
            val myFile = File(uriString)

            var displayName: String? = null

            if (uriString.startsWith("content://")) {
                var cursor: Cursor? = null
                try {
                    cursor = activity!!.contentResolver.query(uri, null, null, null, null)
                    if (cursor != null && cursor.moveToFirst()) {
                        displayName =
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                } finally {
                    cursor!!.close()
                }
            } else if (uriString.startsWith("file://")) {
                displayName = myFile.name
            }
            filepath = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                val split = File(uri.path!!).path.split(":")//split the path.
                split[1]
            } else {
                PathUtil.getPath(context, uri)
            }
            mLog.i(TAG, "file path : $filepath")
            mLog.i(TAG, "file name: " + displayName!!)
            if (!TextUtils.isEmpty(filepath) && !TextUtils.isEmpty(displayName)) {
                fileNameTV.text = String.format("File name : %s", displayName)
            } else {
                showToast(context, "error in choosing file")
            }

        }
    }

    suspend fun getCourseData(batchId: String?) {
        showAnimation()
        val json = JSONObject()
        json.put("batch_id", batchId)
        launch {
            context?.let {
                NetworkOps.post(
                    Urls.courseUrl,
                    json.toString(),
                    context,
                    object : response {
                        override fun onInternetfailure() {
                            noInternetSnackBar(activity!!)
                        }

                        override fun onrespose(string: String?) {
                            val courses = Gson().fromJson(string, DCCourseList::class.java)
                            if (courses.success == "1") {

                                val courseList = courses.courses
                                if (courseList.isNotEmpty()) {
                                    courseListSpinner.clear()
                                    courseListSpinner.addAll(courseList)
                                    CoroutineScope(Dispatchers.Main).launch {
                                        courseAdapter.notifyDataSetChanged()
                                        hideAnimations()
                                    }
                                } else {
                                    val dao1 = MDatabase(it)
                                        .getBatchDao()
                                    CoroutineScope(IO).launch {
                                        showToast(
                                            context,
                                            "There is no course for ${dao1.getBatchName(
                                                batchId.toString()
                                            )}"
                                        )
                                        hideAnimations()
                                    }


                                }
                            } else {
                                failedCourse()
                            }
                        }

                        override fun onfailure() {
                            failedCourse()
                        }

                    }) { _, _, _ -> }
            }
        }
    }

    private fun failedCourse() {
        if (activity == null) {
            return
        }
        activity!!.runOnUiThread {
            showToast(context, "failed")
        }
    }

    private fun hideAnimations() {
        if (activity == null) {
            return
        }
        activity!!.runOnUiThread {
            progress.isIndeterminate = false
            progress.visibility = GONE
            progressText.visibility = GONE
            progressText.text = "Please wait"
        }
    }

    private fun showAnimation() {
        if (activity == null) {
            return
        }
        activity!!.runOnUiThread {
            progress.isIndeterminate = true
            progress.visibility = VISIBLE
            progressText.visibility = VISIBLE
            progressText.text = "Please wait"
        }
    }

    private fun getSecs(secs: Long): String {
        val seconds = TimeUnit.SECONDS.convert(secs, TimeUnit.NANOSECONDS).toInt()
        if (seconds < 60) {
            val s: String
            if (seconds > 0) {

                s = "$seconds secs "

            } else if (seconds == 0) {

                s = "finishing up..."

            } else {
                s = "unknown"
            }
            return s

        } else {
            val min = seconds / 60
            val mins: String
            if (min == 1) {
                mins = "$min min "
            } else {
                mins = "$min mins "
            }
            return mins
        }

    }

    private fun getSpeed(speed: Float): String {
        return if (speed < 1024) {
            if (speed < 200) {

                String.format(Locale.getDefault(), "%.2f kbps", speed)
            } else {

                String.format(Locale.getDefault(), "%.2f kbps", speed)
            }
        } else {
            String.format(Locale.getDefault(), "%.2f Mbps", speed / 1024)
        }
    }

    private fun enableViews() {
        views.forEach {
            it.isEnabled = true
        }
    }

    private fun disableViews() {
        views.forEach {
            it.isEnabled = false
        }
    }
}

package orionedutech.`in`.lmstrainerapp.fragments.assignment


import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_assignment_upload.view.*
import orionedutech.`in`.lmstrainerapp.FileUtils
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.mToast
import java.io.File

/**
 * A simple [Fragment] subclass.
 */
class AssignmentUploadFragment : Fragment() {
    lateinit var fileNameTV : TextView

    private val PICKFILE_REQUEST_CODE = 11
    val PDF = "application/pdf"
    val DOC = "application/msword"
    val DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    var filepath = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_assignment_upload, container, false)

        view.browse.setOnClickListener {
            //browse code
            val intent = getCustomFileChooserIntent(DOC, PDF, DOCX)
            startActivityForResult(intent, PICKFILE_REQUEST_CODE)
        }
        view.upload.setOnClickListener {
            //upload code

        }
        fileNameTV = view.fileName

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
                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                } finally {
                    cursor!!.close()
                }
            } else if (uriString.startsWith("file://")) {
                displayName = myFile.name
            }
            filepath = FileUtils.getPath(context!!, uri)!!
            mLog.i(TAG, "file path : $filepath")
            mLog.i(TAG, "file name: " + displayName!!)
            if (!TextUtils.isEmpty(filepath) && !TextUtils.isEmpty(displayName)) {
                fileNameTV.setText(displayName)
            } else {
                mToast.showToast(context, "error in choosing file")
            }

        }
    }

}

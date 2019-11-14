package orionedutech.`in`.lmstrainerapp.fragments


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import kotlinx.android.synthetic.main.fragment_pdf.view.*
import orionedutech.`in`.lmstrainerapp.R
import java.io.File


/**
 * A simple [Fragment] subclass.
 */
class PDFFragment : Fragment() {
    lateinit var name : TextView
    lateinit var pdfView : PDFView
    var currentPage : Int = 0
    var path  = ""
    lateinit var pdfPref : SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_pdf, container, false)
        name = view.pdfName
        pdfView = view.pdfView
        pdfPref = activity!!.getSharedPreferences("pdf",Context.MODE_PRIVATE)
         path = arguments!!.getString("path")!!
        val naam  = arguments!!.getString("name")

        name.text = naam
        pdfView.fromFile(File(path))
            .scrollHandle(DefaultScrollHandle(context))
            .defaultPage(pdfPref.getInt(path,0)).onPageChange{
            page, pageCount ->
        currentPage = page

        }.load()

        return view
    }

    override fun onStop() {
        pdfPref.edit().putInt(path,currentPage).apply()
        super.onStop()
    }
}

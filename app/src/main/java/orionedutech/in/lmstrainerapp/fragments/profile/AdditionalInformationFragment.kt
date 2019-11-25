package orionedutech.`in`.lmstrainerapp.fragments.profile


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_additional_information.view.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.database.MDatabase
import orionedutech.`in`.lmstrainerapp.fragments.BaseFragment


/**
 * A simple [Fragment] subclass.
 */
class AdditionalInformationFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_additional_information, container, false)

        launch {
            context?.let {
                val dao = MDatabase(it).getUserDao()
                val pan = dao.getPan()
                val aadhar = dao.getAadhar()
                val doj = dao.getDoj()
                val qualification = dao.getLQualification()
                val experience = dao.getWorkExperience()
                view.pan.setText(pan)
                view.aadhar.setText(aadhar)
                view.doj.setText(doj)
                view.qualification.setText(qualification)
                view.experience.setText(String.format(" %s year(s)",experience))

            }
        }
        return view
    }


}

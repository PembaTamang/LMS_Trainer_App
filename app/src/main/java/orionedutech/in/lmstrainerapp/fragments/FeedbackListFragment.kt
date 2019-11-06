package orionedutech.`in`.lmstrainerapp.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import orionedutech.`in`.lmstrainerapp.R

/**
 * A simple [Fragment] subclass.
 */
class FeedbackListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feedback_list, container, false)
    }


}

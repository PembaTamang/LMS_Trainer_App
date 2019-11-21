package orionedutech.`in`.lmstrainerapp.fragments.profile


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.mikhaellopez.circularimageview.CircularImageView
import kotlinx.android.synthetic.main.fragment_parent.view.*
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.fragments.BaseFragment

/**
 * A simple [Fragment] subclass.
 */
class ParentFragment : BaseFragment() {
    private var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_parent, container, false)
        tabLayout = view.tabLayout
        viewPager = view.viewPager

        setUpViewPager()

        view.camera.setOnClickListener {
            //start camera intent

        }
        return view
        }
    private fun setUpViewPager() {
        val adapter = ViewPagerAdapter(childFragmentManager)
        viewPager!!.adapter = adapter
        tabLayout!!.setupWithViewPager(viewPager)
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

}

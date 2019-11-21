package orionedutech.`in`.lmstrainerapp.fragments.profile


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_profile_information.view.*
import kotlinx.coroutines.launch
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.database.MDatabase
import orionedutech.`in`.lmstrainerapp.fragments.BaseFragment


/**
 * A simple [Fragment] subclass.
 */
class ProfileInformationFragment : BaseFragment() {
    lateinit var nameET : TextInputEditText
    lateinit var phoneET : TextInputEditText
    lateinit var emailET : TextInputEditText
    lateinit var centerET : TextInputEditText
    lateinit var submit : MaterialButton
    lateinit var editButton : TextView
    var arraylist : ArrayList<TextInputEditText> = ArrayList()
    var editing  = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_profile_information, container, false)

        nameET = view.name
        phoneET = view.phone
        emailET = view.email
        centerET = view.centerName
        submit = view.updateProfile
        arraylist.addAll(arrayListOf(nameET,phoneET,emailET,centerET))
        disableETs()

        editButton = view.edit
        editButton.setOnClickListener {
            if(!editing) {
                editButton.setTextColor(ContextCompat.getColor(context!!,R.color.blue))
                submit.isEnabled = true
                editing = true
                enableETs()
            }else{
                editButton.setTextColor(ContextCompat.getColor(context!!,R.color.default_text))
                editing = false
                submit.isEnabled = false
                disableETs()
            }
            }

         submit.setOnClickListener {
             //submit code here
         }
            launch {
                context?.let {
                    val dao = MDatabase(it).getUserDao()
                    val name = dao.getadminName()
                    val phone = dao.getPhone()
                    val email = dao.getEmail()
                    val center  = dao.getCenterName()

                    nameET.setText(name)
                    phoneET.setText(phone)
                    emailET.setText(email)
                    centerET.setText(center)

                }
            }

        return view
    }

    private fun disableETs() {
        arraylist.forEach {
            it.isEnabled = false
        }
    }

    private fun enableETs() {
        arraylist.forEach {
            it.isEnabled = true
        }
    }

}

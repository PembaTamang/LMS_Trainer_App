package orionedutech.`in`.lmstrainerapp.fragments.batch


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_batch_creation.view.*
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.mToast.showToast

/**
 * A simple [Fragment] subclass.
 */
class BatchCreationFragment : Fragment() {
    lateinit var active: TextView
    lateinit var button : MaterialButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_batch_creation, container, false)
        active = view.active

        view.back.setOnClickListener {
            activity!!.onBackPressed()
        }

        view.mSwitch.setOnCheckedChangeListener { _, b ->
            if (b) {
                active.text = "active"
                active.setTextColor(ContextCompat.getColor(context!!, R.color.green))
            } else {
                active.text = "inactive"
                active.setTextColor(ContextCompat.getColor(context!!, R.color.orange))
            }

        }

        val array1 : ArrayList<String> = ArrayList() // static value say 10
        var array2 : ArrayList<String> = ArrayList() // may vary
        var array1index = 0
        var array2index = 0
        array2 = getByArray1Value(array1[array1index])
        button.setOnClickListener {
            if(array1index<array1.size){
                mLog.i(TAG,"number of questions : ${array2.size}")
                if(array2index<array2.size){
                    mLog.i(TAG,"1 activity index $array1index question index $array2index ")
                    array2index+=1
                }else{
                    MaterialAlertDialogBuilder(context).setTitle("Questions over")
                        .setMessage("ok thicha")
                        .setPositiveButton("ok"){
                                dialogInterface, i ->
                            dialogInterface.dismiss()
                            array2index =0
                            array1index+=1
                            if(array1index<array1.size){
                                array2 = getByArray1Value(array1[array1index])
                            }
                            button.callOnClick()
                        }.create().show()
                }

            }else{

                over()
            }

        }











        return view
    }

    private fun over() {
        showToast(context,"over")
    }

    private fun getByArray1Value(s: String): ArrayList<String> {
    return arrayListOf()
    }


}

package orionedutech.`in`.lmstrainerapp.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_feedback.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.adapters.spinners.BatchSpinAdapter
import orionedutech.`in`.lmstrainerapp.database.dao.MDatabase
import orionedutech.`in`.lmstrainerapp.database.entities.Batch
import java.util.ArrayList
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.network.NetworkOps
import orionedutech.`in`.lmstrainerapp.network.Urls
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCBatches
import orionedutech.`in`.lmstrainerapp.network.response

/**
 * A simple [Fragment] subclass.
 */
class FeedbackFragment : BaseFragment() {

    var courseListSpinner = ArrayList<String>()
    lateinit var courseAdapter: ArrayAdapter<String>

    var batchListSpinner = ArrayList<Batch>()
    lateinit var batchAdapterBatch: BatchSpinAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_feedback, container, false)

        courseAdapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, courseListSpinner)
        view.course_spinner.adapter = courseAdapter

        batchAdapterBatch = BatchSpinAdapter(
            context!!,
            android.R.layout.simple_list_item_1,
            batchListSpinner
        )
        view.batch_spinner.adapter = batchAdapterBatch

         view.batch_spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(p0: AdapterView<*>?) {

        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val batch = batchListSpinner[p2]
         mLog.i(TAG,"batch id : ${batch.batch_id}")
        }

    }


        /* for (i in 0..19) {
             courseListSpinner.add("Course : $i")
         }
         courseAdapter.notifyDataSetChanged()*/
        CoroutineScope(IO).launch {
            getBatchData()

            getCourseData()
        }

        view.feedback.setOnClickListener {
            val course = view.course_spinner.selectedItem.toString()


        }
        return view
    }

    suspend fun getCourseData() {

    }

    suspend fun getBatchData() {

        val json = JSONObject()
        launch {
            context?.let {
                val dao = MDatabase(it).getDao()
                val trainerID = dao.getTrainerID()
                mLog.i(TAG,"admin id $trainerID")
                json.put("trainer_id",trainerID)
                NetworkOps.post(Urls.batchUrl,json.toString(),it,view!!.feedback,object : response{
                    override fun onrespose(string: String?) {
                        mLog.i(TAG,"response $string")
                      val batches  = Gson().fromJson(string,DCBatches::class.java)
                        if(batches.success=="1"){
                        val batchlist = batches.batches
                        mLog.i(TAG,"list length ${batchlist.size}")
                        val dao1 = MDatabase(it).getDao1()
                        CoroutineScope(IO).launch {
                            dao1.insertBatches(batchlist.toMutableList())
                            mLog.i(TAG,"inserted")
                            batchListSpinner.addAll(batchlist)
                            withContext(Main){
                                batchAdapterBatch.notifyDataSetChanged()
                            }
                        }
                        }else{
                            mLog.i(TAG,"error")
                        }
                    }

                    override fun onfailure() {

                    }



                }) { progress, speed, secs ->

                }
            }
        }
    }

}

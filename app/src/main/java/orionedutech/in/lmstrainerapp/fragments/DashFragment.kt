package orionedutech.`in`.lmstrainerapp.fragments


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_dash.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.adapters.spinners.BatchSpinAdapter
import orionedutech.`in`.lmstrainerapp.database.MDatabase
import orionedutech.`in`.lmstrainerapp.database.entities.Batch
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.mToast
import orionedutech.`in`.lmstrainerapp.network.NetworkOps
import orionedutech.`in`.lmstrainerapp.network.Urls
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCBatches
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCDash
import orionedutech.`in`.lmstrainerapp.network.progress
import orionedutech.`in`.lmstrainerapp.network.response
import java.util.concurrent.CountDownLatch


class DashFragment : BaseFragment() {
    //views
    private lateinit var chart: BarChart

    private lateinit var batchSpinner: Spinner
    private lateinit var animation: LottieAnimationView
    private lateinit var courseCount: TextView
    private lateinit var batchCount: TextView
    private lateinit var studentCount: TextView
    private lateinit var name : TextView
    private lateinit var dashPref : SharedPreferences
    private lateinit var batchName : TextView
    //variables
    var batchListSpinner = java.util.ArrayList<Batch>()
    lateinit var batchAdapter: BatchSpinAdapter
    lateinit var selectedBatchID: String
    private var busy: Boolean = false
    var json: JSONObject = JSONObject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dash, container, false)
        chart = view.bar

        batchSpinner = view.dashSpinner
        animation = view.animation1
        courseCount = view.courseCount
        batchCount = view.batchCount
        studentCount = view.studentCount
        name = view.name
        batchName = view.batchName
        dashPref = activity!!.getSharedPreferences("dash",Context.MODE_PRIVATE)
        batchAdapter = BatchSpinAdapter(
            context!!,
            android.R.layout.simple_list_item_1,
            batchListSpinner
            , 1
        )

        batchSpinner.adapter = batchAdapter

        batchSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                val batch = batchListSpinner[p2]
                selectedBatchID = batch.batch_id.toString()

            }

        }

        setUpChart()
        mLog.i(TAG,"pref value : ${dashPref.getBoolean("data",false)}")
        if(!dashPref.getBoolean("data",false)){
            mLog.i(TAG,"getting data")
            getDashboardData()
        }else{
           courseCount.text = if( dashPref.getInt("course",0)>1) String.format(" %d COURSES", dashPref.getInt("course",0)) else String.format(" %d COURSE", dashPref.getInt("course",0))
           batchCount.text =  if(dashPref.getInt("batch",0)>1) String.format("%d BATCHES",dashPref.getInt("batch",0)) else String.format("%d BATCH",dashPref.getInt("batch",0))
           studentCount.text = if(dashPref.getInt("student",0)>1) String.format("%d STUDENTS", dashPref.getInt("student",0)) else String.format("%d STUDENT", dashPref.getInt("student",0))
            name.text = dashPref.getString("name","error")
            String.format("%02d", 10)
            CoroutineScope(IO).launch {
                context?.let {
                    val dao = MDatabase(it).getBatchDao()
                    if (dao.batchDataExists()) {
                        batchListSpinner.addAll(dao.getAllBatches())
                        withContext(Main) {
                            batchAdapter.notifyDataSetChanged()
                        }
                    }
                }

            }
        }

        return view
    }

    private fun getDashboardData() {
        courseCount.text = getString(R.string.loading)
        batchCount.text = getString(R.string.loading)
        studentCount.text = getString(R.string.loading)

        launch {
            context?.let {
                val dao = MDatabase(it).getUserDao()
                val userID = dao.getUserID()
                val naam = dao.getadminName()
                if(activity==null){
                    return@let
                }
                activity!!.runOnUiThread {
                    name.text = naam
                }
                val json = JSONObject()
                json.put("user_id", userID)

                NetworkOps.post(Urls.dashboardUrl, json.toString(), context, object : response {
                    override fun onrespose(string: String?) {
                        mLog.i(TAG,"dash resp : $string")
                        val dash = Gson().fromJson(string, DCDash::class.java)
                        if (dash != null) {
                            mLog.i(TAG,"success")
                            if(activity==null){
                                return
                            }
                            activity!!.runOnUiThread {
                                courseCount.text = if(dash.total_courses>1) String.format(" %d COURSES", dash.total_courses) else String.format(" %d COURSE", dash.total_courses)
                                batchCount.text = if(dash.total_batches>1) String.format("%d BATCHES",dash.total_batches) else String.format("%d BATCH",dash.total_batches)
                                studentCount.text = if(dash.total_students>1) String.format("%d STUDENTS",dash.total_students) else String.format("%d STUDENT",dash.total_students)
                                dashPref.edit().putInt("student",dash.total_students)
                                    .putInt("course",dash.total_courses)
                                    .putInt("batch",dash.total_batches)
                                    .putString("name",naam)
                                    .putBoolean("data",true).apply()
                            }
                            CoroutineScope(IO).launch {
                                context?.let {

                                    showAnimation()
                                    hideAnimations(getBatchData())
                                }
                            }


                        } else {
                            if(activity==null){
                                return
                            }
                            activity!!.runOnUiThread {
                                mToast.showToast(context, "failed")
                            }

                        }
                    }

                    override fun onfailure() {
                        if(activity==null){
                            return
                        }
                        activity!!.runOnUiThread {
                            mToast.showToast(context, "failed")
                        }
                        Handler().postDelayed({
                            if (!activity!!.isFinishing) {
                                getDashboardData()
                            }
                        }, 3000)
                    }

                    override fun onInternetfailure() {
                        mToast.noInternetSnackBar(activity!!)
                    }

                }) { _, _, _ ->
                }
            }
        }
    }


    private fun setUpChart() {
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()
        for (i in 0..10) {
            entries.add(BarEntry(i * 10f, i))
            labels.add("label " + i * 10)
        }


        mLog.i(TAG, "label count ${labels.size} entries size ${entries.size}")

        val bardataset = BarDataSet(entries, "label")
        val bardata = BarData(labels, bardataset)

        chart.setDescription("")
        chart.legend.isEnabled = false
        val xAxis = chart.xAxis
        xAxis.textSize = 10f
        xAxis.spaceBetweenLabels = 0
        xAxis.labelRotationAngle = 45f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        chart.canScrollHorizontally(1)
        chart.isScaleXEnabled = true
        chart.isClickable = false
        chart.data = bardata
        chart.invalidate()
    }

    private fun showAnimation() {
        busy = true
        mLog.i(TAG, "showing")
        activity?.runOnUiThread {
            animation.visibility = View.VISIBLE
            animation.playAnimation()
        }

    }

    private fun hideAnimations(b: Boolean) {
        busy = false
        mLog.i(TAG, "hiding : $b ")
        activity?.runOnUiThread {
            animation.visibility = View.GONE
            animation.cancelAnimation()
        }
    }


    suspend fun getBatchData(): Boolean {
        var a = false
        val countDownLatch = CountDownLatch(1)
        //  showAnimation()
        launch {
            context?.let {
                val dao = MDatabase(it).getUserDao()
                val trainerID = dao.getUserID()
                mLog.i(TAG, "trainer id $trainerID")
                json.put("trainer_id", trainerID)
                NetworkOps.post(
                    Urls.batchUrl,
                    json.toString(),
                    context,
                    object : response {
                        override fun onInternetfailure() {
                            mToast.noInternetSnackBar(activity!!)
                        }

                        override fun onrespose(string: String?) {

                            val batches = Gson().fromJson(string, DCBatches::class.java)
                            if (batches.success == "1") {
                                val batchlist = batches.batches
                                if (batchlist.isNotEmpty()) {
                                    mLog.i(TAG, "list length ${batchlist.size}")
                                    CoroutineScope(IO).launch{
                                        val dao1 = MDatabase(
                                            it
                                        ).getBatchDao()
                                        mLog.i(TAG,"inserting")
                                        dao1.insertBatches(batchlist.toMutableList())
                                    }
                                    batchListSpinner.clear()
                                    batchListSpinner.addAll(batchlist)
                                    activity?.runOnUiThread {
                                        batchAdapter.notifyDataSetChanged()
                                        //  hideAnimations()
                                    }

                                } else {
                                    mToast.showToast(context, "Error no batches found")

                                    //  hideAnimations()
                                }

                                a = true
                                countDownLatch.countDown()
                            } else {
                                countDownLatch.countDown()

                                // failedBatch()
                            }
                        }

                        override fun onfailure() {
                            countDownLatch.countDown()
                            // failedBatch()


                        }


                    }) { _, _, _ ->

                }

            }
        }

        countDownLatch.await()
        return a
    }
}

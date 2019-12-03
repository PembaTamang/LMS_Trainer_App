package orionedutech.`in`.lmstrainerapp.fragments.course


import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_course.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.adapters.spinners.BatchSpinAdapter
import orionedutech.`in`.lmstrainerapp.adapters.spinners.ChapterAdapter
import orionedutech.`in`.lmstrainerapp.adapters.spinners.CourseSpinAdapter
import orionedutech.`in`.lmstrainerapp.adapters.spinners.ModuleAdapter
import orionedutech.`in`.lmstrainerapp.database.MDatabase
import orionedutech.`in`.lmstrainerapp.database.entities.Batch
import orionedutech.`in`.lmstrainerapp.fragments.BaseFragment
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.mToast.noInternetSnackBar
import orionedutech.`in`.lmstrainerapp.mToast.showToast
import orionedutech.`in`.lmstrainerapp.network.NetworkOps
import orionedutech.`in`.lmstrainerapp.network.Urls
import orionedutech.`in`.lmstrainerapp.network.dataModels.*
import orionedutech.`in`.lmstrainerapp.network.response


/**
 * A simple [Fragment] subclass.
 */
class CourseFragment : BaseFragment() {


    lateinit var batchSpinner: Spinner
    lateinit var courseSpinner: Spinner
    lateinit var moduleSpinner: Spinner
    lateinit var chapterSpinner: Spinner


    lateinit var batchlisenter: AdapterView.OnItemSelectedListener
    lateinit var courselisenter: AdapterView.OnItemSelectedListener
    lateinit var modulelisenter: AdapterView.OnItemSelectedListener
    lateinit var chapterlisenter: AdapterView.OnItemSelectedListener

    lateinit var moduleContainer: MaterialCardView
    lateinit var chapterContainer: MaterialCardView
    lateinit var courseContainer: MaterialCardView

    lateinit var batchAdapter: BatchSpinAdapter
    lateinit var courseAdapter: CourseSpinAdapter
    lateinit var moduleAdapter: ModuleAdapter
    lateinit var chapterAdapter: ChapterAdapter

    lateinit var animation: LottieAnimationView

    var batchList: ArrayList<Batch> = ArrayList()
    var courseList: ArrayList<DCCourse> = ArrayList()
    var moduleList: ArrayList<DCModule> = ArrayList()
    var chapterList: ArrayList<DCModuleChapter> = ArrayList()

    var selectedBatchID = ""
    var selectedCourseID = ""
    var selectedModuleID = ""
    var selectedChapterID = ""

    var userID = ""

    lateinit var getStudentList: MaterialButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_course, container, false)
        batchSpinner = view.batch_spinner
        courseSpinner = view.course_spinner
        moduleSpinner = view.module_spinner
        chapterSpinner = view.chapter_spinner
        getStudentList = view.getStudentList
        moduleContainer = view.materialCardView5
        chapterContainer = view.materialCardView3
        courseContainer = view.materialCardView2
        animation = view.anim

        batchAdapter =
            BatchSpinAdapter(context!!, android.R.layout.simple_list_item_1, batchList, 0)
        courseAdapter =
            CourseSpinAdapter(context!!, android.R.layout.simple_list_item_1, courseList)
        moduleAdapter = ModuleAdapter(context!!, android.R.layout.simple_list_item_1, moduleList)
        chapterAdapter = ChapterAdapter(context!!, android.R.layout.simple_list_item_1, chapterList)

        batchSpinner.adapter = batchAdapter
        courseSpinner.adapter = courseAdapter
        moduleSpinner.adapter = moduleAdapter
        chapterSpinner.adapter = chapterAdapter

        batchlisenter = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val batch = batchList[p2]
                selectedBatchID = batch.batch_id.toString()
                courseContainer.visibility = GONE
                CoroutineScope(Dispatchers.IO).launch {
                    getCourseData(selectedBatchID)
                }
            }

        }

        courselisenter = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                /*   if (firstCourseSelection) {
                       firstCourseSelection = false
                       mLog.i(TAG,"returning")
                       return
                   }*/
                mLog.i(TAG, "going")
                val course = courseList[p2]
                selectedCourseID = course.course_id
                when (course.course_links_to) {
                    "1" -> {
                        getStudentList.isEnabled = false
                        moduleContainer.visibility = GONE
                        getModuleData()
                    }
                    else -> {
                        showToast(context, "error")
                    }
                }
            }

        }

        modulelisenter = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {


            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                /*      if (firstModuleSelection) {
                          firstModuleSelection = false
                          return
                      }*/
                val module = moduleList[p2]
                selectedModuleID = module.course_module_id
                getChapterData(selectedModuleID)
            }

        }

        chapterlisenter = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                val chapter = chapterList[p2]
                selectedChapterID = chapter.chapter_id
                getStudentList.isEnabled = true

            }


        }

        batchSpinner.onItemSelectedListener = batchlisenter

        courseSpinner.onItemSelectedListener = courselisenter

        moduleSpinner.onItemSelectedListener = modulelisenter

        chapterSpinner.onItemSelectedListener = chapterlisenter


        CoroutineScope(Dispatchers.IO).launch {
            context?.let {
                val db = MDatabase(it)
                val dao = db.getBatchDao()
                val userDao = db.getUserDao()
                userID = userDao.getUserID()
                if (dao.batchDataExists()) {
                    batchList.addAll(dao.getAllBatches())
                    withContext(Dispatchers.Main) {
                        batchAdapter.notifyDataSetChanged()

                    }
                }
            }

        }

        getStudentList.setOnClickListener {
            //go to student list fragment
            val fragment = StudentListFragment()
            val bundle = Bundle()
            bundle.putString("batch_id", selectedBatchID)
            bundle.putString("course_id", selectedCourseID)
            bundle.putString("module_id", selectedModuleID)
            bundle.putString("chapter_id", selectedChapterID)
            bundle.putString("unit_id", "0")
            bundle.putString("subunit_id", "0")
            bundle.putString("user_id", userID)

            fragment.arguments = bundle

            val ft = activity?.supportFragmentManager!!.beginTransaction()
            ft.setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
            ft.add(R.id.mainContainer, fragment)
            ft.addToBackStack(null)
            ft.commit()
        }
        return view
    }

    fun disableSpinners() {
        batchSpinner.isEnabled = false
        courseSpinner.isEnabled = false
        moduleSpinner.isEnabled = false
        chapterSpinner.isEnabled = false
    }

    fun enableSpinners() {
        batchSpinner.isEnabled = true
        courseSpinner.isEnabled = true
        moduleSpinner.isEnabled = true
        chapterSpinner.isEnabled = true
    }

    fun showWaitAnimation() {
        animation.visibility = VISIBLE
        animation.playAnimation()
    }

    fun hideAnimation() {
        animation.visibility = GONE
        animation.cancelAnimation()
    }

    fun getCourseData(batchId: String?) {
        activity!!.runOnUiThread {
            moduleContainer.visibility = GONE
            chapterContainer.visibility = GONE
            selectedModuleID = ""
            getStudentList.isEnabled = false
            showWaitAnimation()
            disableSpinners()
        }
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
                            mLog.i(TAG, "response : $string")
                            val courses = Gson().fromJson(string, DCCourseList::class.java)
                            if (courses == null) {
                                onfailure()
                                return
                            }

                            if (courses.success == "1") {

                                val course = courses.courses
                                if (course.isNotEmpty()) {
                                    courseList.clear()
                                    courseList.addAll(course)
                                    activity!!.runOnUiThread {
                                        mLog.i(TAG, "done course lise ${courseList.size}")
                                        courseContainer.visibility = VISIBLE
                                        courseSpinner.onItemSelectedListener = null
                                        selectedCourseID = courseList[0].course_id
                                        hideAnimation()
                                        enableSpinners()
                                        courseAdapter.notifyDataSetChanged()
                                        Handler().postDelayed({
                                            courseSpinner.onItemSelectedListener = courselisenter
                                        }, 500)

                                    }
                                } else {
                                    onfailure()
                                }
                            } else {
                                onfailure()
                            }
                        }

                        override fun onfailure() {
                            activity!!.runOnUiThread {
                                enableSpinners()
                                hideAnimation()
                                showToast(context, "error")
                            }
                        }

                    }) { _, _, _ -> }
            }
        }
    }

    private fun getModuleData() {
        activity!!.runOnUiThread {
            selectedChapterID = ""
            getStudentList.isEnabled = false
            showWaitAnimation()
            disableSpinners()
        }
        val json = JSONObject()
        json.put("course_id", selectedCourseID)
        json.put("course_links_to", "1")
        NetworkOps.post(Urls.courseLinkedModules, json.toString(), context, object : response {
            override fun onInternetfailure() {
                activity!!.runOnUiThread {
                    noInternetSnackBar(activity!!)
                }
            }

            override fun onrespose(string: String?) {
                mLog.i(TAG, "response : $string ")
                val moduleData = Gson().fromJson(string, DCModuleData::class.java)
                if (moduleData == null) {
                    onfailure()
                    return
                }
                if (moduleData.success == "1") {
                    val modules = moduleData.response
                    if (!modules.isNullOrEmpty()) {
                        moduleList.clear()
                        moduleList.addAll(modules)
                        activity!!.runOnUiThread {
                            moduleContainer.visibility = VISIBLE
                            moduleSpinner.onItemSelectedListener = null
                            selectedModuleID = moduleList[0].course_module_id
                            moduleAdapter.notifyDataSetChanged()
                            hideAnimation()
                            enableSpinners()
                            Handler().postDelayed({
                                moduleSpinner.onItemSelectedListener = modulelisenter
                            }, 500)
                        }
                    } else {
                        onfailure()
                    }

                } else {
                    onfailure()
                }
            }

            override fun onfailure() {
                activity!!.runOnUiThread {
                    enableSpinners()
                    hideAnimation()
                    showToast(context, "module data error")
                }
            }

        })
        { _, _, _ -> }
    }


    private fun getChapterData(courseModuleId: String) {
        activity!!.runOnUiThread {
            chapterContainer.visibility = GONE
            getStudentList.isEnabled = false
            showWaitAnimation()
            disableSpinners()
        }
        val json = JSONObject()
        json.put("module_id", courseModuleId)
        NetworkOps.post(Urls.moduleLinkedChapters, json.toString(), context, object : response {
            override fun onInternetfailure() {
                activity!!.runOnUiThread {
                    noInternetSnackBar(activity)
                }
            }

            override fun onrespose(string: String?) {
                val chapterData = Gson().fromJson(string, DCChapterData::class.java)
                if (chapterData == null) {
                    onfailure()
                    return
                }
                val chapterDataOne = chapterData.module_details
                val chapters = chapterDataOne.module_chapters
                chapterList.clear()
                chapterList.addAll(chapters)
                activity!!.runOnUiThread {
                    getStudentList.isEnabled = true
                    chapterContainer.visibility = VISIBLE
                    chapterSpinner.onItemSelectedListener = null
                    selectedChapterID = chapterList[0].chapter_id
                    chapterAdapter.notifyDataSetChanged()
                    enableSpinners()
                    hideAnimation()
                    Handler().postDelayed({
                        chapterSpinner.onItemSelectedListener = chapterlisenter
                    }, 500)
                }
            }

            override fun onfailure() {
                activity!!.runOnUiThread {
                    enableSpinners()
                    hideAnimation()
                    showToast(context, "failed to get chapter data")
                }
            }

        }) { _, _, _ ->
        }

    }
}


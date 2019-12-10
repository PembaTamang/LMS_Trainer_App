package orionedutech.`in`.lmstrainerapp.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.PathEffect
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.VISIBLE
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.core.view.forEach
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_parent.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import kotlinx.coroutines.launch
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.database.MDatabase
import orionedutech.`in`.lmstrainerapp.fragments.DashFragment
import orionedutech.`in`.lmstrainerapp.fragments.ManualFragment
import orionedutech.`in`.lmstrainerapp.fragments.PasswordResetFragment
import orionedutech.`in`.lmstrainerapp.fragments.assessment.AssessmentFragment
import orionedutech.`in`.lmstrainerapp.fragments.assignment.AssignmentFragment
import orionedutech.`in`.lmstrainerapp.fragments.batch.BatchFragment
import orionedutech.`in`.lmstrainerapp.fragments.course.CourseFragment
import orionedutech.`in`.lmstrainerapp.fragments.feedback.FeedbackFragment
import orionedutech.`in`.lmstrainerapp.fragments.profile.ParentFragment
import orionedutech.`in`.lmstrainerapp.fragments.scoreCard.ScoreFragment
import orionedutech.`in`.lmstrainerapp.interfaces.*
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.showToast

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener,
    MoveNavBar.move, profilebooleantoggle.capture {
    override fun capturepic(boolean: Boolean) {
        mLog.i(TAG, "val $boolean")
        profile = boolean
    }

    lateinit var ft: FragmentTransaction
    private var isBarDown = true
    private var exit = false
    private var lastpop = false
    private var lastFrag: String = ""
    private lateinit var bottomNav: BottomNavigationView
    lateinit var profilePref: SharedPreferences
    private val permissionCode = 100
    var permissions = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.CAMERA
    )
    var profile = false
    lateinit var barPreferences: SharedPreferences
    private fun hasPermissions(context: Context, vararg permissions: String): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNav = bottom_navigation

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        profilePref = getSharedPreferences("profile", Context.MODE_PRIVATE)

        notification.setOnClickListener {
            mLog.i(TAG,"clicked")
        }
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        barPreferences = getSharedPreferences("bar", Context.MODE_PRIVATE)
        //for camera
      //  barPreferences.edit().putBoolean("move", true).apply()

        drawerLayout.addDrawerListener(toggle)

        drawerLayout.setScrimColor(ContextCompat.getColor(this, android.R.color.transparent))
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        val m = nav_view.menu
        for (i in 0 until m.size() - 1) {
            val mi = m.getItem(i)
            val font: Typeface? = ResourcesCompat.getFont(
                this,
                R.font.opensans_regular
            )
            val mNewTitle = SpannableString(mi.toString())
            mNewTitle.setSpan(font?.let {
                CustomTypefaceSpan(
                    "",
                    it
                )
            }, 0, mNewTitle.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            mi.title = mNewTitle

        }
        val mi = m.getItem(m.size() - 1)
        val font: Typeface? = ResourcesCompat.getFont(
            this,
            R.font.opensans_bold
        )
        val mNewTitle = SpannableString(mi.toString())
        mNewTitle.setSpan(font?.let {
            CustomTypefaceSpan(
                "",
                it
            )
        }, 0, mNewTitle.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        mi.title = mNewTitle

        addDash()
        lastFrag = DashFragment().javaClass.simpleName
        mLog.i(TAG, "first dash frag count ${supportFragmentManager.backStackEntryCount} ")
        checkDashBoard()

        val headerView = nav_view.getHeaderView(0)
        headerView.profile_image
        val imageURL = profilePref.getString("image", "")
        if (imageURL != "") {
            Glide.with(this).load(imageURL).skipMemoryCache(true).diskCacheStrategy(
                DiskCacheStrategy.NONE).into(headerView.profile_image)
        }
        launch {
            applicationContext?.let {
                val dao = MDatabase(it).getUserDao()
                headerView.name.text = dao.getadminName()
            }
        }

        flash.setOnClickListener {
            CaptureInterface.theRealInstance.toggleflash()
        }

        capture.setOnClickListener {
            CaptureInterface.theRealInstance.capture()
        }
        lens.setOnClickListener {
            CaptureInterface.theRealInstance.togglelens()
        }
        bottomNav.setOnNavigationItemSelectedListener(object :
            NavigationView.OnNavigationItemSelectedListener,
            BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.dashboard -> {
                        mLog.i(TAG, "dash clicked")
                        bottomNav.menu.setGroupCheckable(0, true, true)
                        if (profile) {
                            profile = false
                            mLog.i(TAG, "${supportFragmentManager.backStackEntryCount}")
                            supportFragmentManager.popBackStack()
                        } else {
                            changeFragment(DashFragment())
                        }
                        lastFrag = DashFragment::javaClass.name
                        checkDashBoard()
                        return true
                    }
                    R.id.profile -> {
                        mLog.i(TAG, "last frag before adding profile $lastFrag")
                        if (lastFrag != ParentFragment::class.java.simpleName) {
                            profile = true
                            bottomNav.menu.setGroupCheckable(0, true, true)
                            lastFrag = ParentFragment::class.java.simpleName
                            val fragment =  ParentFragment()
                            ft = supportFragmentManager.beginTransaction()
                            ft.setCustomAnimations(
                                R.anim.enter_from_right,
                                R.anim.exit_to_left,
                                R.anim.enter_from_left,
                                R.anim.exit_to_right
                            )
                            ft.add(R.id.mainContainer,fragment)
                            ft.addToBackStack(null)
                            ft.commit()
                            lastpop = false
                            uncheckAll()

                            mLog.i(TAG, "profile clicked")
                        }
                        return true
                    }
                }
                return false
            }

        })
        if (!hasPermissions(this, *permissions)) {
            ActivityCompat.requestPermissions(this, permissions, permissionCode)
        }
        Handler().postDelayed({
            cameraControls.visibility = VISIBLE
        }, 1500)
        cameraControls.animate()
            .translationYBy(1000f)
            .duration = 0
        MoveNavBar.theRealInstance.setListener(this)
        profilebooleantoggle.theRealInstance.setListener(this)
    }

    fun pulldownBar() {
        cameraControls.animate()
            .translationYBy(1000f)
            .duration = 2000
        profile = true
    }

    fun pushupBar() {
        cameraControls.animate()
            .translationYBy(-1000f)
            .duration = 1000
        profile = false
    }

    fun moveNavBar() {
        if (isBarDown) {
            pushupBar()
            isBarDown = false
        } else {
            pulldownBar()
            isBarDown = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_settings -> {
                val fragment = PasswordResetFragment()
                lastFrag = fragment.javaClass.simpleName
                mLog.i(TAG, "password frag name $lastFrag")
                ft = supportFragmentManager.beginTransaction()
                ft.setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
                ft.add(R.id.mainContainer, fragment)
                ft.addToBackStack(null)
                ft.commit()
                lastpop = false
                bottomNav.menu.setGroupCheckable(0, false, true)
                mLog.i(
                    TAG,
                    "backstack count password change ${supportFragmentManager.backStackEntryCount} "
                )
                return true
            }
            R.id.logout -> {
                showLogOutAlert()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun addDash() {
        ft = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(
            R.anim.fadein,
            R.anim.fadeout,
            R.anim.fadein,
            R.anim.fadeout
        )
        ft.add(R.id.mainContainer, DashFragment())
        ft.addToBackStack(null)
        ft.commit()
    }

    private fun checkDashBoard() {
        nav_view.menu[0].isChecked = true
    }

    private fun uncheckAll() {
        nav_view.menu.forEach {
            it.isChecked = false
        }
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        val drawer: DrawerLayout = findViewById(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        //viewID = p0.itemId
        when (p0.itemId) {

            R.id.dash_board -> {
                changeFragment(DashFragment())

            }
            R.id.batch_creation -> {
                changeFragment(BatchFragment())

            }
            R.id.student -> {
                changeFragment(ScoreFragment())

            }
            R.id.course -> {
                changeFragment(CourseFragment())

            }
            R.id.assignment -> {
                changeFragment(AssignmentFragment())

            }
            R.id.assessment -> {
                changeFragment(AssessmentFragment())

            }
            R.id.feedback -> {

                changeFragment(FeedbackFragment())
            }
            R.id.manual -> {

                changeFragment(ManualFragment())
            }
            R.id.logout -> {
                showLogOutAlert()
            }
        }
        return true
    }

    private fun showLogOutAlert() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Alert")
            .setMessage("Do you want to log out ?")
            .setPositiveButton("logout") { _, _ ->
                launch {
                    applicationContext?.let {
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        overridePendingTransition(
                            R.anim.enter_from_left,
                            R.anim.exit_to_right
                        )
                        finish()
                    }
                }
            }
            .setNegativeButton("cancel") { dialogInterface, _ ->
                checkDashBoard()
                dialogInterface.dismiss()
            }.create().show()
    }

    private fun changeFragment(fragment: Fragment) {
        lastFrag =
            supportFragmentManager.findFragmentById(R.id.mainContainer)?.javaClass?.simpleName!!
        mLog.i(TAG, "fragment name ${fragment.javaClass.simpleName}")

        if (fragment.javaClass.simpleName != "DashFragment") {

            bottomNav.menu.setGroupCheckable(0, false, true)
        }
        if (lastFrag != fragment.javaClass.simpleName) {
            ft = supportFragmentManager.beginTransaction()
            ft.setCustomAnimations(
                R.anim.fadein,
                R.anim.fadeout,
                R.anim.fadein,
                R.anim.fadeout
            )
            ft.replace(R.id.mainContainer, fragment)
            ft.commit()
            lastpop = false
            lastFrag = fragment.javaClass.simpleName

            if (!isBarDown) {
                pulldownBar()
                isBarDown = true
            }

            mLog.i(TAG, "backstack count on change ${supportFragmentManager.backStackEntryCount} ")
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(nav_view)) {
            drawer_layout.closeDrawer(nav_view)
        } else {
            mLog.i(
                TAG,
                " backstack count backpressed ${supportFragmentManager.backStackEntryCount}"
            )
            mLog.i(TAG, "last fragment ${getlastFrag()}")
            if (getlastFrag() == "CameraFragment") {
                pulldownBar()
                isBarDown = true
                mLog.i(TAG, "pulling down bar")
            }
            if (profile) {
                bottomNav.selectedItemId = R.id.dashboard
                profile = false
            }
            if (supportFragmentManager.backStackEntryCount > 1) {
                if (getlastFrag() != "ParentFragment") {
                        supportFragmentManager.popBackStack()
                            mLog.i(TAG, "popping")
                }
            } else {
                if (getlastFrag() == DashFragment().javaClass.simpleName) {
                    if (!exit) {
                        showToast("press back again to exit")
                        Handler().postDelayed({ exit = false }, 2000)
                        exit = true
                    } else {
                        finish()
                    }
                } else {
                    if (!lastpop) {
                        bottomNav.selectedItemId = R.id.dashboard
                        checkDashBoard()
                        lastpop = true
                    }

                }

            }

        }
    }

    private fun getlastFrag(): String {
        return supportFragmentManager.findFragmentById(R.id.mainContainer)?.javaClass?.simpleName!!
    }

    override fun finish() {
        val dashPref = getSharedPreferences("dash", Context.MODE_PRIVATE)
        dashPref.edit().clear().apply()
        mLog.i(TAG, "clearing preferences")
        super.finish()
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
    }


    override fun movebar() {
        if (barPreferences.getBoolean("move", true)) {
            moveNavBar()
        }
    }


}

package orionedutech.`in`.lmstrainerapp.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
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
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.showToast

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener,
    DrawerLayout.DrawerListener {


    lateinit var ft: FragmentTransaction
    private var exit = false
    private var lastpop = false
    private var lastFrag: String = ""
    private lateinit var bottomNav: BottomNavigationView
    private val permissionCode = 100
    var permissions = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.CAMERA
    )

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNav = bottom_navigation
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)

        drawerLayout.setScrimColor(ContextCompat.getColor(this, android.R.color.transparent))
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()
        drawerLayout.addDrawerListener(this)
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
        launch {
            applicationContext?.let {
                val dao = MDatabase(it).getUserDao()
                headerView.name.text = dao.getadminName()
            }
        }



        bottomNav.setOnNavigationItemSelectedListener(object :
            NavigationView.OnNavigationItemSelectedListener,
            BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.dashboard -> {
                        mLog.i(TAG, "dash clicked")
                        changeFragment(DashFragment())
                        return true
                    }
                   /* R.id.menu -> {

                        mLog.i(TAG, "menu clicked")
                        return true
                    }*/
                    R.id.profile -> {

                        mLog.i(TAG, "profile clicked")
                        return true
                    }
                }
                return false
            }

        })
        if (!hasPermissions(this, *permissions)) {
            ActivityCompat.requestPermissions(this, permissions, permissionCode)
        }
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_settings -> {
                val fragment = PasswordResetFragment()
                lastFrag = fragment.javaClass.simpleName
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
                lastFrag = PasswordResetFragment().javaClass.simpleName
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
            // }
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
            if (supportFragmentManager.backStackEntryCount > 1) {
                supportFragmentManager.popBackStack()
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
                        changeFragment(DashFragment())
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
        super.finish()
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
    }

    override fun onDrawerStateChanged(newState: Int) {
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
    }

    override fun onDrawerOpened(drawerView: View) {
    }

    override fun onDrawerClosed(drawerView: View) {
        /*   if(viewID!=0){
           when (viewID) {

               R.id.dash_board -> {
                   changeFragment(DashFragment())
               }

               R.id.batch_creation -> {

               }
               R.id.student -> {

               }
               R.id.course -> {

               }
               R.id.assignment -> {
                   changeFragment(AssignmentFragment())
               }
               R.id.assessment -> {
                   changeFragment(AssessmentFragment())

               }
               R.id.feedback -> {

               }
               R.id.manual -> {

               }
               R.id.logout -> {

               }
           }


       }*/
    }

}

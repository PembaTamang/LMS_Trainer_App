package orionedutech.`in`.lmstrainerapp.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.fragments.DashFragment
import orionedutech.`in`.lmstrainerapp.showToast

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var ft: FragmentTransaction
    private var exit = false
    private var lastFrag: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
        checkDashBoard()
        ft = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(
            R.anim.fadein,
            R.anim.fadeout
        )
        ft.replace(R.id.mainContainer, DashFragment())
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

                return true
            }
            R.id.logout -> {

                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        val drawer: DrawerLayout = findViewById(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        when (p0.itemId) {

            R.id.dash_board -> {
                changeFragment(DashFragment())
                return true
            }
            R.id.batch_creation -> {

                return true
            }
            R.id.student -> {

                return true
            }
            R.id.course -> {

                return true
            }
            R.id.assignment -> {

                return true
            }
            R.id.assessment -> {

                return true
            }
            R.id.feedback -> {

                return true
            }
            R.id.manual -> {

                return true
            }
            R.id.logout -> {

                return true
            }
        }


        return true
    }

    private fun changeFragment(fragment: Fragment) {
        if (lastFrag == "" || lastFrag != fragment.javaClass.simpleName) {
            lastFrag = fragment.javaClass.simpleName
            ft = supportFragmentManager.beginTransaction()
            ft.setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
            ft.add(R.id.mainContainer, fragment)
            ft.commit()
        }
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(nav_view)) {
            drawer_layout.closeDrawer(nav_view)
        } else {

            val count = supportFragmentManager.backStackEntryCount
            if (count > 1) {
                supportFragmentManager.popBackStack()
            } else {
                if (!exit) {
                    showToast("press back again to exit")
                    Handler().postDelayed({ exit = false }, 2000)
                    exit = true
                } else {
                    finish()
                }
            }

        }
    }



}

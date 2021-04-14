package com.example.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.foodrunner.R
import com.example.foodrunner.fragment.*
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    lateinit var toolbar:Toolbar
    lateinit var drawerLayout:DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    var previousMenuItem: MenuItem?=null
    lateinit var sharedPreferences: SharedPreferences
    lateinit var headerView: View
    lateinit var mobile_number: TextView
    lateinit var searchView:SearchView
    lateinit var name:TextView
    var titleName:String?="Username"
    var titleMobileNumber:String?="XXXXXXXX"
    var user_id:String?="00"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences=getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE)

        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        toolbar = findViewById(R.id.toolbar)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        frameLayout = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navigationView)
        headerView = navigationView.getHeaderView(0);
        name =  headerView.findViewById(R.id.drawer_name);
        mobile_number=headerView.findViewById(R.id.drawer_mobile_number)
        searchView=findViewById(R.id.searchView)

        titleName=sharedPreferences.getString("Name","Username")
        titleMobileNumber=sharedPreferences.getString("Mobile_Number","XXXXXXXXXXX")


        name.setText(titleName)
        mobile_number.setText(titleMobileNumber)


        setUpToolbar()

        openHome()
        val actionBarDrawerToggle= ActionBarDrawerToggle(this@MainActivity,
            drawerLayout,
            R.string.openDrawer,
            R.string.closeDrawer
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it
            when (it.itemId) {
                R.id.home -> {
                    openHome()
                    drawerLayout.closeDrawers()

                }
                R.id.myProfile->
                {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, MyProfileFragment())
                        .commit()
                    supportActionBar?.title="My Profile"
                    drawerLayout.closeDrawers()
                    searchView.visibility=View.GONE

                }
                R.id.favourite_restaurant -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, FavouriteFragment())
                        .commit()
                    supportActionBar?.title="Favourites"
                    drawerLayout.closeDrawers()
                    searchView.visibility=View.GONE
                }
                R.id.order_history -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, OrderHistoryFragment())
                        .commit()
                    supportActionBar?.title="Order History"
                    drawerLayout.closeDrawers()
                    searchView.visibility=View.GONE
                }
                R.id.faqs -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, FaqFragment())
                        .commit()
                    supportActionBar?.title="Faqs"
                    drawerLayout.closeDrawers()
                    searchView.visibility=View.GONE
                }
                R.id.log_out->
                {
                    logOut()
                }

            }
            return@setNavigationItemSelectedListener true
        }

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId

        if(id==android.R.id.home)
        {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }
    fun openHome()
    {
        val fragment= HomeFragment()
        val transaction=supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame,fragment)
        transaction.commit()
        supportActionBar?.title="All Restaurants"
        navigationView.setCheckedItem(R.id.home)
        searchView.visibility=View.VISIBLE
    }


    fun setUpToolbar()
    {
        setSupportActionBar(toolbar)
        supportActionBar?.title="All Restaurant"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onBackPressed() {
        val frag=supportFragmentManager.findFragmentById(R.id.frame)
        when(frag)
        {
            !is HomeFragment -> openHome()

            else -> super.onBackPressed()
        }

    }

    private fun logOut()
    {
        startActivity(Intent(this@MainActivity,LoginActivity::class.java))
        sharedPreferences.edit().clear().apply()
        finish()
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

}
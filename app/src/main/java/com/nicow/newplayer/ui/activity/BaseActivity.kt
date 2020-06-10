package com.nicow.newplayer.ui.activity

import androidx.appcompat.app.AppCompatActivity
import com.nicow.newplayer.R
import com.nicow.newplayer.ui.fragment.BottomFragment

open class BaseActivity : AppCompatActivity() {

    protected fun createBottomFragment() {
        val bottomFragment = BottomFragment.newInstance(" ", " ")
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.bottom_container, bottomFragment)
        transaction.commit()

    }

}
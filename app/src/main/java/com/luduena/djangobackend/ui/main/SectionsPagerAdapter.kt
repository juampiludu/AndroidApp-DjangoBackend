package com.luduena.djangobackend.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.luduena.djangobackend.HomeFragment
import com.luduena.djangobackend.ProfileFragment
import com.luduena.djangobackend.R

private val TAB_TITLES = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2
)

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {

        lateinit var fragment: Fragment

        when (position) {

            0 -> fragment = HomeFragment()
            1 -> fragment = ProfileFragment()

        }

        return fragment

    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return 2
    }
}
package dev.mooner.autoselfdiagnosis.ui.main

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mikepenz.aboutlibraries.LibsBuilder
import dev.mooner.autoselfdiagnosis.R
import dev.mooner.autoselfdiagnosis.ui.main.fragments.HomeFragment
import dev.mooner.autoselfdiagnosis.ui.main.fragments.SettingsFragment

class ViewPagerAdapter(activity: MainActivity): FragmentStateAdapter(activity) {

    companion object {
        const val PAGE_COUNT = 3
    }

    override fun getItemCount(): Int = PAGE_COUNT

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> HomeFragment()
            1 -> SettingsFragment()
            2 -> LibsBuilder().apply {
                withFields(R.string::class.java.fields)
                withAboutIconShown(true)
                withAboutAppName("자동 자가진단")
                withAboutVersionShown(true)
            }.supportFragment()
            else -> HomeFragment()
        }
    }

}
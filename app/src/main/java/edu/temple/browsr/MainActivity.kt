package edu.temple.browsr

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import android.content.Context

class MainActivity : AppCompatActivity(), BrowserInterface {
    private lateinit var viewPager: ViewPager2
    private lateinit var pagerAdapter: BrowserPagerAdapter
    private var currentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.viewPager)
        pagerAdapter = BrowserPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        // Restore state from SharedPreferences or create initial tab
        val prefs = getSharedPreferences("BrowserState", Context.MODE_PRIVATE)
        val tabCount = prefs.getInt("TAB_COUNT", 0)
        currentPosition = prefs.getInt("CURRENT_POSITION", 0)

        if (tabCount > 0) {
            // Restore tabs
            repeat(tabCount) {
                pagerAdapter.addTab()
            }
            // Restore position
            viewPager.setCurrentItem(currentPosition, false)
        } else {
            // Add initial tab
            pagerAdapter.addTab()
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPosition = position
            }
        })
    }

    override fun onStop() {
        super.onStop()
        // Save state when app is closed
        getSharedPreferences("BrowserState", Context.MODE_PRIVATE)
            .edit()
            .putInt("TAB_COUNT", pagerAdapter.itemCount)
            .putInt("CURRENT_POSITION", currentPosition)
            .apply()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("TAB_COUNT", pagerAdapter.itemCount)
        outState.putInt("CURRENT_POSITION", currentPosition)
    }

    override fun addNewTab() {
        pagerAdapter.addTab()
        viewPager.currentItem = pagerAdapter.itemCount - 1
    }

    override fun loadUrl(url: String) {
        getCurrentTab()?.loadUrl(url)
    }

    override fun updateUrl(url: String) {
        getCurrentTab()?.updateUrl(url)
    }

    override fun goBack() {
        getCurrentTab()?.goBack()
    }

    override fun goForward() {
        getCurrentTab()?.goForward()
    }

    private fun getCurrentTab(): TabFragment? {
        return supportFragmentManager.findFragmentByTag("f${viewPager.currentItem}")
            as? TabFragment
    }
}

class BrowserPagerAdapter(activity: MainActivity) : FragmentStateAdapter(activity) {
    private val tabs = mutableListOf<TabFragment>()

    fun addTab() {
        tabs.add(TabFragment())
        notifyItemInserted(tabs.size - 1)
    }

    override fun getItemCount(): Int = tabs.size

    override fun createFragment(position: Int): Fragment = tabs[position]
}

interface BrowserInterface {
    fun addNewTab()
    fun loadUrl(url: String)
    fun updateUrl(url: String)
    fun goBack()
    fun goForward()
} 
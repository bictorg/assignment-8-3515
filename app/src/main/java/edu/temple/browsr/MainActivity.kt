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

        // Try to restore saved state
        val prefs = getSharedPreferences("BrowserState", Context.MODE_PRIVATE)
        val tabCount = prefs.getInt("TAB_COUNT", 1)
        currentPosition = prefs.getInt("CURRENT_POSITION", 0)

        // Create tabs
        if (tabCount > 0) {
            repeat(tabCount) { index ->
                val url = prefs.getString("TAB_URL_$index", null)
                pagerAdapter.addTab()
                if (url != null) {
                    // Load the URL after a short delay to ensure the fragment is ready
                    viewPager.post {
                        getCurrentTab()?.loadUrl(url)
                    }
                }
            }
            // Set the last active tab
            viewPager.setCurrentItem(currentPosition, false)
        } else {
            // Fallback: start with one tab
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
        // Save state when app is stopped
        val prefs = getSharedPreferences("BrowserState", Context.MODE_PRIVATE).edit()
        
        prefs.putInt("TAB_COUNT", pagerAdapter.itemCount)
        prefs.putInt("CURRENT_POSITION", currentPosition)

        // Save URL for each tab
        for (i in 0 until pagerAdapter.itemCount) {
            val tab = getCurrentTabAt(i)
            val url = tab?.getCurrentUrl()
            if (url != null) {
                prefs.putString("TAB_URL_$i", url)
            }
        }

        prefs.apply()
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
        return getCurrentTabAt(viewPager.currentItem)
    }

    private fun getCurrentTabAt(position: Int): TabFragment? {
        return supportFragmentManager.findFragmentByTag("f$position") as? TabFragment
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
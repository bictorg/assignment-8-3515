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
    private val tabUrls = mutableMapOf<Int, String>()

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

        // Create tabs and restore URLs
        if (tabCount > 0) {
            repeat(tabCount) { index ->
                val url = prefs.getString("TAB_URL_$index", null)
                if (url != null) {
                    tabUrls[index] = url
                }
                pagerAdapter.addTab()
                // Load the URL after a short delay
                viewPager.post {
                    getCurrentTabAt(index)?.loadUrl(url ?: "about:blank")
                }
            }
            viewPager.setCurrentItem(currentPosition, false)
        } else {
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
        val prefs = getSharedPreferences("BrowserState", Context.MODE_PRIVATE).edit()
        
        prefs.putInt("TAB_COUNT", pagerAdapter.itemCount)
        prefs.putInt("CURRENT_POSITION", currentPosition)

        // Save URLs from our tracking map
        tabUrls.forEach { (index, url) ->
            prefs.putString("TAB_URL_$index", url)
        }

        prefs.apply()
    }

    // Add this new method to update stored URLs
    fun updateStoredUrl(url: String) {
        tabUrls[currentPosition] = url
    }

    override fun addNewTab() {
        pagerAdapter.addTab()
        tabUrls[pagerAdapter.itemCount - 1] = "about:blank"
        viewPager.currentItem = pagerAdapter.itemCount - 1
    }

    override fun loadUrl(url: String) {
        getCurrentTab()?.loadUrl(url)
        tabUrls[currentPosition] = url
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
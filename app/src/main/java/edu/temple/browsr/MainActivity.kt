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
    private var tabUrls = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.viewPager)
        pagerAdapter = BrowserPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        // Restore state from SharedPreferences
        val prefs = getSharedPreferences("BrowserState", Context.MODE_PRIVATE)
        val tabCount = prefs.getInt("TAB_COUNT", 1) // Default to 1 tab
        currentPosition = prefs.getInt("CURRENT_POSITION", 0)
        
        try {
            // Restore URLs
            tabUrls.clear()
            for (i in 0 until tabCount) {
                val url = prefs.getString("TAB_URL_$i", "about:blank") ?: "about:blank"
                tabUrls.add(url)
            }

            // Always ensure at least one tab exists
            if (tabUrls.isEmpty()) {
                tabUrls.add("about:blank")
            }

            // Create tabs
            tabUrls.forEach { url ->
                pagerAdapter.addTab(url)
            }

            // Safely set current position
            viewPager.post {
                val safePosition = currentPosition.coerceIn(0, pagerAdapter.itemCount - 1)
                viewPager.setCurrentItem(safePosition, false)
            }
        } catch (e: Exception) {
            // If anything goes wrong, start with a single blank tab
            tabUrls.clear()
            tabUrls.add("about:blank")
            pagerAdapter.addTab("about:blank")
            currentPosition = 0
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
        try {
            // Save state and URLs when app is closed
            val prefs = getSharedPreferences("BrowserState", Context.MODE_PRIVATE).edit()
            prefs.putInt("TAB_COUNT", pagerAdapter.itemCount)
            prefs.putInt("CURRENT_POSITION", currentPosition)
            
            // Save URL for each tab
            for (i in 0 until pagerAdapter.itemCount) {
                val url = tabUrls.getOrNull(i) ?: "about:blank"
                prefs.putString("TAB_URL_$i", url)
            }
            prefs.apply()
        } catch (e: Exception) {
            // If saving fails, clear preferences
            getSharedPreferences("BrowserState", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply()
        }
    }

    override fun addNewTab() {
        tabUrls.add("about:blank")
        pagerAdapter.addTab("about:blank")
        viewPager.currentItem = pagerAdapter.itemCount - 1
    }

    override fun loadUrl(url: String) {
        getCurrentTab()?.loadUrl(url)
        // Update URL in our list
        if (currentPosition < tabUrls.size) {
            tabUrls[currentPosition] = url
        }
    }

    override fun updateUrl(url: String) {
        getCurrentTab()?.updateUrl(url)
        // Update URL in our list
        if (currentPosition < tabUrls.size) {
            tabUrls[currentPosition] = url
        }
    }

    override fun goBack() {
        getCurrentTab()?.goBack()
    }

    override fun goForward() {
        getCurrentTab()?.goForward()
    }

    private fun getCurrentTab(): TabFragment? {
        return try {
            supportFragmentManager.findFragmentByTag("f${viewPager.currentItem}")
                as? TabFragment
        } catch (e: Exception) {
            null
        }
    }
}

class BrowserPagerAdapter(activity: MainActivity) : FragmentStateAdapter(activity) {
    private val tabs = mutableListOf<TabFragment>()

    fun addTab(url: String) {
        tabs.add(TabFragment.newInstance(url))
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
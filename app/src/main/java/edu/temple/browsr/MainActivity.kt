package edu.temple.browsr

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainActivity : AppCompatActivity(), BrowserInterface {
    private lateinit var viewPager: ViewPager2
    private lateinit var pagerAdapter: BrowserPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.viewPager)
        pagerAdapter = BrowserPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        if (savedInstanceState == null) {
            // Add initial tab
            pagerAdapter.addTab()
        }
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
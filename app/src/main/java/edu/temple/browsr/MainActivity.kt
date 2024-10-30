package edu.temple.browsr

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private var pageFragment: PageFragment? = null
    private var controlFragment: ControlFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            pageFragment = PageFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.page, pageFragment!!)
                .commit()
        } else {
            pageFragment = supportFragmentManager.findFragmentById(R.id.page) as? PageFragment
        }

        controlFragment = supportFragmentManager.findFragmentById(R.id.control) as? ControlFragment
    }

    fun loadUrl(url: String) {
        pageFragment?.loadUrl(url)
    }

    fun updateUrl(url: String) {
        controlFragment?.updateUrl(url)
    }

    fun goBack() {
        if (pageFragment?.canGoBack() == true) {
            pageFragment?.goBack()
        }
    }

    fun goForward() {
        if (pageFragment?.canGoForward() == true) {
            pageFragment?.goForward()
        }
    }
} 
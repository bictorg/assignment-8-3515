package edu.temple.browsr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class TabFragment : Fragment() {
    private var pageFragment: PageFragment? = null
    private var controlFragment: ControlFragment? = null
    private var initialUrl: String = "about:blank"

    companion object {
        private const val ARG_URL = "url"
        
        fun newInstance(url: String): TabFragment {
            return TabFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_URL, url)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialUrl = arguments?.getString(ARG_URL) ?: "about:blank"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Get references to child fragments
        childFragmentManager.findFragmentById(R.id.page)?.let {
            pageFragment = it as PageFragment
        }
        
        childFragmentManager.findFragmentById(R.id.control)?.let {
            controlFragment = it as ControlFragment
        }

        // Load initial URL if not blank
        if (initialUrl != "about:blank") {
            pageFragment?.loadUrl(initialUrl)
            controlFragment?.updateUrl(initialUrl)
        }
    }

    fun getCurrentUrl(): String {
        return pageFragment?.getCurrentUrl() ?: "about:blank"
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
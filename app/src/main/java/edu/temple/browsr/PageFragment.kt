package edu.temple.browsr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import java.net.URLEncoder

class PageFragment : Fragment() {
    private var webView: WebView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView = view.findViewById(R.id.webView)
        webView?.apply {
            settings.javaScriptEnabled = true
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    url?.let { 
                        (activity as? MainActivity)?.updateUrl(it)
                        (activity as? MainActivity)?.updateStoredUrl(it)
                    }
                }
            }
        }

        savedInstanceState?.let {
            webView?.restoreState(it)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView?.saveState(outState)
    }

    fun loadUrl(url: String) {
        val processedUrl = processUrl(url)
        webView?.loadUrl(processedUrl)
    }

    fun canGoBack(): Boolean = webView?.canGoBack() ?: false
    fun canGoForward(): Boolean = webView?.canGoForward() ?: false
    fun goBack() = webView?.goBack()
    fun goForward() = webView?.goForward()

    private fun processUrl(url: String): String {
        return when {
            url.startsWith("http://") || url.startsWith("https://") -> url
            url.matches(Regex(".*\\.[a-zA-Z]{2,}.*")) -> "https://$url"
            else -> "https://duckduckgo.com/?q=${URLEncoder.encode(url, "UTF-8")}"
        }
    }

    fun getCurrentUrl(): String? {
        return webView?.url
    }
} 
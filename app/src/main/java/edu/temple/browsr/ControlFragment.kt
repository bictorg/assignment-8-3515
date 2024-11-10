package edu.temple.browsr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment

class ControlFragment : Fragment() {
    private lateinit var urlEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_control, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        urlEditText = view.findViewById(R.id.urlEditText)

        view.findViewById<ImageView>(R.id.goButton).setOnClickListener {
            (activity as? BrowserInterface)?.loadUrl(urlEditText.text.toString())
        }
        
        view.findViewById<ImageView>(R.id.backButton).setOnClickListener {
            (activity as? BrowserInterface)?.goBack()
        }
        
        view.findViewById<ImageView>(R.id.nextButton).setOnClickListener {
            (activity as? BrowserInterface)?.goForward()
        }

        view.findViewById<ImageView>(R.id.newTabButton).setOnClickListener {
            (activity as? BrowserInterface)?.addNewTab()
        }
    }

    fun updateUrl(url: String) {
        urlEditText.setText(url)
    }
} 
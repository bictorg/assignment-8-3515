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
    private lateinit var goButton: ImageView
    private lateinit var backButton: ImageView
    private lateinit var forwardButton: ImageView

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
        goButton = view.findViewById(R.id.goButton)
        backButton = view.findViewById(R.id.backButton)
        forwardButton = view.findViewById(R.id.forwardButton)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        goButton.setOnClickListener {
            val url = urlEditText.text.toString()
            (activity as? MainActivity)?.loadUrl(url)
        }

        backButton.setOnClickListener {
            (activity as? MainActivity)?.goBack()
        }

        forwardButton.setOnClickListener {
            (activity as? MainActivity)?.goForward()
        }
    }

    fun updateUrl(url: String) {
        urlEditText.setText(url)
    }
} 
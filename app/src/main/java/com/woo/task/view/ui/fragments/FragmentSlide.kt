package com.woo.task.view.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import com.woo.task.R
import com.woo.task.databinding.FragmentSlideBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class FragmentSlide : Fragment() {
    private var param1: String? = null
    private lateinit var listener: (CharSequence) -> Unit
    private lateinit var binding: FragmentSlideBinding
    //used to create argument for Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    //Inflate the layout for this fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSlideBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //used to process slide by slide from fragment. it means we can do anything from this code
        val textFragment by lazy {
            mutableListOf<TextView>(
                view.findViewById(R.id.text_up),
                view.findViewById(R.id.text_down)
            )
        }


        when (param1) {
            "3" -> {
                textFragment[0].text = resources.getString(R.string.slide_3_title)
                textFragment[1].text = resources.getString(R.string.slide_3_text)
                binding.slide2.isVisible = false
                binding.slide3.isVisible = true
            }
            "2" -> {
                textFragment[0].text = resources.getString(R.string.slide_2_title)
                textFragment[1].text = resources.getString(R.string.slide_2_text)
                binding.slide1.isVisible = false
                binding.slide2.isVisible = true
            }
            "1" -> {
                textFragment[0].text = resources.getString(R.string.alarm_text)
                textFragment[1].text = resources.getString(R.string.slide_1_text)
                binding.slide1.isVisible = true
                binding.imageFragment.visibility = View.INVISIBLE
                binding.slide0.visibility = View.INVISIBLE
            }
            else -> {
               //binding.imageFragment.isVisible = true
                binding.slide0.isVisible = true
                textFragment[0].text = resources.getString(R.string.app_name)
                textFragment[1].text = resources.getString(R.string.slide_0_text)
            }
        }
    }

    //Companion object used to adapter call fragment and don't need to create object , just call the object
    companion object {
        @JvmStatic
        fun newInstance(param1: String, listener: (CharSequence) -> Unit) =
            FragmentSlide().apply {
                this.listener = listener
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}
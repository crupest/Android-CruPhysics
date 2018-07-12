package crupest.cruphysics.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import crupest.cruphysics.R


class AddBodyListItemFragment : Fragment() {

    private lateinit var name: String
    private var imageSrc: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            name = arguments!!.getString(ARG_NAME)
            imageSrc = arguments!!.getInt(ARG_IMAGE_SRC)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_add_body_list_item, container, false)

        val nameText = rootView.findViewById<TextView>(R.id.name)
        val image = rootView.findViewById<ImageView>(R.id.image)

        nameText.text = name
        image.contentDescription = name

        if (imageSrc == 0)
            image.visibility = View.GONE
        else
            image.setImageResource(imageSrc)

        return rootView
    }

    companion object {
        private const val ARG_NAME = "Name"
        private const val ARG_IMAGE_SRC = "ImageSrc"

        fun newInstance(name: String, imageSrc: Int = 0): AddBodyListItemFragment {
            val fragment = AddBodyListItemFragment()
            val args = Bundle()
            args.putString(ARG_NAME, name)
            args.putInt(ARG_IMAGE_SRC, imageSrc)
            fragment.arguments = args
            return fragment
        }
    }

}

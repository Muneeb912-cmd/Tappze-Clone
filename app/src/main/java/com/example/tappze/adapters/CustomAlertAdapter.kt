package com.example.tappze.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.tappze.R

class CustomAlertAdapter(
    context: Context,
    private val options: Array<String>,
    private val icons: Array<Int>
) : ArrayAdapter<String>(context, R.layout.dialog_item, R.id.dialog_text, options) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.dialog_item, parent, false)
        val icon = view.findViewById<ImageView>(R.id.dialog_icon)
        val text = view.findViewById<TextView>(R.id.dialog_text)

        icon.setImageResource(icons[position])
        text.text = options[position]
        return view
    }
}

package com.example.tappze.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tappze.R
import com.example.tappze.com.example.tappze.models.SocialLinks

class SocialLinkAdapter(private val items: List<SocialLinks>, private val listener: OnItemClickListener) : RecyclerView.Adapter<SocialLinkAdapter.ViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(socialLink: SocialLinks)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view),View.OnClickListener {
        val imageView: ImageView = view.findViewById(R.id.socialLinkImg)
        val textView: TextView = view.findViewById(R.id.socialLinksTxt)
        init {
            view.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(items[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.social_link_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.imageView.setImageResource(item.imageResId)
        holder.textView.text = item.text
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

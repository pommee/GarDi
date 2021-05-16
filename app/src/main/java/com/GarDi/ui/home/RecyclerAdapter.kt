package com.GarDi.ui.home

import android.content.Intent
import android.os.TestLooperManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.GarDi.Models.MaterialHandler
import com.GarDi.Models.Product
import com.GarDi.Models.Singleton
import com.GarDi.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.ArrayList

class RecyclerAdapter(val searches: ArrayList<Product>) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
        return ViewHolder(v, viewGroup)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val search = searches[position]
        holder.itemView.findViewById<TextView>(R.id.name).text = search.productName
        holder.itemView.findViewById<TextView>(R.id.id).text = "product id: " + search.barcode
        val materials = search.materialList
        var result = "";
        if (materials != null) {
            for (item in materials) {
                result += MaterialHandler.findSortingFromMaterial(item as String?)
                result += " "
            }
        }
        holder.itemView.findViewById<TextView>(R.id.recycling).text = "Sorting: " + result


    }
    class ViewHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(
        inflater.inflate(R.layout.fragment_searched_barcode, parent, false)
    ) {
        private var name : TextView? = null
        private var id : TextView? = null
        private var recycling : TextView? = null
        init{
            name = itemView.findViewById(R.id.name)
            id = itemView.findViewById(R.id.id)
            recycling = itemView.findViewById(R.id.recycling)
        }
    }

    override fun getItemCount(): Int {
        return searches.size
    }

}

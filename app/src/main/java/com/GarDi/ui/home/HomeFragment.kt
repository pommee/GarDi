package com.GarDi.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.GarDi.Models.Product
import com.GarDi.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*

class HomeFragment : Fragment() {
    private var searches = ArrayList<Product>()
    lateinit var recycler: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        recycler = root.findViewById(R.id.recyclerViewStats)
        initList()
        return root
    }

    fun initList() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Products").orderBy("timesSearched", Query.Direction.DESCENDING).limit(3)
            .get()
            .addOnSuccessListener { result ->
                for (product in result) {
                    searches.add(product.toObject(Product::class.java))
                }
                val adapter = RecyclerAdapter(searches)
                if (recycler != null) {
                    recycler.layoutManager = LinearLayoutManager(context)
                }
                if (recycler != null) {
                    recycler.adapter = adapter
                }
                recycler.adapter?.notifyDataSetChanged()
                if (recycler != null) {
                    Log.e("TAG", recycler.adapter?.itemCount.toString())
                }

                Log.e("TAG", searches.toString())
            }
    }
}
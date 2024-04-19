package com.example.foodapp.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.foodapp.MenuAdapter
import com.example.foodapp.MenuBootomSheetFragment
import com.example.foodapp.R
import com.example.foodapp.adaptar.PopularAdapter
import com.example.foodapp.databinding.FragmentHomeBinding
import com.example.foodapp.model.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var database : FirebaseDatabase
    private lateinit var menuItems: MutableList<MenuItem>
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Tăng cường bố cục cho đoạn này
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.viewAllMenu.setOnClickListener {
            val bottomSheetDialog = MenuBootomSheetFragment()
            bottomSheetDialog.show(parentFragmentManager, "Test")
        }
        // Truy xuất và hiển thị các mục menu phổ biến
        retraieveAndDisplayPopularItems()
        return binding.root


    }

    private fun retraieveAndDisplayPopularItems() {
        // lấy tham chiếu tới cơ sở dữ liệu
        database = FirebaseDatabase.getInstance()
        val foodRef:DatabaseReference = database.reference.child("menu")
        menuItems = mutableListOf()
        // Truy xuất mục menu từ CSDL
        foodRef.addListenerForSingleValueEvent(object  :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(foodSnapshot in snapshot.children){
                    val menuItem = foodSnapshot.getValue(MenuItem::class.java)
                    menuItem?.let { menuItems.add(it) }
                }
                // hiển thị một mục phổ biến ngẫu nhiên
                randomPopularItems()
            }

            private fun randomPopularItems() {
                // create as shuffled list of menu items
                val index = menuItems.indices.toList().shuffled()
                val numItemToShow = 6
                val subsetMenuItems = index.take(numItemToShow).map { menuItems[it] }

                setPopularItemsAdapter(subsetMenuItems)
            }

            private fun setPopularItemsAdapter(subsetMenuItems: List<MenuItem>) {
                val adapter = MenuAdapter(subsetMenuItems, requireContext())
                binding.PopulerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.PopulerRecyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.banner1, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner2, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner3, ScaleTypes.FIT))
        // Thanh trượt
        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList)
        imageSlider.setImageList(imageList, ScaleTypes.FIT)
        imageSlider.setItemClickListener(object : ItemClickListener {
            override fun doubleClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(position: Int) {
                val itemPosition = imageList[position]
                val itemMessenger = "Selected Image $position"
                Toast.makeText(requireContext(), itemMessenger, Toast.LENGTH_SHORT).show()
            }
        })

    }

}
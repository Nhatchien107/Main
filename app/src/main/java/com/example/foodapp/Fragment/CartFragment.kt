package com.example.foodapp.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodapp.CongratsBottomSheet
import com.example.foodapp.PayOutActivity
import com.example.foodapp.R
import com.example.foodapp.adaptar.CartAdapter
import com.example.foodapp.databinding.FragmentCartBinding
import com.example.foodapp.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var foodNames: MutableList<String>
    private lateinit var foodPrices: MutableList<String>
    private lateinit var foodDescription: MutableList<String>
    private lateinit var foodImagesUri: MutableList<String>
    private lateinit var quantity: MutableList<Int>
    private lateinit var foodIngredients: MutableList<String>
    private lateinit var cartAdapter: CartAdapter
    private lateinit var userId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        retrieveCartItems()

        binding.proceedButton.setOnClickListener {
            // lấy thông tin chi tiết về đơn hàng trước khi tiến hành thanh toán
            getOrderItemDetail()

        }


        return binding.root
    }

    private fun getOrderItemDetail() {
        val orderIdReference: DatabaseReference =
            database.reference.child("user").child(userId).child("CartItems")
        val foodName = mutableListOf<String>()
        val foodPrice = mutableListOf<String>()
        val foodImage = mutableListOf<String>()
        val fooDescription = mutableListOf<String>()
        val foodIngredient = mutableListOf<String>()
        // lấy items Quantities
        val foodQuantities = cartAdapter.getUpdatedItemsQuantities()

        orderIdReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    // get the cartItems to respective List
                    val orderItems = foodSnapshot.getValue(CartItems::class.java)
                    // add items details in to list
                    orderItems?.foodName?.let { foodName.add(it) }
                    orderItems?.foodPrice?.let { foodPrice.add(it) }
                    orderItems?.foodDescription?.let { foodDescription.add(it) }
                    orderItems?.foodImage?.let { foodImage.add(it) }
                    orderItems?.foodIngredient?.let { foodIngredient.add(it) }

                }
                orderNow(
                    foodName,
                    foodPrice,
                    foodImage,
                    fooDescription,
                    foodIngredient,
                    foodQuantities
                )
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Đặt hàng không thành công. Vui lòng thử lại", Toast.LENGTH_SHORT).show()
            }

        })
    }
    private fun orderNow(
        foodName: MutableList<String>,
        foodPrice: MutableList<String>,
        foodImage: MutableList<String>,
        fooDescription: MutableList<String>,
        foodIngredient: MutableList<String>,
        foodQuantities: MutableList<Int>
    ) {
    if(isAdded && context!=null){
        val intent = Intent(requireContext(),PayOutActivity::class.java)
        intent.putExtra("FoodItemName",foodName as ArrayList<String>)
        intent.putExtra("FoodItemPrice",foodPrice as ArrayList<String>)
        intent.putExtra("FoodItemDescription",fooDescription as ArrayList<String>)
        intent.putExtra("FoodItemImage",foodImage as ArrayList<String>)
        intent.putExtra("FoodItemIngredient",foodIngredient as ArrayList<String>)
        intent.putExtra("FoodItemQuantities",foodQuantities as ArrayList<Int>)
        startActivity(intent)
    }
    }

    private fun retrieveCartItems() {
        // database reference to the Database
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid ?: ""
        val foodReference: DatabaseReference =
            database.reference.child("user").child(userId).child("CartItems")
        foodNames = mutableListOf()
        foodPrices = mutableListOf()
        foodDescription = mutableListOf()
        foodImagesUri = mutableListOf()
        foodIngredients = mutableListOf()
        quantity = mutableListOf()

        // fetch data from the database
        foodReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    // get the cartItems object from the child mode
                    val cartItems = foodSnapshot.getValue(CartItems::class.java)
                    // add cart items details to the list
                    cartItems?.foodName?.let { foodNames.add(it) }
                    cartItems?.foodPrice?.let { foodPrices.add(it) }
                    cartItems?.foodDescription?.let { foodDescription.add(it) }
                    cartItems?.foodImage?.let { foodImagesUri.add(it) }
                    cartItems?.foodQuantity?.let { quantity.add(it) }
                    cartItems?.foodIngredient?.let { foodIngredients.add(it) }

                }
                setAdapter()
            }

            private fun setAdapter() {
               cartAdapter = CartAdapter(
                    requireContext(),
                    foodNames,
                    foodPrices,
                    foodDescription,
                    foodImagesUri,
                    quantity,
                    foodIngredients
                )
                binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.cartRecyclerView.adapter = cartAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Dữ liệu không được tìm nạp", Toast.LENGTH_SHORT).show()
            }

        })


    }

    companion object {


    }
}
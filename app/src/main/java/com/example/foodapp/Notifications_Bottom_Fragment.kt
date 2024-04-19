package com.example.foodapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodapp.adaptar.NotificationAdapter
import com.example.foodapp.databinding.FragmentNotificationsBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class Notifications_Bottom_Fragment : BottomSheetDialogFragment() {
    private lateinit var binding:FragmentNotificationsBottomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationsBottomBinding.inflate(layoutInflater,container,false)
        val notifications = listOf("Đơn hàng của bạn đã được hủy thành công","Lệnh đã được thực hiện bởi người lái xe","Xin chúc mừng đơn hàng của bạn đã được đặt")
        val notificationImage = listOf(R.drawable.sademoji,R.drawable.truck,R.drawable.congrats
        )
        val adapter = NotificationAdapter(
            ArrayList(notifications),
            ArrayList(notificationImage)
        )
        binding.notificationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.notificationRecyclerView.adapter = adapter
        return binding.root
    }

    companion object {

    }
}
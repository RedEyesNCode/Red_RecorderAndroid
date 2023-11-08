package com.redeyesncode.redrecorder.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.redeyesncode.redrecorder.data.PermissionData
import com.redeyesncode.redrecorder.databinding.ItemPermissionBinding

class PermissionAdapter(var context: Context, var data :ArrayList<PermissionData>, var permission: onPermission) :
    RecyclerView.Adapter<PermissionAdapter.MyViewholder>(){

    lateinit var binding: ItemPermissionBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewholder {

        binding = ItemPermissionBinding.inflate(LayoutInflater.from(context),parent,false)



        return MyViewholder(binding)



    }

    override fun onBindViewHolder(holder: MyViewholder, position: Int) {

        binding.apply {
            val data = data[position]

            tvPermissionInfo.text = data.permissionInfo
            tvPermissionTitle.text = data.permissionName
            ivDrawable.setImageDrawable(data.drawable)

            btnCheck.setOnClickListener {
                permission.onPermissionClick(data)

            }

        }
    }

    override fun getItemCount(): Int {

        return data.size
    }
    interface onPermission{
        fun onPermissionClick(data:PermissionData)
    }
    class MyViewholder(var binding:ItemPermissionBinding): RecyclerView.ViewHolder(binding.root)
}
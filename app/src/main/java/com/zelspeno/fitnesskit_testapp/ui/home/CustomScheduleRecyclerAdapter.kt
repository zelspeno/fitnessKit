package com.zelspeno.fitnesskit_testapp.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zelspeno.fitnesskit_testapp.R

class CustomScheduleRecyclerAdapter(private var workDays: List<WorkDay>?):
    RecyclerView.Adapter<CustomScheduleRecyclerAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val headerDate: TextView = itemView.findViewById(R.id.homeScheduleDateTV)
        val childRecyclerAdapter: RecyclerView = itemView.findViewById(R.id.homeScheduleInsideRecyclerView)
        val context = itemView.context!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.home_schedule_recyclerview_item, parent, false
        )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.headerDate.text =
            "${workDays!![position].dayOfWeek}, ${workDays!![position].dateTimeString}"

        val childAdapter = CustomScheduleChildRecyclerAdapter(workDays!![position].lessonsList)
        holder.childRecyclerAdapter.layoutManager = LinearLayoutManager(
            holder.context, LinearLayoutManager.VERTICAL, false
        )
        holder.childRecyclerAdapter.adapter = childAdapter

    }

    override fun getItemCount(): Int = workDays?.size ?: 0


    fun updateList(list: MutableList<WorkDay>?) {
        this.workDays = list
        notifyDataSetChanged()
    }

    fun getList() = this.workDays

}
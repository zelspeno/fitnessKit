package com.zelspeno.fitnesskit_testapp.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zelspeno.fitnesskit_testapp.R

class CustomScheduleChildRecyclerAdapter(private var lessons: List<Lesson>?):
    RecyclerView.Adapter<CustomScheduleChildRecyclerAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val startLessonTV: TextView = itemView.findViewById(R.id.homeScheduleStartClassDateTV)
        val endLessonTV: TextView = itemView.findViewById(R.id.homeScheduleEndClassDateTV)
        val lessonName: TextView = itemView.findViewById(R.id.homeScheduleClassNameTV)
        val deltaLessonTV: TextView = itemView.findViewById(R.id.homeScheduleTotalTimeTV)
        val trainerName: TextView = itemView.findViewById(R.id.homeScheduleTrainerNameTV)
        val place: TextView = itemView.findViewById(R.id.homeScheduleLocationTV)
        val coloredLine: View = itemView.findViewById(R.id.homeScheduleStartColoredLine)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.home_schedule_inside_recyclerview_item, parent, false
        )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.startLessonTV.text = lessons?.get(position)?.startLesson ?: ""
        holder.endLessonTV.text = lessons?.get(position)?.endLesson ?: ""
        holder.lessonName.text = lessons?.get(position)?.lessonName ?: ""
        holder.deltaLessonTV.text = lessons?.get(position)?.deltaLesson ?: ""
        holder.trainerName.text = lessons?.get(position)?.trainerName ?: ""
        holder.place.text = lessons?.get(position)?.locationName ?: ""
        holder.coloredLine.setBackgroundColor(lessons?.get(position)?.lineColor ?: Color.TRANSPARENT)

    }

    override fun getItemCount(): Int = lessons?.size ?: 0


    fun updateList(list: MutableList<Lesson>?) {
        this.lessons = list
        notifyDataSetChanged()
    }

    fun getList() = this.lessons

}
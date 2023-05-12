package com.zelspeno.fitnesskit_testapp.ui.home

import android.graphics.Color

data class Lesson(
    val id: Long,
    val dateTime: String,
    val startLesson: String,
    val endLesson: String,
    val deltaLesson: String,
    val lessonName: String,
    val trainerName: String,
    val locationName: String,
    val lineColor: Int,
)

data class WorkDay(
    val dayOfWeek: String,
    val dateTimeString: String,
    val lessonsList: MutableList<Lesson>,

)
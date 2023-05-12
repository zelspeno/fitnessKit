package com.zelspeno.fitnesskit_testapp.ui

import android.content.Context
import com.zelspeno.fitnesskit_testapp.ui.home.ScheduleImplementRepository
import com.zelspeno.fitnesskit_testapp.ui.home.ScheduleRepository

object Repositories {

    private lateinit var applicationContext: Context

    val scheduleRepository: ScheduleRepository by lazy {
        ScheduleImplementRepository()
    }

    fun init(context: Context) {
        applicationContext = context
    }
}
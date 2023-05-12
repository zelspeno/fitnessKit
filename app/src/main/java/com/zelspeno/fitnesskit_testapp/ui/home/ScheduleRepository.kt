package com.zelspeno.fitnesskit_testapp.ui.home

import kotlinx.coroutines.flow.Flow


interface ScheduleRepository {

    /** Get list of Lessons in Flow */
    suspend fun getWorkDaysFromURL(urlName: String): Flow<List<WorkDay>>?


}
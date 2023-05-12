package com.zelspeno.fitnesskit_testapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

public class HomeViewModel(
    private val scheduleRepository: ScheduleRepository
    ) : ViewModel() {

    private val urlName = "https://olimpia.fitnesskit-admin.ru/schedule/get_v3/?club_id=2"

    private val _workDay = MutableSharedFlow<List<WorkDay>>(1)
    val workDay = _workDay.asSharedFlow()
    private val _listLessons = MutableSharedFlow<List<Lesson>>(1)
    val listLesson = _listLessons.asSharedFlow()

    fun getWorkDayList() {
        viewModelScope.launch {
            scheduleRepository.getWorkDaysFromURL(urlName)?.collect {
                _workDay.emit(it)
            }
        }
    }

}
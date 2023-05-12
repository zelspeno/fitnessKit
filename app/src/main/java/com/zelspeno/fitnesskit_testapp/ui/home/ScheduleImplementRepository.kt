package com.zelspeno.fitnesskit_testapp.ui.home

import android.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.net.URLConnection
import java.time.LocalDate


class ScheduleImplementRepository : ScheduleRepository {

    override suspend fun getWorkDaysFromURL(urlName: String): Flow<List<WorkDay>>? {

        val jsonData = getDataAsJSON(urlName)
        return if (jsonData != null) {
            val jsonObj = JSONObject(jsonData)
            val data = getListOfWorkDays(jsonObj)
            flowOf(data)
        } else null

    }

    private suspend fun getDataAsJSON(urlName: String): String? {

        val url = URL(urlName)
        var urlConnection: URLConnection? = null
        var inputStream: InputStream? = null
        var jsonData = ""

        try {
            withContext(Dispatchers.IO) {
                urlConnection = url.openConnection()
                urlConnection?.connect()
                inputStream = urlConnection?.getInputStream()

                val br = BufferedReader(InputStreamReader(inputStream))
                val sb = StringBuffer()
                var line = br.readLine()
                while (line != null) {
                    sb.append(line)
                    line = br.readLine()
                }
                jsonData = sb.toString()
                br.close()
                inputStream?.close()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.IO) {
                inputStream?.close()
            }
            return null
        }
        return jsonData
    }

    private fun getListOfWorkDays(jsonObj: JSONObject): MutableList<WorkDay> {
        val lessonsList = mutableListOf<Lesson>()
        val lessonsNode = jsonObj.getJSONArray("lessons")
        val data = mutableListOf<WorkDay>()

        for (i in 0 until lessonsNode.length()) {
            val startLesson = lessonsNode.getJSONObject(i).getString("startTime")
            val endLesson = lessonsNode.getJSONObject(i).getString("endTime")
            val dateTime = lessonsNode.getJSONObject(i).getString("date")

            if (i != 0) {
                val prevDateTime = lessonsNode
                    .getJSONObject(i - 1)
                    .getString("date")
                if (prevDateTime != dateTime) {
                    data.add(WorkDay(
                        dayOfWeek = getDayOfWeekByDate(prevDateTime),
                        dateTimeString = getUIDateFromDate(prevDateTime),
                        lessonsList = lessonsList
                    ))
                    lessonsList.clear()
                    lessonsList.add(addLessonToList(i, dateTime, startLesson, endLesson, lessonsNode))
                } else {
                    lessonsList.add(addLessonToList(i, dateTime, startLesson, endLesson, lessonsNode))
                }
            }
            if (i != lessonsNode.length()-1) {
                lessonsList.add(addLessonToList(i, dateTime, startLesson, endLesson, lessonsNode))
                data.add(
                    WorkDay(
                        dayOfWeek = getDayOfWeekByDate(dateTime),
                        dateTimeString = getUIDateFromDate(dateTime),
                        lessonsList = lessonsList
                    ))
            }
            else {
                lessonsList.add(addLessonToList(i, dateTime, startLesson, endLesson, lessonsNode))
            }
        }
        return data
    }

    private fun addLessonToList(i: Int,
                                dateTime: String,
                                startLesson: String,
                                endLesson: String,
                                lessonsNode: JSONArray
    ): Lesson =
        Lesson(
            id = i.toLong(),
            dateTime = dateTime,
            startLesson = startLesson,
            endLesson = endLesson,
            deltaLesson = getDeltaLesson(startLesson, endLesson),
            lessonName = lessonsNode.getJSONObject(i).getString("name"),
            trainerName = lessonsNode.getJSONObject(i).getString("coach_id"),
            locationName = lessonsNode.getJSONObject(i).getString("place"),
            lineColor = Color.parseColor(
                lessonsNode.getJSONObject(i).getString("color")
            ),
        )

    private fun getDayOfWeekByDate(date: String): String {
        val (year, month, day) = date.split("-").map { it.toInt() }
        val time = LocalDate.of(year, month, day)
        return time.dayOfWeek.name.LocaleDayOfWeekToRus()
    }

    private fun getUIDateFromDate(date: String): String {
        val (year, month, day) = date.split("-").map { it.toInt() }
        val time = LocalDate.of(year, month, day)
        val stringMonth = time.month.name.LocaleMonthToRus()
        return "$day $stringMonth"
    }


    private fun String.LocaleDayOfWeekToRus(): String {
        var result = ""
        when (this) {
            "MONDAY" -> result = "понедельник"
            "TUESDAY" -> result = "вторник"
            "WEDNESDAY" -> result = "среда"
            "THURSDAY" -> result = "четверг"
            "FRIDAY" -> result = "пятница"
            "SATURDAY" -> result = "суббота"
            "SUNDAY" -> result = "воскресенье"
        }
        return result
    }

    private fun String.LocaleMonthToRus(): String {
        var result = ""
        when (this.lowercase()) {
            "january" -> result = "Январь"
            "february" -> result =  "Февраль"
            "march" -> result = "Март"
            "april" -> result = "Апрель"
            "may" -> result = "Май"
            "june" -> result = "Июнь"
            "july" -> result = "Июль"
            "august" -> result = "Август"
            "september" -> result = "Сентябрь"
            "october" -> result = "Октябрь"
            "november" -> result = "Ноябрь"
            "december" -> result = "Декабрь"
        }
        return result
    }

    private fun getDeltaLesson(startLesson: String, endLesson: String): String {
        val (startHour, startMinute) = startLesson.split(":").map { it.toInt() }
        val (endHour, endMinute) = endLesson.split(":").map { it.toInt() }
        val totalMinutes = ((endHour * 60) + endMinute) - ((startHour * 60) + startMinute)
        val totalHours = totalMinutes/60
        return "$totalHours ч. ${totalMinutes - (totalHours * 60)}мин."
    }


}
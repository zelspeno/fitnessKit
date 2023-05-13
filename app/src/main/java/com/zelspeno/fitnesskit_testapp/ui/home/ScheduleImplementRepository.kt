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

        val lessonsNode = jsonObj.getJSONArray("lessons")
        val trainersNode = jsonObj.getJSONArray("trainers")
        val data = mutableListOf<WorkDay>()
        var workDayUnique = mutableMapOf<String, MutableList<Lesson>>()
        val trainersMap = getMapTrainers(trainersNode)

        for (i in 0 until lessonsNode.length()) {


            val dateTime = lessonsNode.getJSONObject(i).getString("date")

            if (workDayUnique.containsKey(dateTime)) {
                workDayUnique[dateTime]!!.add(
                    addLessonToList(i, dateTime, lessonsNode, trainersMap)
                )
            } else {
                workDayUnique[dateTime] = mutableListOf(
                    addLessonToList(i, dateTime, lessonsNode, trainersMap)
                )
            }

        }
        workDayUnique = workDayUnique.toSortedMap()

        for (i in workDayUnique) {
            val date = i.key
            val listLesson = i.value.sortedBy { it.startLesson }.toMutableList()
            data.add(
                WorkDay(
                    dayOfWeek = getDayOfWeekByDate(date),
                    dateTimeString = getUIDateFromDate(date),
                    lessonsList = listLesson
                )
            )
        }
        return data
    }

    private fun addLessonToList(
        i: Int,
        dateTime: String,
        lessonsNode: JSONArray,
        mapTrainers: Map<String, String>
    ): Lesson {

        val startLesson = lessonsNode.getJSONObject(i).getString("startTime")
        val endLesson = lessonsNode.getJSONObject(i).getString("endTime")
        val trainerID = lessonsNode.getJSONObject(i).getString("coach_id")
        var trainerName = ""
        if (trainerID.isNotEmpty()) {
            trainerName = mapTrainers[trainerID]!!
        }

        return Lesson(
            id = i.toLong(),
            dateTime = dateTime,
            startLesson = startLesson,
            endLesson = endLesson,
            deltaLesson = getDeltaLesson(startLesson, endLesson),
            lessonName = lessonsNode.getJSONObject(i).getString("name"),
            trainerName = trainerName,
            locationName = lessonsNode.getJSONObject(i).getString("place"),
            lineColor = Color.parseColor(
                lessonsNode.getJSONObject(i).getString("color")
            ),
        )
    }

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
            "february" -> result = "Февраль"
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
        val totalHours = totalMinutes / 60
        return "$totalHours ч. ${totalMinutes - (totalHours * 60)}мин."
    }

    private fun getMapTrainers(trainersNode: JSONArray): MutableMap<String, String> {
        val mapTrainers = mutableMapOf<String, String>()

        for (i in 0 until trainersNode.length()) {

            val id = trainersNode.getJSONObject(i).getString("id")
            val fullName = trainersNode.getJSONObject(i).getString("full_name")

            mapTrainers[id] = fullName

        }

        return mapTrainers


    }
}
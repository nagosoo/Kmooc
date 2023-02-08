package com.programmers.kmooc.repositories

import com.programmers.kmooc.models.Lecture
import com.programmers.kmooc.models.LectureList
import com.programmers.kmooc.network.HttpClient
import com.programmers.kmooc.utils.DateUtil.parseDate
import org.json.JSONObject
import java.util.*

class KmoocRepository {

    /**
     * 국가평생교육진흥원_K-MOOC_강좌정보API
     * https://www.data.go.kr/data/15042355/openapi.do
     */

    private val httpClient = HttpClient("http://apis.data.go.kr/B552881/kmooc")
    private val serviceKey =
        "iXHvekD3tu372wV0oKqmiSy87%2FcrKQVxPJ8b9AUWqhBLDh5LhXVp7DpDHY57qnYOlBi59YbY6WgB0A834Q%2By6A%3D%3D"

    fun list(completed: (LectureList) -> Unit) {
        httpClient.getJson(
            "/courseList",
            mapOf("serviceKey" to serviceKey, "Mobile" to 1)
        ) { result ->
            result.onSuccess {
                completed(parseLectureList(JSONObject(it)))
            }
        }
    }

    fun next(currentPage: LectureList, completed: (LectureList) -> Unit) {
        val nextPageUrl = currentPage.next
        httpClient.getJson(nextPageUrl, emptyMap()) { result ->
            result.onSuccess {
                completed(parseLectureList(JSONObject(it)))
            }
        }
    }

    fun detail(courseId: String, completed: (Lecture) -> Unit) {
        httpClient.getJson(
            "/courseDetail",
            mapOf("CourseId" to courseId, "serviceKey" to serviceKey)
        ) { result ->
            result.onSuccess {
                completed(parseLecture(JSONObject(it)))
            }
        }
    }

    private fun parseLectureList(jsonObject: JSONObject): LectureList {
        //TODO: JSONObject -> LectureList 를 구현하세요
        val pagination = jsonObject.getJSONObject("pagination")
        val count = pagination.getInt("count")
        val previous = pagination.getString("previous")
        val numPages = pagination.getInt("num_pages")
        val next = pagination.getString("next")
        val lecturesJsonArray = jsonObject.getJSONArray("results")
        val lectures = mutableListOf<Lecture>()
        for (i in 0 until lecturesJsonArray.length()) {
            parseLecture(lecturesJsonArray.getJSONObject(i)).apply {
                lectures.add(this)
            }
        }
        //return LectureList.EMPTY
        return LectureList(
            count = count,
            numPages = numPages,
            previous = previous,
            next = next,
            lectures = lectures
        )
    }

    private fun parseLecture(jsonObject: JSONObject): Lecture {
        //TODO: JSONObject -> Lecture 를 구현하세요
        val id = jsonObject.getString("id")
        val number = jsonObject.getString("number")
        val name = jsonObject.getString("name")
        val classfyName = jsonObject.getString("classfy_name")
        val middleClassfyName = jsonObject.getString("middle_classfy")
        val media = jsonObject.getJSONObject("media")
        val courseImage = with(media.getJSONObject("image")) {
            getString("small")
        }
        val courseImageLarge: String = with(media.getJSONObject("image")) {
            getString("large")
        }
        val shortDescription = jsonObject.getString("short_description")
        val orgName = jsonObject.getString("org_name")
        val start: Date = parseDate(jsonObject.getString("start"))
        val end: Date = parseDate(jsonObject.getString("end"))
        val teachers: String? =
            if (jsonObject.has("teachers")) jsonObject.getString("teachers") else null
        val overview: String? =
            if (jsonObject.has("overview")) jsonObject.getString("overview") else null
        //  return Lecture.EMPTY
        return Lecture(
            id = id,
            number = number,
            name = name,
            classfyName = classfyName,
            middleClassfyName = middleClassfyName,
            courseImage = courseImage,
            courseImageLarge = courseImageLarge,
            shortDescription = shortDescription,
            orgName = orgName,
            start = start,
            end = end,
            teachers = teachers,
            overview = overview,
        )
    }
}
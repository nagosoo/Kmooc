package com.programmers.kmooc.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.programmers.kmooc.models.LectureList
import com.programmers.kmooc.repositories.KmoocRepository

class KmoocListViewModel(private val repository: KmoocRepository) : ViewModel() {

    private val _lectureList = MutableLiveData<LectureList>()
    val lectureList: LiveData<LectureList> = _lectureList

    private val _progressVisible = MutableLiveData<Boolean>().apply { value = false }
    val progressVisible: LiveData<Boolean> = _progressVisible

    fun list() {
        repository.list { lectureList ->
            _lectureList.postValue(lectureList)
        }
    }

    fun next() {
        _progressVisible.value = true
        val currentLectureList = _lectureList.value ?: return
        repository.next(currentLectureList) { lectureList ->
            val mergedLectures = currentLectureList.lectures.toMutableList().apply {
                addAll(lectureList.lectures)
            }
            lectureList.lectures = mergedLectures
            _lectureList.postValue(lectureList)
            _progressVisible.postValue(false)
        }
    }

}

class KmoocListViewModelFactory(private val repository: KmoocRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KmoocListViewModel::class.java)) {
            return KmoocListViewModel(repository) as T
        }
        throw IllegalAccessException("Unkown Viewmodel Class")
    }
}
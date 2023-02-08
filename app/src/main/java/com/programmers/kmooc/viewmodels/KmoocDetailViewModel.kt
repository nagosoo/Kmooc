package com.programmers.kmooc.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.programmers.kmooc.models.Lecture
import com.programmers.kmooc.repositories.KmoocRepository


class KmoocDetailViewModel(private val repository: KmoocRepository) : ViewModel() {

    private val _lectureDetail = MutableLiveData<Lecture>()
    val lectureDetail: LiveData<Lecture> = _lectureDetail

    private val _progressVisibility = MutableLiveData<Boolean>().apply { value = false }
    val progressVisibility: LiveData<Boolean> = _progressVisibility


    fun detail(courseId: String) {
        _progressVisibility.value = true
        repository.detail(courseId) { lecture ->
            _lectureDetail.postValue(lecture)
            _progressVisibility.postValue(false)
        }
    }
}

class KmoocDetailViewModelFactory(private val repository: KmoocRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KmoocDetailViewModel::class.java)) {
            return KmoocDetailViewModel(repository) as T
        }
        throw IllegalAccessException("Unkown Viewmodel Class")
    }
}
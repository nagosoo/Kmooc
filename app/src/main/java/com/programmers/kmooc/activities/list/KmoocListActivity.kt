package com.programmers.kmooc.activities.list

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.programmers.kmooc.KmoocApplication
import com.programmers.kmooc.activities.detail.KmoocDetailActivity
import com.programmers.kmooc.databinding.ActivityKmookListBinding
import com.programmers.kmooc.models.Lecture
import com.programmers.kmooc.viewmodels.KmoocListViewModel
import com.programmers.kmooc.viewmodels.KmoocListViewModelFactory

class KmoocListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKmookListBinding
    private lateinit var viewModel: KmoocListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val kmoocRepository = (application as KmoocApplication).kmoocRepository
        viewModel = ViewModelProvider(this, KmoocListViewModelFactory(kmoocRepository)).get(
            KmoocListViewModel::class.java
        )

        binding = ActivityKmookListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = LecturesAdapter()
            .apply { onClick = this@KmoocListActivity::startDetailActivity }

        binding.lectureList.adapter = adapter
        binding.lectureList.setHasFixedSize(true)

        viewModel.list()

        viewModel.lectureList.observe(this) {
            adapter.updateLectures(it.lectures)
            binding.pullToRefresh.isRefreshing = false
        }

        viewModel.progressVisible.observe(this){
            binding.progressBar.isVisible = it
        }

        refresh()
        loadNextList()
    }

    private fun refresh() {
        binding.pullToRefresh.setOnRefreshListener {
            viewModel.list()
        }
    }

    private fun loadNextList() {
        binding.lectureList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val hasNext = viewModel.lectureList.value?.next != null
                if (!hasNext) return

                if (viewModel.progressVisible.value == false) {
                    val lm = binding.lectureList.layoutManager as LinearLayoutManager
                    val lastItemPosition = lm.findLastCompletelyVisibleItemPosition()

                    if (lm.itemCount <= lastItemPosition + 5) {
                        viewModel.next()
                    }
                }
            }
        })
    }

    private fun startDetailActivity(lecture: Lecture) {
        startActivity(
            Intent(this, KmoocDetailActivity::class.java)
                .apply { putExtra(KmoocDetailActivity.INTENT_PARAM_COURSE_ID, lecture.id) }
        )
    }
}

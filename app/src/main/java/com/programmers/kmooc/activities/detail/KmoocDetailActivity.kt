package com.programmers.kmooc.activities.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.programmers.kmooc.KmoocApplication
import com.programmers.kmooc.databinding.ActivityKmookDetailBinding
import com.programmers.kmooc.network.ImageLoader
import com.programmers.kmooc.utils.DateUtil.formatDate
import com.programmers.kmooc.viewmodels.KmoocDetailViewModel
import com.programmers.kmooc.viewmodels.KmoocDetailViewModelFactory

class KmoocDetailActivity : AppCompatActivity() {

    companion object {
        const val INTENT_PARAM_COURSE_ID = "param_course_id"
    }

    private lateinit var binding: ActivityKmookDetailBinding
    private lateinit var viewModel: KmoocDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val kmoocRepository = (application as KmoocApplication).kmoocRepository
        viewModel = ViewModelProvider(this, KmoocDetailViewModelFactory(kmoocRepository)).get(
            KmoocDetailViewModel::class.java
        )
        binding = ActivityKmookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val courseId = intent?.getStringExtra(INTENT_PARAM_COURSE_ID) ?: return

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        viewModel.detail(courseId = courseId)

        viewModel.progressVisibility.observe(this) { visibility ->
            binding.progressBar.isVisible = visibility
        }

        viewModel.lectureDetail.observe(this) { lecture ->
            ImageLoader.loadImage(lecture.courseImageLarge) {
                binding.lectureImage.setImageBitmap(it)
            }

            binding.lectureDue.setDescription(
                "강의 기한",
                "${formatDate(lecture.start)} ~ ${formatDate(lecture.end)}"
            )
            binding.lectureNumber.setDescription("강의 번호", lecture.number)
            binding.lectureOrg.setDescription("강의 주최기관", lecture.orgName)
            lecture.teachers?.let { teachers ->
                binding.lectureTeachers.setDescription("강의 교수진", teachers)
            }
            binding.lectureType.setDescription("강의 타입", lecture.classfyName)
            lecture.overview?.let {
                binding.webView.apply {
                    //settings.javaScriptEnabled = true
                    //webViewClient = WebViewClient()
                    loadData(lecture.overview, "text/html", "UTF-8")
                }
            }
        }
    }
}
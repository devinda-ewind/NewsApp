package com.ewind.newsapptest.view.main.newsview

import android.os.Bundle
import com.ewind.newsapptest.domain.model.DArticles
import com.ewind.newsapptest.databinding.ActivityNewsDetailsBinding
import com.ewind.newsapptest.util.ext.*
import com.ewind.newsapptest.view.main.base.BaseActivity

const val EXTRA_NEWS = "extra_news"

class NewsViewActivity : BaseActivity() {

    lateinit var binding: ActivityNewsDetailsBinding
    private var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.apply {
            this.setDisplayHomeAsUpEnabled(true)
            this.setDisplayShowHomeEnabled(true)
            //this.setHomeAsUpIndicator(R.drawable.ic_back)
        }

        val news = intent?.extras?.getParcelable<DArticles>(EXTRA_NEWS)
        news?.let {
            setData(it)
        }
    }

    private fun setData(news: DArticles) {
        binding.apply {
            ivImage.loadImageCenterCrop(news.urlToImage)
            tvAuthor.text = news.source?.name
            tvTitle.text = news.title
            //tvDescription.text = news.description
            tvContent.text = news.content
            news.publishedAt?.let {
                tvDate.text = it.toDate(YYYY_MM_DDThh_mm_ssZ)?.toCustomDate(DD_MMM_YYYY_H_MM_A)
            }
            url = news.url
        }
    }
}

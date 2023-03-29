package com.side.runwithme.component

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import com.google.android.material.appbar.AppBarLayout
import com.side.runwithme.R
import com.side.runwithme.databinding.RwmToolbarBinding

class CustomToolbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppBarLayout(context, attrs, defStyleAttr) {

    private var binding = RwmToolbarBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        attrs?.let {
            getAttrs(it)
        }
    }

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomToolbar)

        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        binding.apply {
            val backImage = typedArray.getResourceId(
                R.styleable.CustomToolbar_back_image,
                R.drawable.baseline_arrow_back_24
            )
            setBackImage(backImage)
            val usesOptionOne =
                typedArray.getBoolean(R.styleable.CustomToolbar_uses_option_one, false)

            if (usesOptionOne) {
                val optionImage = typedArray.getResourceId(
                    R.styleable.CustomToolbar_option_one_image,
                    R.drawable.ic_launcher_foreground
                )
                setOptionImage(optionImage, 1)
            }
            val usesOptionTwo =
                typedArray.getBoolean(R.styleable.CustomToolbar_uses_option_two, false)

            if (usesOptionTwo) {
                val optionImage = typedArray.getResourceId(
                    R.styleable.CustomToolbar_option_two_image,
                    R.drawable.ic_launcher_foreground
                )
                setOptionImage(optionImage, 2)
            }


            val title = typedArray.getString(R.styleable.CustomToolbar_title)
            setTitle(title!!)
        }

        // 데이터를 캐싱해두어 가비지컬렉션에서 제외시키도록 하는 함수
        // typedArray 사용 후 호출해야하나, 커스텀뷰가 반복 사용되는 것이 아니라면 호출하지 않아도 무방하다.
        typedArray.recycle()
    }

    fun setBackImage(image: Int) {

        binding.ivBackButton.setImageResource(image)
    }

    fun setOptionImage(image: Int, flag: Int) {
        when (flag) {
            1 -> {
                binding.ivOptionOne.setImageResource(image)
            }
            2 -> {
                binding.ivOptionTwo.setImageResource(image)
            }
        }

    }

    fun setTitle(title: String) {
        binding.tvTitle.text = title
    }

    fun setBackButtonClickEvent(onClick: () -> Unit) {
        binding.ivBackButton.setOnClickListener {
            onClick()
        }
    }

    fun setOptionButtonClickEvent(flag: Int, onClick: () -> Unit ) {

        when (flag) {
            1 -> {
                binding.ivOptionOne.setOnClickListener {
                    onClick()
                }
            }
            2 -> {
                binding.ivOptionTwo.setOnClickListener {
                    onClick()
                }
            }
        }

    }

}
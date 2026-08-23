package com.github.aakumykov.sync_dir_to_cloud.view_utils

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AutoScrollRecyclerView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.recyclerview.R.attr.recyclerViewStyle,
    private val autoScrollEnabled: Boolean = true,
    private val scrollPausingTimeoutSec: Int = DEFAULT_SCROLL_PAUSING_TIMEOUT_SEC,
    private val scrollPausingScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
)
    : RecyclerView(context, attrs, defStyleAttr)
{

    constructor(context: Context, attrs: AttributeSet? = null)
            : this(context, attrs, androidx.recyclerview.R.attr.recyclerViewStyle)

    constructor(context: Context)
            : this(context, null ,androidx.recyclerview.R.attr.recyclerViewStyle)


    private var scrollPausingJob: Job? = null
        set(value) {
            if (null == value && null != field) field = value
            else if (null != value && null == field) field = value
        }

    private var scrollPausingCounter: Int = 0

    private val shouldBeScrolled: Boolean
        get() = autoScrollEnabled && 0 == scrollPausingCounter


    fun pauseScroll(timeoutSec: Int = scrollPausingTimeoutSec) {

        scrollPausingCounter = timeoutSec

        scrollPausingJob?.cancel(CancellationException("pauseScroll called again"))

        scrollPausingJob = scrollPausingScope.launch {
            while(scrollPausingCounter > 0) {
                scrollPausingCounter--
                delay(1000L)
            }
            scrollPausingJob = null
        }
    }

    fun unPauseScroll() {
        scrollPausingJob?.cancel(CancellationException("manual scroll unpausing")).also {
            scrollPausingJob = null
        }
    }

    override fun scrollToPosition(position: Int) {
        if (shouldBeScrolled)
            super.scrollToPosition(position)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        addOnScrollListener(onScrollListener)
    }

    override fun onDetachedFromWindow() {
        removeOnScrollListener(onScrollListener)
        scrollPausingScope.cancel(CancellationException("onDetachedFromWindow"))
        super.onDetachedFromWindow()
    }

    private val onScrollListener: OnScrollListener by lazy {
        object:  OnScrollListener() {
            override fun onScrollStateChanged(
                recyclerView: RecyclerView,
                newState: Int
            ) {
                super.onScrollStateChanged(recyclerView, newState)
                when(newState) {
                    SCROLL_STATE_IDLE -> {}
                    else -> pauseScroll(scrollPausingTimeoutSec)
                }
            }
        }
    }

    companion object {
        val TAG: String = AutoScrollRecyclerView::class.java.simpleName
        const val DEFAULT_SCROLL_PAUSING_TIMEOUT_SEC: Int = 5
    }
}
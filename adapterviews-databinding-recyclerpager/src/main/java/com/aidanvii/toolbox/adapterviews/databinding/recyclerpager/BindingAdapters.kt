package com.aidanvii.toolbox.adapterviews.databinding.recyclerpager

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.viewpager.widget.ViewPager
import com.aidanvii.toolbox.adapterviews.databinding.BindableAdapterItem
import com.aidanvii.toolbox.databinding.IntBindingConsumer
import com.aidanvii.toolbox.databinding.trackInstance

@BindingAdapter(
    "binder",
    "items", requireAll = true
)
internal fun <Item : BindableAdapterItem> ViewPager._bind(
    binder: BindingRecyclerPagerBinder<Item>?,
    items: List<Item>?
) {
    trackInstance(
        newInstance = binder,
        instanceResId = R.id.list_binder,
        onDetached = { detachedBinder ->
            detachedBinder.apply {
                viewPagerState = onSaveInstanceState()
                adapter.items = emptyList()
            }
            adapter = null
        },
        onAttached = { attachedBinder ->
            attachedBinder.applicationContext = context.applicationContext
            attachedBinder.viewPagerState?.let {
                onRestoreInstanceState(it)
            }
            adapter = attachedBinder.adapter
        })

    binder?.apply {
        items?.let { adapter.items = items }
    }
}

internal var ViewPager._currentItem: Int
    @InverseBindingAdapter(attribute = "currentItem", event = "currentItemAttrChanged")
    get() = currentItem
    @BindingAdapter(value = ["currentItem"])
    set(value) {
        if (currentItem != value)
            currentItem = value
    }

@BindingAdapter(
    "onPageSelected",
    "currentItemAttrChanged", requireAll = false
)
internal fun ViewPager._bind(
    onPageSelected: IntBindingConsumer?,
    currentItemAttrChanged: InverseBindingListener?
) {
    val onPageChangedListener = if (onPageSelected != null || currentItemAttrChanged != null) {
        object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                onPageSelected?.invoke(position)
                currentItemAttrChanged?.onChange()
            }
        }
    } else null

    trackInstance(
        newInstance = onPageChangedListener,
        instanceResId = R.id.on_page_changed_listener,
        onDetached = { removeOnPageChangeListener(it) },
        onAttached = { addOnPageChangeListener(it) })
}
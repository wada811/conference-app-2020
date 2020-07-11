package io.github.droidkaigi.confsched2020.announcement.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.GroupieViewHolder
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import io.github.droidkaigi.confsched2020.announcement.R
import io.github.droidkaigi.confsched2020.announcement.databinding.FragmentAnnouncementBinding
import io.github.droidkaigi.confsched2020.announcement.ui.item.AnnouncementItem
import io.github.droidkaigi.confsched2020.announcement.ui.viewmodel.AnnouncementViewModel
import io.github.droidkaigi.confsched2020.ext.isShow
import io.github.droidkaigi.confsched2020.system.ui.viewmodel.SystemViewModel

class AnnouncementFragment : Fragment(R.layout.fragment_announcement) {

    private val announcementViewModel: AnnouncementViewModel by viewModels()
    private val systemViewModel: SystemViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAnnouncementBinding.bind(view)

        val groupAdapter = GroupAdapter<GroupieViewHolder<*>>()
        binding.announcementRecycler.run {
            val offset = resources.getDimension(R.dimen.announcement_item_offset)
            addItemDecoration(AnnouncementItemDecoration(offset))
            adapter = groupAdapter
            doOnApplyWindowInsets { recyclerView, insets, initialState ->
                // Set a bottom padding due to the system UI is enabled.
                recyclerView.updatePadding(
                    bottom = insets.systemWindowInsetBottom + initialState.paddings.bottom
                )
            }
        }

        binding.progressBar.show()
        announcementViewModel.loadLanguageSetting()
        announcementViewModel.uiModel.observe(viewLifecycleOwner) { uiModel ->
            binding.progressBar.isShow = uiModel.isLoading
            binding.emptyMessage.isVisible = uiModel.isEmpty
            groupAdapter.update(
                uiModel.announcements.map { announcement ->
                    val showEllipsis = !uiModel.expandedItemIds.contains(announcement.id)
                    AnnouncementItem(
                        announcement,
                        showEllipsis
                    ) { announcementViewModel.expandItem(announcement.id) }
                }
            )
            uiModel.error?.let {
                systemViewModel.onError(it)
            }
        }
    }

    private class AnnouncementItemDecoration(val offset: Float) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            val position = parent.getChildLayoutPosition(view)
            if (position > 0) {
                outRect.top = offset.toInt()
            }
        }
    }
}

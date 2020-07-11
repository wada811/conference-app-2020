package io.github.droidkaigi.confsched2020.sponsor.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import com.xwray.groupie.databinding.GroupieViewHolder
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import io.github.droidkaigi.confsched2020.ext.isShow
import io.github.droidkaigi.confsched2020.model.Sponsor
import io.github.droidkaigi.confsched2020.model.SponsorCategory
import io.github.droidkaigi.confsched2020.sponsor.R
import io.github.droidkaigi.confsched2020.sponsor.databinding.FragmentSponsorsBinding
import io.github.droidkaigi.confsched2020.sponsor.ui.item.CategoryHeaderItem
import io.github.droidkaigi.confsched2020.sponsor.ui.item.DividerItem
import io.github.droidkaigi.confsched2020.sponsor.ui.item.LargeSponsorItem
import io.github.droidkaigi.confsched2020.sponsor.ui.item.SponsorItem
import io.github.droidkaigi.confsched2020.sponsor.ui.viewmodel.SponsorsViewModel
import io.github.droidkaigi.confsched2020.system.ui.viewmodel.SystemViewModel

class SponsorsFragment : Fragment(R.layout.fragment_sponsors) {

    private val sponsorsViewModel: SponsorsViewModel by viewModels()
    private val systemViewModel: SystemViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSponsorsBinding.bind(view)

        val groupAdapter = GroupAdapter<GroupieViewHolder<*>>()
        groupAdapter.spanCount = 2

        binding.sponsorRecycler.layoutManager = GridLayoutManager(
            requireContext(),
            groupAdapter.spanCount
        ).apply {
            spanSizeLookup = groupAdapter.spanSizeLookup
        }
        binding.sponsorRecycler.adapter = groupAdapter
        binding.sponsorRecycler.doOnApplyWindowInsets { recyclerView, insets, initialState ->
            // Set a bottom padding due to the system UI is enabled.
            recyclerView.updatePadding(
                bottom = insets.systemWindowInsetBottom + initialState.paddings.bottom
            )
        }

        binding.progressBar.show()
        sponsorsViewModel.uiModel.observe(viewLifecycleOwner) { uiModel ->
            binding.progressBar.isShow = uiModel.isLoading
            groupAdapter.update(
                uiModel.sponsorCategories.map {
                    it.toSection()
                }
            )
            uiModel.error?.let {
                systemViewModel.onError(it)
            }
        }
    }

    private fun SponsorCategory.toSection() = Section().apply {
        setHeader(CategoryHeaderItem(category))
        addAll(
            sponsors.map { sponsor ->
                sponsor.toItem(category)
            }
        )
        setFooter(DividerItem())
        setHideWhenEmpty(true)
    }

    private fun Sponsor.toItem(category: SponsorCategory.Category): Item<*> {
        val spanSize = when (category) {
            SponsorCategory.Category.PLATINUM -> 2
            else -> 1
        }
        return when (category) {
            SponsorCategory.Category.PLATINUM,
            SponsorCategory.Category.GOLD -> {
                LargeSponsorItem(this, spanSize, viewLifecycleOwnerLiveData)
            }
            else -> {
                SponsorItem(this, spanSize, viewLifecycleOwnerLiveData)
            }
        }
    }
}

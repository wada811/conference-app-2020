package io.github.droidkaigi.confsched2020.contributor.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.google.android.material.snackbar.Snackbar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.GroupieViewHolder
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import io.github.droidkaigi.confsched2020.contributor.R
import io.github.droidkaigi.confsched2020.contributor.databinding.FragmentContributorsBinding
import io.github.droidkaigi.confsched2020.contributor.ui.item.ContributorItem
import io.github.droidkaigi.confsched2020.contributor.ui.viewmodel.ContributorsViewModel
import io.github.droidkaigi.confsched2020.ext.isShow
import io.github.droidkaigi.confsched2020.ext.stringRes
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.model.Contributor
import io.github.droidkaigi.confsched2020.ui.transition.Stagger

class ContributorsFragment : Fragment(R.layout.fragment_contributors) {

    private val contributorsViewModel: ContributorsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentContributorsBinding.bind(view)

        val groupAdapter = GroupAdapter<GroupieViewHolder<*>>()
        binding.contributorRecycler.adapter = groupAdapter
        binding.contributorRecycler.doOnApplyWindowInsets { recyclerView, insets, initialState ->
            // Set a bottom padding due to the system UI is enabled.
            recyclerView.updatePadding(
                bottom = insets.systemWindowInsetBottom + initialState.paddings.bottom
            )
        }
        // Because custom RecyclerView's animation, set custom SimpleItemAnimator implementation.
        //
        // see https://developer.android.com/reference/androidx/recyclerview/widget/SimpleItemAnimator.html#animateAdd(androidx.recyclerview.widget.RecyclerView.ViewHolder)
        // see https://github.com/android/animation-samples/blob/232709094f9c60e0ead9cf4873e0c1549a9a8505/Motion/app/src/main/java/com/example/android/motion/demo/stagger/StaggerActivity.kt#L61
        binding.contributorRecycler.itemAnimator = object : DefaultItemAnimator() {
            override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
                dispatchAddFinished(holder)
                dispatchAddStarting(holder)
                return false
            }
        }

        binding.progressBar.show()
        binding.retryButton.setOnClickListener {
            contributorsViewModel.onRetry()
        }

        // This is the transition for the stagger effect.
        val stagger = Stagger()
        contributorsViewModel.uiModel.observe(viewLifecycleOwner) { uiModel ->
            binding.progressBar.isShow = uiModel.isLoading

            // Delay the stagger effect until the list is updated.
            TransitionManager.beginDelayedTransition(binding.contributorRecycler, stagger)
            groupAdapter.update(uiModel.contributors.toItems())
            binding.retryButton.visibility =
                if (uiModel.error != null && uiModel.contributors.isEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            uiModel.error?.let {
                showErrorSnackbar(
                    binding.contributor,
                    it,
                    binding.retryButton.visibility != View.VISIBLE
                )
            }
        }
    }

    private fun List<Contributor>.toItems() =
        map {
            ContributorItem(it, viewLifecycleOwnerLiveData)
        }

    private fun showErrorSnackbar(view: View, appError: AppError, showRetryAction: Boolean) {
        Snackbar.make(
            view,
            appError.stringRes(),
            Snackbar.LENGTH_LONG
        ).apply {
            if (showRetryAction) {
                setAction(R.string.retry_label) {
                    contributorsViewModel.onRetry()
                }
            }
        }.show()
    }
}

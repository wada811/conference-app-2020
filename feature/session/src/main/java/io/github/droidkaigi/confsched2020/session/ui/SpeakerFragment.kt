package io.github.droidkaigi.confsched2020.session.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialContainerTransform
import com.wada811.dependencyproperty.DependencyModule
import com.wada811.dependencyproperty.dependencyModules
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.GroupieViewHolder
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import io.github.droidkaigi.confsched2020.ext.isShow
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.FragmentSpeakerBinding
import io.github.droidkaigi.confsched2020.session.ui.item.SpeakerDetailItem
import io.github.droidkaigi.confsched2020.session.ui.item.SpeakerSessionItem
import io.github.droidkaigi.confsched2020.session.ui.viewmodel.SpeakerViewModel

class SpeakerFragment : Fragment(R.layout.fragment_speaker) {

    private val speakerViewModel: SpeakerViewModel by viewModels()
    private val navArgs: SpeakerFragmentArgs by navArgs()

    class SpeakerFragmentArgsModule(val navArgs: SpeakerFragmentArgs) : DependencyModule

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform(requireContext()).apply {
            drawingViewId = R.id.speaker_root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dependencyModules.replaceModule(SpeakerFragmentArgsModule(navArgs))
        val binding = FragmentSpeakerBinding.bind(view)
        binding.speakerRoot.transitionName = "${navArgs.speakerId}-${navArgs.transitionNameSuffix}"
        postponeEnterTransition()
        binding.progressBar.show()

        val groupAdapter = GroupAdapter<GroupieViewHolder<*>>()
        binding.speakerRecycler.also {
            it.adapter = groupAdapter
            it.doOnApplyWindowInsets { recyclerView, insets, initialState ->
                recyclerView.updatePadding(
                    bottom = insets.systemWindowInsetBottom + initialState.paddings.bottom
                )
            }
        }

        speakerViewModel.uiModel.distinctUntilChanged()
            .observe(viewLifecycleOwner) { uiModel: SpeakerViewModel.UiModel ->
                binding.progressBar.isShow = uiModel.isLoading
                val speaker = uiModel.speaker ?: return@observe
                val sessions = uiModel.sessions.takeIf { it.isNotEmpty() } ?: return@observe

                groupAdapter.update(
                    listOf(
                        SpeakerDetailItem(
                            speaker,
                            navArgs.transitionNameSuffix,
                            navArgs.searchQuery,
                            { startPostponedEnterTransition() },
                            viewLifecycleOwnerLiveData
                        )
                    ) + sessions.map { SpeakerSessionItem(it) }
                )
            }
    }
}

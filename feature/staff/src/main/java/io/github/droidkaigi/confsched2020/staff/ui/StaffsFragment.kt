package io.github.droidkaigi.confsched2020.staff.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.dropbox.android.external.store4.MemoryPolicy
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreBuilder
import com.wada811.dependencyproperty.DependencyModule
import com.wada811.dependencyproperty.dependencyModules
import com.wada811.dependencyproperty.dependency
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.GroupieViewHolder
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import io.github.droidkaigi.confsched2020.data.api.DroidKaigiApi
import io.github.droidkaigi.confsched2020.data.api.response.StaffResponse
import io.github.droidkaigi.confsched2020.data.db.StaffDatabase
import io.github.droidkaigi.confsched2020.data.db.entity.StaffEntity
import io.github.droidkaigi.confsched2020.data.repository.di.RepositoryModule
import io.github.droidkaigi.confsched2020.ext.isShow
import io.github.droidkaigi.confsched2020.model.Staff
import io.github.droidkaigi.confsched2020.model.StaffContents
import io.github.droidkaigi.confsched2020.staff.R
import io.github.droidkaigi.confsched2020.staff.databinding.FragmentStaffsBinding
import io.github.droidkaigi.confsched2020.staff.ui.item.StaffItem
import io.github.droidkaigi.confsched2020.staff.ui.viewmodel.StaffsViewModel
import io.github.droidkaigi.confsched2020.system.ui.viewmodel.SystemViewModel
import io.github.droidkaigi.confsched2020.ui.transition.Stagger
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@FlowPreview
class StaffsFragment : Fragment(R.layout.fragment_staffs) {

    private val staffsViewModel: StaffsViewModel by viewModels()
    private val systemViewModel: SystemViewModel by activityViewModels()

    private val droidKaigiApi by dependency<RepositoryModule, DroidKaigiApi> { it.apiModule.droidKaigiApi }
    private val staffDatabase by dependency<RepositoryModule, StaffDatabase> { it.dbModule.staffDatabase }

    override fun onAttach(context: Context) {
        dependencyModules.addModule(StaffModule(droidKaigiApi, staffDatabase))
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentStaffsBinding.bind(view)

        val groupAdapter = GroupAdapter<GroupieViewHolder<*>>()
        binding.staffRecycler.adapter = groupAdapter
        binding.staffRecycler.doOnApplyWindowInsets { recyclerView, insets, initialState ->
            // Set a bottom padding due to the system UI is enabled.
            recyclerView.updatePadding(
                bottom = insets.systemWindowInsetBottom + initialState.paddings.bottom
            )
        }
        // Because custom RecyclerView's animation, set custom SimpleItemAnimator implementation.
        //
        // see https://developer.android.com/reference/androidx/recyclerview/widget/SimpleItemAnimator.html#animateAdd(androidx.recyclerview.widget.RecyclerView.ViewHolder)
        // see https://github.com/android/animation-samples/blob/232709094f9c60e0ead9cf4873e0c1549a9a8505/Motion/app/src/main/java/com/example/android/motion/demo/stagger/StaggerActivity.kt#L61
        binding.staffRecycler.itemAnimator = object : DefaultItemAnimator() {
            override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
                dispatchAddFinished(holder)
                dispatchAddStarting(holder)
                return false
            }
        }

        binding.progressBar.show()

        // This is the transition for the stagger effect.
        val stagger = Stagger()
        staffsViewModel.uiModel.observe(viewLifecycleOwner) { uiModel ->
            binding.progressBar.isShow = uiModel.isLoading

            // Delay the stagger effect until the list is updated.
            TransitionManager.beginDelayedTransition(binding.staffRecycler, stagger)
            groupAdapter.update(uiModel.staffContents.staffs.map {
                StaffItem(it, viewLifecycleOwnerLiveData)
            })

            uiModel.error?.let {
                systemViewModel.onError(it)
            }
        }
    }
}

@VisibleForTesting
fun readFromLocal(staffDatabase: StaffDatabase): Flow<StaffContents> {
    return staffDatabase
        .staffs()
        .map { StaffContents(it.map { staffEntity -> staffEntity.toStaff() }) }
}

private fun StaffEntity.toStaff(): Staff = Staff(id, name, iconUrl, profileUrl)

@FlowPreview
class StaffModule(
    api: DroidKaigiApi,
    staffDatabase: StaffDatabase
) : DependencyModule {

    val staffsContentsStore: Store<Unit, StaffContents> by lazy {
        StoreBuilder.fromNonFlow<Unit, StaffResponse> { api.getStaffs() }
            .persister(
                reader = { readFromLocal(staffDatabase) },
                writer = { _: Unit, output: StaffResponse -> staffDatabase.save(output) }
            )
            .cachePolicy(MemoryPolicy.builder().build())
            .build()
    }
}

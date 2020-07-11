package io.github.droidkaigi.confsched2020.about.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.GroupieViewHolder
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import io.github.droidkaigi.confsched2020.about.R
import io.github.droidkaigi.confsched2020.about.databinding.FragmentAboutBinding
import io.github.droidkaigi.confsched2020.about.ui.AboutFragmentDirections.Companion.actionAboutToChrome
import io.github.droidkaigi.confsched2020.about.ui.AboutFragmentDirections.Companion.actionAboutToStaffs
import io.github.droidkaigi.confsched2020.about.ui.item.AboutHeaderItem
import io.github.droidkaigi.confsched2020.about.ui.item.AboutItem
import io.github.droidkaigi.confsched2020.about.ui.item.AboutLaunchItem
import io.github.droidkaigi.confsched2020.about.ui.item.AboutTextItem
import io.github.droidkaigi.confsched2020.about.ui.viewmodel.AboutViewModel
import io.github.droidkaigi.confsched2020.system.ui.viewmodel.SystemViewModel

class AboutFragment : Fragment(R.layout.fragment_about) {

    companion object {
        const val TWITTER_URL = "https://twitter.com/DroidKaigi"
        const val YOUTUBE_URL = "https://www.youtube.com/channel/UCgK6L-PKx2OZBuhrQ6mmQZw"
        const val MEDIUM_URL = "https://medium.com/droidkaigi"
        const val PRIVACY_URL = "http://www.association.droidkaigi.jp/privacy.html"
    }

    private val aboutViewModel: AboutViewModel by viewModels()
    private val systemViewModel: SystemViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAboutBinding.bind(view)

        val groupAdapter = GroupAdapter<GroupieViewHolder<*>>()
        binding.aboutRecycler.run {
            adapter = groupAdapter
            doOnApplyWindowInsets { recyclerView, insets, initialState ->
                // Set a bottom padding due to the system UI is enabled.
                recyclerView.updatePadding(
                    bottom = insets.systemWindowInsetBottom + initialState.paddings.bottom
                )
            }
        }

        groupAdapter.update(
            listOf(
                AboutHeaderItem(
                    onClickTwitter = {
                        findNavController().navigate(actionAboutToChrome(TWITTER_URL))
                    },
                    onClickYoutube = {
                        findNavController().navigate(actionAboutToChrome(YOUTUBE_URL))
                    },
                    onClickMedium = {
                        findNavController().navigate(actionAboutToChrome(MEDIUM_URL))
                    }
                ),
                AboutLaunchItem(
                    getString(R.string.about_item_access)
                ) {
                    systemViewModel.navigateToAccessMap(requireActivity())
                },
                AboutItem(
                    getString(R.string.about_item_staff)
                ) {
                    findNavController().navigate(actionAboutToStaffs())
                },
                AboutItem(
                    getString(R.string.about_item_privacy_policy)
                ) {
                    findNavController().navigate(actionAboutToChrome(PRIVACY_URL))
                },
                AboutItem(
                    getString(R.string.about_item_licence)
                ) {
                    OssLicensesMenuActivity.setActivityTitle(
                        this.getString(R.string.licenses_label)
                    )
                    findNavController().navigate(R.id.licenses)
                },
                AboutTextItem(
                    getString(R.string.about_item_app_version),
                    "1.2.0" // TODO get app version code
                )
            )
        )
        binding.progressBar.hide()
    }
}

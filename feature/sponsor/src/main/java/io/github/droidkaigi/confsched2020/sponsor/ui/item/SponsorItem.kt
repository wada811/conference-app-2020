package io.github.droidkaigi.confsched2020.sponsor.ui.item

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.findNavController
import coil.api.load
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.model.Sponsor
import io.github.droidkaigi.confsched2020.sponsor.R
import io.github.droidkaigi.confsched2020.sponsor.databinding.ItemSponsorBinding
import io.github.droidkaigi.confsched2020.sponsor.ui.SponsorsFragmentDirections.Companion.actionSponsorsToChrome

class SponsorItem(
    private val sponsor: Sponsor,
    private val spanSize: Int,
    private val lifecycleOwnerLiveData: LiveData<LifecycleOwner>
) : BindableItem<ItemSponsorBinding>(sponsor.id.toLong()) {
    override fun getLayout(): Int = R.layout.item_sponsor

    override fun bind(viewBinding: ItemSponsorBinding, position: Int) {
        viewBinding.card.setOnClickListener {
            it.findNavController().navigate(actionSponsorsToChrome(sponsor.company.url))
        }

        viewBinding.image.load(sponsor.company.logoUrl) {
            crossfade(true)
            lifecycle(lifecycleOwnerLiveData.value)
        }
    }

    override fun hasSameContentAs(other: Item<*>): Boolean =
        sponsor == (other as? SponsorItem)?.sponsor

    override fun getSpanSize(spanCount: Int, position: Int): Int {
        return spanSize
    }
}

package io.github.droidkaigi.confsched2020.about.ui.item

import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.about.R
import io.github.droidkaigi.confsched2020.about.databinding.ItemAboutHeaderBinding

class AboutHeaderItem(
    private val onClickTwitter: () -> Unit,
    private val onClickYoutube: () -> Unit,
    private val onClickMedium: () -> Unit
) : BindableItem<ItemAboutHeaderBinding>() {
    override fun getLayout(): Int = R.layout.item_about_header

    override fun bind(viewBinding: ItemAboutHeaderBinding, position: Int) {
        viewBinding.twitterButton.setOnClickListener {
            onClickTwitter()
        }
        viewBinding.youtubeButton.setOnClickListener {
            onClickYoutube()
        }
        viewBinding.mediumButton.setOnClickListener {
            onClickMedium()
        }
    }
}

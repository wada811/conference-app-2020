package io.github.droidkaigi.confsched2020.about.ui.item

import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.about.R
import io.github.droidkaigi.confsched2020.about.databinding.ItemAboutLaunchBinding

class AboutLaunchItem(
    private val name: String,
    private val onClick: () -> Unit
) : BindableItem<ItemAboutLaunchBinding>() {
    override fun getLayout(): Int = R.layout.item_about_launch

    override fun bind(viewBinding: ItemAboutLaunchBinding, position: Int) {
        viewBinding.title.text = name
        viewBinding.root.setOnClickListener {
            onClick()
        }
    }
}

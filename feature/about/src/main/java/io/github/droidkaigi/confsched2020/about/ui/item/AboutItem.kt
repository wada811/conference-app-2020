package io.github.droidkaigi.confsched2020.about.ui.item

import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.about.R
import io.github.droidkaigi.confsched2020.about.databinding.ItemAboutBinding

class AboutItem(
    private val name: String,
    private val onClick: () -> Unit
) : BindableItem<ItemAboutBinding>() {
    override fun getLayout(): Int = R.layout.item_about

    override fun bind(viewBinding: ItemAboutBinding, position: Int) {
        viewBinding.title.text = name
        viewBinding.root.setOnClickListener {
            onClick()
        }
    }
}

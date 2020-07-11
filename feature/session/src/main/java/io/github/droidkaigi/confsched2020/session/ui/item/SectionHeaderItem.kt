package io.github.droidkaigi.confsched2020.session.ui.item

import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.ItemSectionHeaderBinding

class SectionHeaderItem(
    val title: String
) : BindableItem<ItemSectionHeaderBinding>(title.hashCode().toLong()) {
    override fun getLayout(): Int = R.layout.item_section_header

    override fun bind(viewBinding: ItemSectionHeaderBinding, position: Int) {
        viewBinding.title.text = title
    }

    override fun hasSameContentAs(other: Item<*>): Boolean =
        title == (other as? SectionHeaderItem)?.title
}

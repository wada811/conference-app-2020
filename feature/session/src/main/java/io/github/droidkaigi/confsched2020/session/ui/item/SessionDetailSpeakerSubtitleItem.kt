package io.github.droidkaigi.confsched2020.session.ui.item

import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.ItemSessionDetailSpeakerSubtitleBinding

class SessionDetailSpeakerSubtitleItem :
    BindableItem<ItemSessionDetailSpeakerSubtitleBinding>(GROUPIE_ITEM_ID) {
    override fun getLayout() = R.layout.item_session_detail_speaker_subtitle

    override fun bind(binding: ItemSessionDetailSpeakerSubtitleBinding, position: Int) {
    }

    companion object {
        private const val GROUPIE_ITEM_ID = -1L
    }
}

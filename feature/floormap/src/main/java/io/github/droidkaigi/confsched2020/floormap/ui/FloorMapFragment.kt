package io.github.droidkaigi.confsched2020.floormap.ui

import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.ImageLoader
import coil.api.load
import io.github.droidkaigi.confsched2020.floormap.R
import io.github.droidkaigi.confsched2020.floormap.databinding.FragmentFloormapBinding
import io.github.droidkaigi.confsched2020.model.Room

// TODO: Apply the floor map UI
class FloorMapFragment : Fragment(R.layout.fragment_floormap) {

    private val navArgs: FloorMapFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentFloormapBinding.bind(view)

        val mapPlaceholderDrawable = ResourcesCompat.getDrawable(
            resources,
            R.drawable.ic_floormap_placeholder,
            null
        )
        val mapImageLoader = ImageLoader(requireContext()) {
            crossfade(true)
            placeholder(mapPlaceholderDrawable)
            error(mapPlaceholderDrawable)
        }

        navArgs.room?.getRoomTypeResourceUrl()?.let { url ->
            // handle navigation from session detail page
            binding.floorMapImage.load(url, mapImageLoader) {
                lifecycle(viewLifecycleOwnerLiveData.value)
            }
        } ?: apply {
            // handle navigation from drawer menu
            binding.floorMapImage.load(MAP_URL, mapImageLoader) {
                lifecycle(viewLifecycleOwnerLiveData.value)
            }
        }
    }

    companion object {
        const val MAP_URL = "https://api.droidkaigi.jp/images/2020/map.png"
    }
}

fun Room.getRoomTypeResourceUrl(): String {
    val type = this.roomType ?: return FloorMapFragment.MAP_URL
    return when (type) { // TODO: Add pin images
        Room.RoomType.EXHIBITION -> FloorMapFragment.MAP_URL
        Room.RoomType.APP_BAR -> FloorMapFragment.MAP_URL
        Room.RoomType.BACKDROP -> FloorMapFragment.MAP_URL
        Room.RoomType.CARDS -> FloorMapFragment.MAP_URL
        Room.RoomType.DIALOGS -> FloorMapFragment.MAP_URL
        Room.RoomType.PICKERS -> FloorMapFragment.MAP_URL
        Room.RoomType.SLIDERS -> FloorMapFragment.MAP_URL
        Room.RoomType.TABS -> FloorMapFragment.MAP_URL
    }
}

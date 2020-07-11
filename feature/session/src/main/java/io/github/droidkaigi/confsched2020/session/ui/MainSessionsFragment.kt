package io.github.droidkaigi.confsched2020.session.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.soywiz.klock.DateTime
import io.github.droidkaigi.confsched2020.model.SessionPage
import io.github.droidkaigi.confsched2020.model.defaultTimeZoneOffset
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.FragmentMainSessionsBinding
import io.github.droidkaigi.confsched2020.session.ui.MainSessionsFragmentDirections.Companion.actionSessionToSearchSessions
import io.github.droidkaigi.confsched2020.session.ui.viewmodel.SessionsViewModel
import io.github.droidkaigi.confsched2020.system.ui.viewmodel.SystemViewModel

class MainSessionsFragment : Fragment(R.layout.fragment_main_sessions) {

    private val sessionsViewModel: SessionsViewModel by activityViewModels()
    private val systemViewModel: SystemViewModel by activityViewModels()

    private val args: MainSessionsFragmentArgs by lazy {
        MainSessionsFragmentArgs.fromBundle(arguments ?: Bundle())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        val binding = FragmentMainSessionsBinding.bind(view)
        setupSessionPager(binding)
    }

    private fun setupSessionPager(binding: FragmentMainSessionsBinding) {
        val tabLayoutMediator = TabLayoutMediator(
            binding.sessionsTabLayout,
            binding.sessionsViewpager
        ) { tab, position ->
            tab.text = SessionPage.pages[position].title
        }
        // TODO: apply margin design
//        binding.sessionsViewpager.pageMargin =
//            resources.getDimensionPixelSize(R.dimen.session_pager_horizontal_padding)
        binding.sessionsProgressBar.show()
        sessionsViewModel.uiModel.observe(viewLifecycleOwner) { uiModel ->
            with(binding.sessionsProgressBar) { if (uiModel.isLoading) show() else hide() }
        }
        binding.sessionsViewpager.adapter = object : FragmentStateAdapter(
            this
        ) {
            override fun getItemCount(): Int = SessionPage.pages.size

            override fun createFragment(position: Int): Fragment {
                return SessionsFragment.newInstance(
                    SessionsFragmentArgs(position)
                )
            }
        }

        binding.sessionsTabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {
                    tab?.let {
                        sessionsViewModel.onTabReselected()
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) { // no-op
                }

                override fun onTabSelected(tab: TabLayout.Tab?) { // no-op
                }
            })

        val jstNow = DateTime.now().toOffset(defaultTimeZoneOffset())
        if (jstNow.yearInt == 2020 && jstNow.month1 == 2 && jstNow.dayOfMonth == 21) {
            binding.sessionsViewpager.currentItem = 1
        }
        // Switch the tab to be displayed when an argument is specified in args
        if (args.tabIndex > 0) {
            binding.sessionsViewpager.currentItem = args.tabIndex
        }

        tabLayoutMediator.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sessions, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.session_search -> {
                findNavController().navigate(actionSessionToSearchSessions())
                return false
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

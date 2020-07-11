package io.github.droidkaigi.confsched2020.session_survey.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.wada811.dependencyproperty.DependencyModule
import com.wada811.dependencyproperty.addModule
import io.github.droidkaigi.confsched2020.session_survey.R
import io.github.droidkaigi.confsched2020.session_survey.databinding.FragmentSessionSurveyBinding
import io.github.droidkaigi.confsched2020.session_survey.ui.viewmodel.SessionSurveyViewModel
import io.github.droidkaigi.confsched2020.system.ui.viewmodel.SystemViewModel

class SessionSurveyFragment : Fragment(R.layout.fragment_session_survey) {

    private val sessionSurveyViewModel: SessionSurveyViewModel by viewModels()
    private val systemViewModel: SystemViewModel by activityViewModels()
    private val navArgs: SessionSurveyFragmentArgs by navArgs()

    class SessionSurveyFragmentArgsModule(val navArgs: SessionSurveyFragmentArgs) : DependencyModule

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addModule(SessionSurveyFragmentArgsModule(navArgs))
        val binding = FragmentSessionSurveyBinding.bind(view)
        binding.progressBar.show()

        // TODO: Add SessionSurveyUI
    }
}

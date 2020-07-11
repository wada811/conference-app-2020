package io.github.droidkaigi.confsched2020.session.ui.viewmodel

import com.jraska.livedata.test
import io.github.droidkaigi.confsched2020.data.repository.di.RepositoryModule
import io.github.droidkaigi.confsched2020.model.SessionContents
import io.github.droidkaigi.confsched2020.model.SessionId
import io.github.droidkaigi.confsched2020.model.SessionList
import io.github.droidkaigi.confsched2020.model.SpeakerId
import io.github.droidkaigi.confsched2020.model.repository.SessionRepository
import io.github.droidkaigi.confsched2020.session.ui.SpeakerFragment
import io.github.droidkaigi.confsched2020.widget.component.MockkRule
import io.github.droidkaigi.confsched2020.widget.component.TestApplication
import io.github.droidkaigi.confsched2020.widget.component.ViewModelTestRule
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class SpeakerViewModelTest {
    @get:Rule
    val viewModelTestRule = ViewModelTestRule()

    @get:Rule
    val mockkRule = MockkRule(this)

    @MockK(relaxed = true)
    lateinit var repositoryModule: RepositoryModule

    @MockK(relaxed = true)
    lateinit var sessionRepository: SessionRepository

    @MockK(relaxed = true)
    lateinit var navArgsModule: SpeakerFragment.SpeakerFragmentArgsModule

    @Test
    fun uiModel_correctly_loaded() {
        coEvery { repositoryModule.sessionRepository } returns sessionRepository
        coEvery { sessionRepository.sessionContents() } returns flowOf(Dummies.sessionContents)
        coEvery { navArgsModule.navArgs.speakerId } returns Dummies.speakers.first().id
        coEvery { navArgsModule.navArgs.searchQuery } returns null
        val speakerViewModel = SpeakerViewModel(TestApplication(repositoryModule, navArgsModule))

        val testObserver = speakerViewModel
            .uiModel
            .test()

        val valueHistory = testObserver.valueHistory()
        valueHistory[0].isLoading shouldBe true // other properties are not deterministic.
        valueHistory[1].apply {
            isLoading shouldBe false
            error shouldBe null
            speaker shouldBe Dummies.speakers.first()
            sessions shouldBe listOf(Dummies.speachSession1)
            searchQuery shouldBe null
        }
    }

    @Test
    fun uiModel_correctly_loaded_which_has_multiple_sessions() {
        val speachSession2 = Dummies.speachSession1.copy(
            id = SessionId("speech_session_id_2")
        )

        coEvery { repositoryModule.sessionRepository } returns sessionRepository
        coEvery { sessionRepository.sessionContents() } returns flowOf(
            Dummies.sessionContents.copy(
                sessions = SessionList(
                    listOf(
                        Dummies.serviceSession,
                        Dummies.speachSession1,
                        speachSession2
                    )
                )
            )
        )
        coEvery { navArgsModule.navArgs.speakerId } returns Dummies.speakers.first().id
        coEvery { navArgsModule.navArgs.searchQuery } returns null
        val speakerViewModel = SpeakerViewModel(TestApplication(repositoryModule, navArgsModule))

        val testObserver = speakerViewModel
            .uiModel
            .test()

        val valueHistory = testObserver.valueHistory()
        valueHistory[0].isLoading shouldBe true // other properties are not deterministic.
        valueHistory[1].apply {
            isLoading shouldBe false
            error shouldBe null
            speaker shouldBe Dummies.speakers.first()
            sessions shouldBe listOf(Dummies.speachSession1, speachSession2)
            searchQuery shouldBe null
        }
    }

    @Test
    fun uiModel_notFoundSpeaker() {
        coEvery { repositoryModule.sessionRepository } returns sessionRepository
        coEvery { sessionRepository.sessionContents() } returns flowOf(Dummies.sessionContents)
        coEvery { navArgsModule.navArgs.speakerId } returns SpeakerId("notExistId")
        coEvery { navArgsModule.navArgs.searchQuery } returns null
        val speakerViewModel = SpeakerViewModel(TestApplication(repositoryModule, navArgsModule))

        val testObserver = speakerViewModel
            .uiModel
            .test()

        val valueHistory = testObserver.valueHistory()
        valueHistory[0].isLoading shouldBe true // other properties are not deterministic.
        valueHistory[1].apply {
            isLoading shouldBe false
            error shouldNotBe null
            speaker shouldBe null
            sessions shouldBe listOf()
            searchQuery shouldBe null
        }
    }

    @Test
    fun uiModel_notSessionContents() {
        coEvery { repositoryModule.sessionRepository } returns sessionRepository
        coEvery { sessionRepository.sessionContents() } returns flowOf(SessionContents.EMPTY)
        coEvery { navArgsModule.navArgs.speakerId } returns SpeakerId("anyId")
        coEvery { navArgsModule.navArgs.searchQuery } returns null
        val speakerViewModel = SpeakerViewModel(TestApplication(repositoryModule, navArgsModule))

        val testObserver = speakerViewModel
            .uiModel
            .test()

        val valueHistory = testObserver.valueHistory()
        valueHistory[0].isLoading shouldBe true // other properties are not deterministic.
        valueHistory[1].apply {
            isLoading shouldBe false
            error shouldNotBe null
            speaker shouldBe null
            sessions shouldBe listOf()
            searchQuery shouldBe null
        }
    }

    @Test
    fun uiModel_from_search() {
        coEvery { repositoryModule.sessionRepository } returns sessionRepository
        coEvery { sessionRepository.sessionContents() } returns flowOf(Dummies.sessionContents)
        coEvery { navArgsModule.navArgs.speakerId } returns Dummies.speakers.first().id
        coEvery { navArgsModule.navArgs.searchQuery } returns "query"
        val speakerViewModel = SpeakerViewModel(TestApplication(repositoryModule, navArgsModule))

        val testObserver = speakerViewModel
            .uiModel
            .test()

        val valueHistory = testObserver.valueHistory()
        valueHistory[0].isLoading shouldBe true // other properties are not deterministic.
        valueHistory[1].apply {
            isLoading shouldBe false
            error shouldBe null
            speaker shouldBe Dummies.speakers.first()
            sessions shouldBe listOf(Dummies.speachSession1)
            searchQuery shouldBe "query"
        }
    }
}

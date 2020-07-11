package io.github.droidkaigi.confsched2020.session.ui.viewmodel

import com.jraska.livedata.test
import io.github.droidkaigi.confsched2020.data.repository.di.RepositoryModule
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.model.SessionContents
import io.github.droidkaigi.confsched2020.model.SessionId
import io.github.droidkaigi.confsched2020.model.repository.SessionRepository
import io.github.droidkaigi.confsched2020.session.ui.SessionDetailFragment
import io.github.droidkaigi.confsched2020.widget.component.MockkRule
import io.github.droidkaigi.confsched2020.widget.component.TestApplication
import io.github.droidkaigi.confsched2020.widget.component.ViewModelTestRule
import io.kotlintest.matchers.beOfType
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class SessionDetailViewModelTest {
    @get:Rule val viewModelTestRule = ViewModelTestRule()
    @get:Rule val mockkRule = MockkRule(this)

    @MockK(relaxed = true)
    lateinit var repositoryModule: RepositoryModule

    @MockK(relaxed = true)
    lateinit var sessionRepository: SessionRepository

    @MockK(relaxed = true)
    lateinit var navArgsModule: SessionDetailFragment.SessionDetailFragmentArgsModule

    @Test
    fun load() {
        coEvery { repositoryModule.sessionRepository } returns sessionRepository
        coEvery { sessionRepository.sessionContents() } returns flowOf(Dummies.sessionContents)
        coEvery { navArgsModule.navArgs.sessionId } returns Dummies.speachSession1.id
        coEvery { navArgsModule.navArgs.searchQuery } returns null
        val sessionDetailViewModel = SessionDetailViewModel(
            TestApplication(repositoryModule, navArgsModule)
        )

        val testObserver = sessionDetailViewModel
            .uiModel
            .test()

        val valueHistory = testObserver.valueHistory()
        // Observer does not receive UiModel.EMPTY,
        // because sessionRepository.sessionContents does not always emit
        // before UiModel is observed
        valueHistory[0].apply {
            isLoading shouldBe false
            session shouldBe Dummies.speachSession1
            showEllipsis shouldBe true
            searchQuery shouldBe null
        }
    }

    @Test
    fun load_NotFoundSession() {
        coEvery { repositoryModule.sessionRepository } returns sessionRepository
        coEvery { sessionRepository.sessionContents() } returns flowOf(SessionContents.EMPTY)
        coEvery { navArgsModule.navArgs.sessionId } returns SessionId("1")
        coEvery { navArgsModule.navArgs.searchQuery } returns null
        val sessionDetailViewModel = SessionDetailViewModel(
            TestApplication(repositoryModule, navArgsModule)
        )

        val uiModelTestObserver = sessionDetailViewModel
            .uiModel
            .test()

        val uiModelValueHistory = uiModelTestObserver.valueHistory()
        uiModelValueHistory[0].apply {
            isLoading shouldBe false
            error should beOfType<AppError.ApiException.SessionNotFoundException>()
            session shouldBe null
            showEllipsis shouldBe true
            searchQuery shouldBe null
        }
    }

    @Test
    fun favorite() {
        coEvery { repositoryModule.sessionRepository } returns sessionRepository
        coEvery { sessionRepository.sessionContents() } returns flowOf(Dummies.sessionContents)
        coEvery { sessionRepository.toggleFavorite(Dummies.speachSession1.id) } returns Unit
        coEvery { navArgsModule.navArgs.sessionId } returns Dummies.speachSession1.id
        coEvery { navArgsModule.navArgs.searchQuery } returns null
        val sessionDetailViewModel = SessionDetailViewModel(
            TestApplication(repositoryModule, navArgsModule)
        )

        val testObserver = sessionDetailViewModel
            .uiModel
            .test()
        sessionDetailViewModel.favorite(Dummies.speachSession1)

        verify { sessionDetailViewModel.favorite(Dummies.speachSession1) }
        val valueHistory = testObserver.valueHistory()
        valueHistory[0].apply {
            isLoading shouldBe false
            error shouldBe null
            session shouldBe Dummies.speachSession1
            showEllipsis shouldBe true
            searchQuery shouldBe null
        }
        valueHistory[1].apply {
            isLoading shouldBe true
            error shouldBe null
            session shouldBe Dummies.speachSession1
            showEllipsis shouldBe true
            searchQuery shouldBe null
        }
        valueHistory[2].apply {
            isLoading shouldBe false
            error shouldBe null
            session shouldBe Dummies.speachSession1
            showEllipsis shouldBe true
            searchQuery shouldBe null
        }
    }

    @Test
    fun expandDescription() {
        coEvery { repositoryModule.sessionRepository } returns sessionRepository
        coEvery { navArgsModule.navArgs.sessionId } returns Dummies.speachSession1.id
        coEvery { navArgsModule.navArgs.searchQuery } returns null
        val sessionDetailViewModel = SessionDetailViewModel(
            TestApplication(repositoryModule, navArgsModule)
        )
        val testObserver = sessionDetailViewModel
            .uiModel
            .test()

        sessionDetailViewModel.expandDescription()
        val valueHistory = testObserver.valueHistory()
        valueHistory[0] shouldBe SessionDetailViewModel.UiModel.EMPTY.copy(isLoading = true)
        valueHistory[1].apply {
            isLoading shouldBe true
            error shouldBe null
            session shouldBe null
            showEllipsis shouldBe false
            searchQuery shouldBe null
        }
    }

    @Test
    fun fromSearch() {
        coEvery { repositoryModule.sessionRepository } returns sessionRepository
        coEvery { sessionRepository.sessionContents() } returns flowOf(Dummies.sessionContents)
        coEvery { navArgsModule.navArgs.sessionId } returns Dummies.speachSession1.id
        coEvery { navArgsModule.navArgs.searchQuery } returns "query"
        val sessionDetailViewModel = SessionDetailViewModel(
            TestApplication(repositoryModule, navArgsModule)
        )

        val testObserver = sessionDetailViewModel
            .uiModel
            .test()

        val valueHistory = testObserver.valueHistory()
        valueHistory[0].apply {
            isLoading shouldBe false
            error shouldBe null
            session shouldBe Dummies.speachSession1
            showEllipsis shouldBe true
            searchQuery shouldBe "query"
        }
    }

    @Test
    fun thumbsUpCount() {
        coEvery { repositoryModule.sessionRepository } returns sessionRepository
        coEvery {
            sessionRepository.thumbsUpCounts(Dummies.speachSession1.id)
        } returns flowOf(Dummies.thumbsUpCount.total)
        coEvery { navArgsModule.navArgs.sessionId } returns Dummies.speachSession1.id
        coEvery { navArgsModule.navArgs.searchQuery } returns null
        val sessionDetailViewModel = SessionDetailViewModel(
            TestApplication(repositoryModule, navArgsModule)
        )
        val testObserver = sessionDetailViewModel
            .uiModel
            .test()

        val valueHistory = testObserver.valueHistory()
        valueHistory[0] shouldBe SessionDetailViewModel.UiModel.EMPTY.copy(isLoading = true)
        valueHistory[1].apply {
            isLoading shouldBe true
            error shouldBe null
            session shouldBe null
            showEllipsis shouldBe true
            searchQuery shouldBe null
            thumbsUpCount.total shouldBe Dummies.thumbsUpCount.total
        }
    }
}

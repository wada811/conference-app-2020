package io.github.droidkaigi.confsched2020.data.api.di

import com.wada811.dependencyproperty.DependencyModule
import io.github.droidkaigi.confsched2020.api.BuildConfig
import io.github.droidkaigi.confsched2020.data.api.DroidKaigiApi
import io.github.droidkaigi.confsched2020.data.api.GoogleFormApi
import io.github.droidkaigi.confsched2020.data.api.internal.InjectableKtorDroidKaigiApi
import io.github.droidkaigi.confsched2020.data.api.internal.InjectableKtorGoogleFormApi
import io.github.droidkaigi.confsched2020.data.api.internal.UserAgentInterceptor
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.logging.HttpLoggingInterceptor

class ApiModule {
    val droidKaigiApi: DroidKaigiApi by lazy {
        InjectableKtorDroidKaigiApi(
            httpClient(),
            apiEndpoint()
        )
    }
    val googleFormApi: GoogleFormApi by lazy {
        InjectableKtorGoogleFormApi(
            httpClient()
        )
    }

    private fun httpClient(): HttpClient {
        return HttpClient(OkHttp) {
            engine {
                if (BuildConfig.DEBUG) {
                    val loggingInterceptor = HttpLoggingInterceptor()
                    loggingInterceptor.level = HttpLoggingInterceptor.Level.HEADERS
                    addInterceptor(loggingInterceptor)
                }

                addInterceptor(UserAgentInterceptor())
            }
            install(JsonFeature) {
                serializer = KotlinxSerializer(
                    Json(
                        JsonConfiguration.Stable.copy(strictMode = false)
                    )
                )
            }
        }
    }

    private fun apiEndpoint(): String {
        return io.github.droidkaigi.confsched2020.data.api.internal.apiEndpoint()
    }
}

package io.github.droidkaigi.confsched2020.data.api.internal

import io.ktor.client.HttpClient

internal class InjectableKtorGoogleFormApi(
    httpClient: HttpClient
) : KtorGoogleFormApi(httpClient)

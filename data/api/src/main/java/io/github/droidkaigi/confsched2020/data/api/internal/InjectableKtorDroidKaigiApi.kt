package io.github.droidkaigi.confsched2020.data.api.internal

import io.ktor.client.HttpClient

internal class InjectableKtorDroidKaigiApi(
    httpClient: HttpClient,
    apiEndpoint: String
) : KtorDroidKaigiApi(httpClient, apiEndpoint, null)

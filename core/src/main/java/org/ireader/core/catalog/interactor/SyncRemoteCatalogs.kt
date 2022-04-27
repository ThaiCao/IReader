/*
 * Copyright (C) 2018 The Tachiyomi Open Source Project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.ireader.core.catalog.interactor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ireader.core_api.log.Log
import org.ireader.core.catalog.service.CatalogPreferences
import org.ireader.core.catalog.service.CatalogRemoteApi
import org.ireader.core.catalog.service.CatalogRemoteRepository
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SyncRemoteCatalogs @Inject constructor(
    private val catalogRemoteRepository: CatalogRemoteRepository,
    private val catalogRemoteApi: CatalogRemoteApi,
    private val catalogPreferences: CatalogPreferences,
) {

    suspend fun await(forceRefresh: Boolean,onError:(Throwable) -> Unit = {}): Boolean {
        val lastCheckPref = catalogPreferences.lastRemoteCheck()
        val lastCheck = lastCheckPref.get()
        val now = Calendar.getInstance().timeInMillis


        if (forceRefresh || (now - lastCheck) > TimeUnit.MINUTES.toMillis(5)) {
            try {
                withContext(Dispatchers.IO) {
                    val newCatalogs = catalogRemoteApi.fetchCatalogs()
                    catalogRemoteRepository.deleteAllRemoteCatalogs()
                    catalogRemoteRepository.insertRemoteCatalogs(newCatalogs)
                    lastCheckPref.set(Calendar.getInstance().timeInMillis)
                }
                return true
            } catch (e: Throwable) {
                onError(e)
                Log.warn(e, "Failed to fetch remote catalogs")
            }
        }

        return false
    }

}
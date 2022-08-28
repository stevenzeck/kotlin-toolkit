/*
 * Copyright 2021 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package org.readium.r2.testapp.catalogs


import androidx.lifecycle.LiveData
import org.readium.r2.testapp.db.CatalogDao
import org.readium.r2.testapp.domain.model.Catalog
import org.readium.r2.testapp.utils.Result

class CatalogRepository(private val catalogDao: CatalogDao) {

    suspend fun insertCatalog(catalog: Catalog): Long {
        return catalogDao.insertCatalog(catalog)
    }

    fun getCatalogsFromDatabase(): LiveData<List<Catalog>> = catalogDao.getCatalogModels()

    suspend fun catalogs(): Result<List<Catalog>> {
        val catalogs = catalogDao.getCatalogs()
        return if (catalogs != null) {
            Result.Success(catalogs)
        } else {
            Result.Error(IllegalArgumentException("Unable to fetch catalogs"))
        }
    }

    suspend fun getCatalog(catalogId: Long): Catalog? = catalogDao.getCatalog(catalogId)

    suspend fun deleteCatalog(id: Long) = catalogDao.deleteCatalog(id)
}
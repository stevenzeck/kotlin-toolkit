/*
 * Copyright 2021 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package org.readium.r2.testapp.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import org.readium.r2.testapp.bookshelf.BookshelfViewModel

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
class ComposeActivity : AppCompatActivity() {

    private lateinit var viewModel: BookshelfViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)

        viewModel =
            ViewModelProvider(this).get(BookshelfViewModel::class.java)

        intent.data?.let {
            viewModel.importPublicationFromUri(it)
        }

        setContent {
            ReadiumTestApp()
        }
    }
}
/*
 * Copyright 2024 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package org.readium.demo.navigator.reader

import org.readium.navigator.common.ExportableLocation
import org.readium.navigator.common.GoLocation
import org.readium.navigator.common.NavigationController
import org.readium.navigator.common.RenditionState
import org.readium.navigator.common.Selection
import org.readium.navigator.common.SelectionController
import org.readium.navigator.common.SelectionLocation
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.util.AbsoluteUrl
import org.readium.r2.shared.util.Url
import org.readium.r2.shared.util.mediatype.MediaType

@OptIn(ExperimentalReadiumApi::class)
class EmptyLocation : ExportableLocation {
    override val href: Url = AbsoluteUrl("https://readium.org")!!
    override fun toLocator(): Locator = Locator(href = href, mediaType = MediaType.PDF)
}

@OptIn(ExperimentalReadiumApi::class)
class EmptyGoLocation : GoLocation {
    override val href: Url = AbsoluteUrl("https://readium.org")!!
}

@OptIn(ExperimentalReadiumApi::class)
class EmptySelectionLocation : SelectionLocation {
    override val href: Url = AbsoluteUrl("https://readium.org")!!
    override fun toLocator(): Locator = Locator(href = href, mediaType = MediaType.PDF)
}

@OptIn(ExperimentalReadiumApi::class)
class EmptyController : NavigationController<EmptyLocation, EmptyGoLocation>, SelectionController<EmptySelectionLocation> {
    override val location: EmptyLocation = EmptyLocation()
    override suspend fun goTo(location: EmptyGoLocation) {}
    override suspend fun goTo(location: EmptyLocation) {}
    override suspend fun goTo(url: Url) {}
    override fun clearSelection() {}
    override suspend fun currentSelection(): Selection<EmptySelectionLocation>? = null
}

@OptIn(ExperimentalReadiumApi::class)
class EmptyRenditionState : RenditionState<EmptyController> {
    override val controller: EmptyController = EmptyController()
}

/*
 * Module: r2-streamer-kotlin
 * Developers: Quentin Gliosca
 *
 * Copyright (c) 2018. Readium Foundation. All rights reserved.
 * Use of this source code is governed by a BSD-style license which is detailed in the
 * LICENSE file present in the project repository where this source code is maintained.
 */

@file:OptIn(InternalReadiumApi::class)

package org.readium.r2.streamer.parser.epub

import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.readium.r2.shared.InternalReadiumApi
import org.readium.r2.shared.publication.Href
import org.readium.r2.shared.publication.Link
import org.readium.r2.shared.util.Url
import org.readium.r2.shared.util.xml.XmlParser
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NavigationDocumentParserTest {
    private fun parseNavigationDocument(path: String): Map<String, List<Link>> {
        val res = NavigationDocumentParser::class.java.getResourceAsStream(path)
        checkNotNull(res)
        val document = XmlParser().parse(res)
        val navigationDocument = NavigationDocumentParser.parse(
            document,
            Url("OEBPS/xhtml/nav.xhtml")!!
        )
        assertNotNull(navigationDocument)
        return navigationDocument
    }

    private val navComplex = parseNavigationDocument("navigation/nav-complex.xhtml")
    private val navTitles = parseNavigationDocument("navigation/nav-titles.xhtml")
    private val navSection = parseNavigationDocument("navigation/nav-section.xhtml")
    private val navChildren = parseNavigationDocument("navigation/nav-children.xhtml")
    private val navEmpty = parseNavigationDocument("navigation/nav-empty.xhtml")

    @Test
    fun `nav can be a non-direct descendant of body`() {
        assertThat(navSection["toc"]).containsExactly(
            Link(title = "Chapter 1", href = Href("OEBPS/xhtml/chapter1.xhtml")!!)
        )
    }

    @Test
    fun `Newlines are trimmed from title`() {
        assertThat(navTitles["toc"]).contains(
            Link(
                title = "A link with new lines splitting the text",
                href = Href("OEBPS/xhtml/chapter1.xhtml")!!
            )
        )
    }

    @Test
    fun `Spaces are trimmed from title`() {
        assertThat(navTitles["toc"]).contains(
            Link(
                title = "A link with ignorable spaces",
                href = Href("OEBPS/xhtml/chapter2.xhtml")!!
            )
        )
    }

    @Test
    fun `Nested HTML elements are allowed in titles`() {
        assertThat(navTitles["toc"]).contains(
            Link(
                title = "A link with nested HTML elements",
                href = Href("OEBPS/xhtml/chapter3.xhtml")!!
            )
        )
    }

    @Test
    fun `Entries with a zero-length title and no children are ignored`() {
        assertThat(navTitles["toc"]).doesNotContain(
            Link(title = "", href = Href("OEBPS/xhtml/chapter4.xhtml")!!)
        )
    }

    @Test
    fun `Unlinked entries without children are ignored`() {
        assertThat(navTitles["toc"]).doesNotContain(
            Link(
                title = "An unlinked element without children must be ignored",
                href = Href("#")!!
            )
        )
    }

    @Test
    fun `Hierarchical items are allowed`() {
        assertThat(navChildren["toc"]).containsExactly(
            Link(title = "Introduction", href = Href("OEBPS/xhtml/introduction.xhtml")!!),
            Link(
                title = "Part I",
                href = Href("#")!!,
                children = listOf(
                    Link(title = "Chapter 1", href = Href("OEBPS/xhtml/part1/chapter1.xhtml")!!),
                    Link(title = "Chapter 2", href = Href("OEBPS/xhtml/part1/chapter2.xhtml")!!)
                )
            ),
            Link(
                title = "Part II",
                href = Href("OEBPS/xhtml/part2/chapter1.xhtml")!!,
                children = listOf(
                    Link(title = "Chapter 1", href = Href("OEBPS/xhtml/part2/chapter1.xhtml")!!),
                    Link(title = "Chapter 2", href = Href("OEBPS/xhtml/part2/chapter2.xhtml")!!)
                )
            )
        )
    }

    @Test
    fun `Fake Navigation Document is accepted`() {
        assertThat(navEmpty["toc"]).isNull()
    }

    @Test
    fun `toc is rightly parsed`() {
        assertThat(navComplex["toc"]).containsExactly(
            Link(title = "Chapter 1", href = Href("OEBPS/xhtml/chapter1.xhtml")!!),
            Link(title = "Chapter 2", href = Href("OEBPS/xhtml/chapter2.xhtml")!!)
        )
    }

    @Test
    fun `landmarks are rightly parsed`() {
        assertThat(navComplex["landmarks"]).containsExactly(
            Link(title = "Table of Contents", href = Href("OEBPS/xhtml/nav.xhtml#toc")!!, rels = setOf("http://idpf.org/epub/vocab/structure/#toc")),
            Link(title = "Begin Reading", href = Href("OEBPS/xhtml/chapter1.xhtml")!!, rels = setOf("http://idpf.org/epub/vocab/structure/#bodymatter"))
        )
    }

    @Test
    fun `page-list is rightly parsed`() {
        assertThat(navComplex["page-list"]).containsExactly(
            Link(title = "1", href = Href("OEBPS/xhtml/chapter1.xhtml#page1")!!),
            Link(title = "2", href = Href("OEBPS/xhtml/chapter1.xhtml#page2")!!)
        )
    }
}

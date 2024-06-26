/*
 * Copyright 2020 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package org.readium.r2.lcp

import java.net.URL
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.joda.time.DateTime
import org.readium.r2.lcp.license.model.LicenseDocument
import org.readium.r2.lcp.license.model.StatusDocument
import org.readium.r2.shared.publication.services.ContentProtectionService
import org.readium.r2.shared.util.Closeable
import org.readium.r2.shared.util.Instant
import org.readium.r2.shared.util.Try
import org.readium.r2.shared.util.Url
import org.readium.r2.shared.util.toDebugDescription
import timber.log.Timber

/**
 * Opened license, used to decipher a protected publication and manage its license.
 */
public interface LcpLicense : ContentProtectionService.UserRights, Closeable {

    /**
     * License Document information.
     * https://readium.org/lcp-specs/releases/lcp/latest.html
     */
    public val license: LicenseDocument

    /**
     * License Status Document information.
     * https://readium.org/lcp-specs/releases/lsd/latest.html
     */
    public val status: StatusDocument?

    /**
     * Number of remaining characters allowed to be copied by the user. If null, there's no limit.
     */
    public val charactersToCopyLeft: StateFlow<Int?>

    /**
     * Number of pages allowed to be printed by the user. If null, there's no limit.
     */
    public val pagesToPrintLeft: StateFlow<Int?>

    /**
     * Can the user renew the loaned publication?
     */
    public val canRenewLoan: Boolean

    /**
     * The maximum potential date to renew to.
     * If null, then the renew date might not be customizable.
     */
    public val maxRenewDate: Instant?

    /**
     * Renews the loan by starting a renew LSD interaction.
     *
     * @param prefersWebPage Indicates whether the loan should be renewed through a web page if
     *        available, instead of programmatically.
     */
    public suspend fun renewLoan(listener: RenewListener, prefersWebPage: Boolean = false): Try<Instant?, LcpError>

    /**
     * Can the user return the loaned publication?
     */
    public val canReturnPublication: Boolean

    /**
     * Returns the publication to its provider.
     */
    public suspend fun returnPublication(): Try<Unit, LcpError>

    /**
     * Decrypts the given [data] encrypted with the license's content key.
     */
    public suspend fun decrypt(data: ByteArray): Try<ByteArray, LcpError>

    /**
     * UX delegate for the loan renew LSD interaction.
     *
     * If your application fits Material Design guidelines, take a look at [MaterialRenewListener]
     * for a default implementation.
     */
    public interface RenewListener {

        /**
         * Called when the renew interaction allows to customize the end date programmatically.
         * You can prompt the user for the number of days to renew, for example.
         *
         * The returned date can't exceed [maximumDate].
         */
        public suspend fun preferredEndDate(maximumDate: Instant?): Instant?

        /**
         * Called when the renew interaction uses an HTML web page.
         *
         * You should present the URL in a Chrome Custom Tab and terminate the function when the
         * web page is dismissed by the user.
         */
        public suspend fun openWebPage(url: Url)
    }

    @Deprecated(
        "Use `license.encryption.profile` instead",
        ReplaceWith("license.encryption.profile"),
        level = DeprecationLevel.ERROR
    )
    public val encryptionProfile: String? get() =
        license.encryption.profile

    @Deprecated(
        "Use `decrypt()` with coroutines instead",
        ReplaceWith("decrypt(data)"),
        level = DeprecationLevel.ERROR
    )
    public fun decipher(data: ByteArray): ByteArray? =
        runBlocking { decrypt(data) }
            .onFailure { Timber.e(it.toDebugDescription()) }
            .getOrNull()

    @Deprecated(
        "Use `renewLoan` with `RenewListener` instead",
        ReplaceWith("renewLoan(LcpLicense.RenewListener)"),
        level = DeprecationLevel.ERROR
    )
    public suspend fun renewLoan(end: DateTime?, urlPresenter: suspend (URL) -> Unit): Try<Unit, LcpError> = Try.success(
        Unit
    )

    @Deprecated(
        "Use `renewLoan` with `RenewListener` instead",
        ReplaceWith("renewLoan(LcpLicense.RenewListener)"),
        level = DeprecationLevel.ERROR
    )
    public fun renewLoan(
        end: DateTime?,
        present: (URL, dismissed: () -> Unit) -> Unit,
        completion: (LcpError?) -> Unit
    ) {}

    @Deprecated(
        "Use `returnPublication()` with coroutines instead",
        ReplaceWith("returnPublication"),
        level = DeprecationLevel.ERROR
    )
    @DelicateCoroutinesApi
    public fun returnPublication(completion: (LcpError?) -> Unit) {
        GlobalScope.launch {
            completion(returnPublication().failureOrNull())
        }
    }
}

@Deprecated(
    "Renamed to `LcpService`",
    replaceWith = ReplaceWith("LcpService"),
    level = DeprecationLevel.ERROR
)
public typealias LCPService = LcpService

@Deprecated(
    "Renamed to `LcpLicense`",
    replaceWith = ReplaceWith("LcpLicense"),
    level = DeprecationLevel.ERROR
)
public typealias LCPLicense = LcpLicense

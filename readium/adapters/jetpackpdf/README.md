# Readium Adapter for [Jetpack PDF](https://developer.android.com/jetpack/androidx/releases/pdf)

[Jetpack PDF](https://developer.android.com/jetpack/androidx/releases/pdf) is Google's official
Android library for viewing PDF documents. This adapter provides an implementation of Readium's
`PdfDocument` for parsing a PDF publication, and of `PdfDocumentFragment` to render a PDF with
`PdfNavigatorFragment` using the system's native PDF viewer capabilities.

Jetpack PDF is a modern, performant, and open-source alternative to PdfiumAndroid, but it has
specific requirements:

* **Minimum API:** Requires Android 12 (API 31) with SDK Extensions or Android 13 (API 33)+.
* **Alpha Status:** The library is currently in Alpha. APIs and behavior are subject to change.
* **Local Files Only:** The underlying `PdfViewerFragment` currently requires a local `file://` or
  `content://` URI. Remote HTTP streaming is not yet supported by the viewer. If you need to display
  a PDF from a URL, you must download it to a local file first.

## Setup

Follow the same setup instructions as the core Readium toolkit, then add this new dependency in your
app's `build.gradle.kts`.

```kotlin
dependencies {
    implementation("org.readium.kotlin-toolkit:readium-adapter-jetpackpdf:$readium_version")
    // Or, if you need only the parser but not the navigator:
    implementation("org.readium.kotlin-toolkit:readium-adapter-jetpackpdf-document:$readium_version")
}
```

### Handling Manifest Merge Errors

If your app supports Android versions lower than 12 (API 31), you will encounter a Manifest merger
failed error because androidx.pdf requires minSdkVersion 31.

To fix this, you must explicitly override the library requirement in your AndroidManifest.xml to
force the build system to merge the library:

```xml

<manifest xmlns:tools="[http://schemas.android.com/tools](http://schemas.android.com/tools)">
    <uses-sdk
        tools:overrideLibrary="androidx.pdf, androidx.pdf.compose, androidx.pdf.viewer.fragment, androidx.pdf.viewer, androidx.pdf.document, androidx.pdf.document.service" />

</manifest>
```

## Parse a PDF into a Readium Publication

To open a PDF publication with Jetpack PDF, initialize the Streamer with the adapter factory:

```kotlin
val streamer = Streamer(
    context,
    pdfFactory = JetpackPdfDocumentFactory(context)
)

val publication = streamer.open(FileAsset(pdfFile)).getOrThrow()
```

## Render a PDF with Readium's PdfNavigatorFragment

To render the PDF using Readium's `PdfNavigatorFragment`, instantiate `JetpackPdfEngineProvider` and
use the `PdfNavigatorFactory`.

**Important**: This adapter relies on Android SDK Extensions that may not be present on all
devices (
even those running Android 12+). You must check for support before instantiating the engine
provider using the provided `isSupported()` function.`

**Note**: Because `androidx.pdf` strictly requires absolute URIs, you may need to provide the
absolute
file path to the engine provider if your Publication uses relative paths internally.

```kotlin
if (JetpackPdfEngineProvider.isSupported()) {

    val pdfEngine = JetpackPdfEngineProvider(
        // 2. Required: Provide the explicit absolute URI to the file on disk
        dataSource = Uri.fromFile(File(bookPath))
    )

    val navigatorFactory = PdfNavigatorFactory(
        publication = publication,
        pdfEngineProvider = pdfEngine
    )
} else {
    // Fallback to another adapter (e.g. Pdfium) or show an error
}
```

## Feature Support

The following table summarizes the support for Readium PDF APIs with this adapter.

| Feature              | Supported | Notes                                                                                                                |
|:---------------------|:---------:|:---------------------------------------------------------------------------------------------------------------------|
| **Rendering**        |     ✅     | Uses the native `PdfViewerFragment`.                                                                                 |
| **Styling**          |     ⏳     | In progress for what is supported by `PdfStylingOptions` (mapped to Readium Settings).                               |
| **Cover Generation** |     ✅     | Generates bitmaps using `PdfRenderer`.                                                                               |
| **Metadata**         |     ❌     | `PdfRenderer` cannot extract metadata.                                                                               |
| **Search**           |    ❌️     | No API to control search programmatically.                                                                           |
| **Highlights**       |     ❌     | The `Highlight` and `SelectionMenuItemPreparer` classes exist, but the Fragment lacks the API to save/load them yet. |
| **Selection**        |    ❌️     | Text selection works natively, but we cannot retrieve the selected text/coordinates yet.                             |
| **Remote Streaming** |     ❌     | Requires downloading to a local file first.                                                                          |

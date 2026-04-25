# Readium Adapter for [Jetpack PDF](https://developer.android.com/jetpack/androidx/releases/pdf)

[Jetpack PDF](https://developer.android.com/jetpack/androidx/releases/pdf) is Google's official
Android library for viewing PDF documents. This adapter provides an implementation of Readium's
`PdfDocument` for parsing a PDF publication, and `PdfEngineProvider` implementations to render a PDF
with either `PdfNavigatorFragment` (Views) or `PdfNavigator` (Compose).

This adapter utilizes `androidx.pdf` for rendering and the native `PdfRenderer` for
metadata and covers.

* **Minimum API:** Requires **Android 8.0 (API 26)** or higher.
* **Alpha Status:** The underlying Jetpack PDF library is currently in Alpha. APIs and behavior are
  subject to change.
* **Universal Access:** Unlike the platform PDF viewer, this adapter uses a `ProxyFileDescriptor`.
  This allows it to read from any Readium `Resource`, enabling support for encrypted assets (DRM),
  assets inside generic Zip archives, and cached remote content, without requiring a strictly local
  file URI.

## Setup

Follow the same setup instructions as the core Readium toolkit, then add this dependency in your
app's `build.gradle.kts`.

```kotlin
dependencies {
    // Basic support for parsing PDF publications
    implementation("org.readium.kotlin-toolkit:readium-adapter-jetpackpdf-document:$readium_version")

    // To render with Fragments (Views)
    implementation("org.readium.kotlin-toolkit:readium-adapter-jetpackpdf-navigator-fragment:$readium_version")

    // To render with Jetpack Compose
    implementation("org.readium.kotlin-toolkit:readium-adapter-jetpackpdf-navigator-compose:$readium_version")

    // Or the umbrella dependency which includes everything
    implementation("org.readium.kotlin-toolkit:readium-adapter-jetpackpdf:$readium_version")
}
```

### Handling Manifest Merge Errors

If your app supports Android versions lower than 12 (API 31), you will encounter a Manifest merger
failed error because `androidx.pdf` requires `minSdkVersion 31`.

To fix this, you must explicitly override the library requirement in your `AndroidManifest.xml` to
force the build system to merge the library:

```xml

<manifest xmlns:tools="http://schemas.android.com/tools">
    <uses-sdk
        tools:overrideLibrary="androidx.pdf, androidx.pdf.compose, androidx.pdf.viewer.fragment, androidx.pdf.viewer, androidx.pdf.document, androidx.pdf.document.service" />
</manifest>
```

## Parse a PDF into a Readium Publication

To open a PDF publication with Jetpack PDF, initialize the `Streamer` with the adapter factory:

```kotlin
val streamer = Streamer(
    context,
    pdfFactory = JetpackPdfDocumentFactory(context)
)

val publication = streamer.open(asset).getOrThrow()
```

## Render a PDF with Readium's PdfNavigator

Both Fragment and Compose implementations use a `JetpackPdfEngineProvider` to interface with the
navigator.

**Note**: This adapter requires Android 8.0+ (API 26). You must check for support before
instantiating the engine provider using the provided `isSupported()` function.

### Jetpack Compose

Use the `JetpackPdfEngineProvider` from the `navigator-compose` module.

```kotlin
if (JetpackPdfEngineProvider.isSupported()) {
    val pdfEngine = JetpackPdfEngineProvider(
        defaults = JetpackPdfDefaults()
    )

    PdfNavigator(
        publication = publication,
        initialLocation = initialLocation,
        pdfEngineProvider = pdfEngine
    )
}
```

### Fragments (Views)

Use the `JetpackPdfEngineProvider` from the `navigator-fragment` module with `PdfNavigatorFragment`.

```kotlin
if (JetpackPdfEngineProvider.isSupported()) {
    val pdfEngine = JetpackPdfEngineProvider(
        defaults = JetpackPdfDefaults()
    )

    val navigatorFactory = PdfNavigatorFactory(
        publication = publication,
        pdfEngineProvider = pdfEngine
    )

    val fragment = navigatorFactory.createFragment(initialLocation)
}
```

## Feature Support

The following table summarizes the support for Readium PDF APIs with this adapter.

| Feature              | Supported | Notes                                                                                                  |
|:---------------------|:---------:|:-------------------------------------------------------------------------------------------------------|
| **Rendering**        |     ✅     | Uses the Jetpack `PdfView`.                                                                            |
| **Styling**          |     ⏳     | In progress for what is supported by `PdfStylingOptions`.                                              |
| **Cover Generation** |     ✅     | Generates bitmaps using the native `PdfRenderer`.                                                      |
| **Metadata**         |     ❌     | `PdfRenderer` cannot extract metadata (Title/Author).                                                  |
| **Search**           |     ❌     | No API to control search programmatically yet.                                                         |
| **Highlights**       |     ❌     | Not yet supported by the adapter.                                                                      |
| **Selection**        |     ❌     | Text selection works natively within the view, but programmatic retrieval is not supported.            |
| **Streaming/DRM**    |     ✅     | Uses `ProxyFileDescriptor` to stream content from any Readium `Resource` (including encrypted/remote). |

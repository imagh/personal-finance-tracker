package dev.imagh.pft.app

import android.content.pm.ApplicationInfo
import android.content.pm.ComponentInfo
import android.content.pm.PackageManager
import android.view.WindowManager
import androidx.test.core.app.ApplicationProvider
import dev.imagh.pft.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.w3c.dom.Element
import org.xmlpull.v1.XmlPullParser
import java.io.File
import java.util.Properties
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Pins the security posture of the merged manifest (SEC-2, SEC-3, SEC-6, plan §8):
 * no backup, no permissions, nothing exported except the launcher, and FLAG_SECURE on the
 * single activity. Runs against the real merged manifest through Robolectric (JUnit 4 runner,
 * executed on the JUnit Vintage engine).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ManifestHardeningTest {
    private val context = ApplicationProvider.getApplicationContext<android.app.Application>()
    private val packageName = context.packageName

    /**
     * Reads an `android:` attribute of `<application>` from the merged manifest that AGP records
     * for local tests. Robolectric's ApplicationInfo does not expose `dataExtractionRules`.
     */
    private fun mergedManifestApplicationAttribute(name: String): String {
        val config =
            Properties().apply {
                val resource = "com/android/tools/test_config.properties"
                checkNotNull(ManifestHardeningTest::class.java.classLoader?.getResourceAsStream(resource)) {
                    "AGP test_config.properties missing; is isIncludeAndroidResources enabled?"
                }.use { load(it) }
            }
        val manifest = File(config.getProperty("android_merged_manifest"))
        val document =
            DocumentBuilderFactory
                .newInstance()
                .apply { isNamespaceAware = true }
                .newDocumentBuilder()
                .parse(manifest)
        val application = document.getElementsByTagName("application").item(0) as Element
        return application.getAttributeNS("http://schemas.android.com/apk/res/android", name)
    }

    @Test
    fun `backup is disabled`() {
        val flags = context.applicationInfo.flags
        assertEquals("allowBackup must be off", 0, flags and ApplicationInfo.FLAG_ALLOW_BACKUP)
    }

    @Test
    fun `data extraction rules exclude every domain from cloud backup and device transfer`() {
        assertEquals(
            "manifest must reference the rules file",
            "@xml/data_extraction_rules",
            mergedManifestApplicationAttribute("dataExtractionRules"),
        )

        val parser = context.resources.getXml(R.xml.data_extraction_rules)
        val excluded = mutableMapOf<String, MutableSet<String>>()
        var section = ""
        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            if (event == XmlPullParser.START_TAG) {
                when (parser.name) {
                    "cloud-backup", "device-transfer" -> section = parser.name
                    "exclude" ->
                        excluded
                            .getOrPut(section) { mutableSetOf() }
                            .add(parser.getAttributeValue(null, "domain"))
                    "include" -> throw AssertionError("rules must not include anything: $section")
                }
            }
            event = parser.next()
        }
        val all =
            setOf(
                "root",
                "file",
                "database",
                "sharedpref",
                "external",
                "device_root",
                "device_file",
                "device_database",
                "device_sharedpref",
            )
        assertEquals(all, excluded["cloud-backup"])
        assertEquals(all, excluded["device-transfer"])
    }

    @Test
    fun `merged manifest requests no permissions`() {
        val info = context.packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
        val requested = info.requestedPermissions?.toList().orEmpty()
        assertTrue("unexpected permissions: $requested", requested.isEmpty())
    }

    @Test
    fun `only the launcher activity is exported`() {
        val flags =
            PackageManager.GET_ACTIVITIES or PackageManager.GET_RECEIVERS or
                PackageManager.GET_SERVICES or PackageManager.GET_PROVIDERS
        val info = context.packageManager.getPackageInfo(packageName, flags)
        val components =
            buildList<ComponentInfo> {
                info.activities?.let(::addAll)
                info.receivers?.let(::addAll)
                info.services?.let(::addAll)
                info.providers?.let(::addAll)
            }
        val exported = components.filter { it.exported }.map { it.name }
        assertEquals(listOf(MainActivity::class.java.name), exported)
    }

    @Test
    fun `main activity window is FLAG_SECURE`() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val flags = activity.window.attributes.flags
        assertTrue(
            "FLAG_SECURE must be set on the single activity",
            flags and WindowManager.LayoutParams.FLAG_SECURE != 0,
        )
    }
}

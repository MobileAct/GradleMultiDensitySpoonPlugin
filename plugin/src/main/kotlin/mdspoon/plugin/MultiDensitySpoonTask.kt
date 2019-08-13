package mdspoon.plugin

import com.android.build.gradle.AppExtension
import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.CollectingOutputReceiver
import com.android.ddmlib.IDevice
import com.jaredsburrows.spoon.SpoonTask
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.concurrent.TimeUnit

open class MultiDensitySpoonTask : DefaultTask() {

    private lateinit var spoonTask: SpoonTask

    private lateinit var multiDensitySpoonExtension: MultiDensitySpoonExtension
    private lateinit var appExtension: AppExtension

    fun setup(
        spoonTask: SpoonTask,
        multiDensitySpoonExtension: MultiDensitySpoonExtension,
        appExtension: AppExtension
    ) {
        this.spoonTask = spoonTask
        this.multiDensitySpoonExtension = multiDensitySpoonExtension
        this.appExtension = appExtension
    }

    @Suppress("unused")
    @TaskAction
    fun mdspoonTask() {
        val spoonOutputDirectory = spoonTask.outputDir
        val spoonTitle = spoonTask.extension.title
        val androidDebugBridge = createAndroidDebugBridge()

        val defaultDensities = mutableMapOf<IDevice, Int>()

        for (device in androidDebugBridge.devices) {
            val outputReceiver = CollectingOutputReceiver()
            device.executeShellCommand("wm density", outputReceiver)
            val defaultDensity = parsePhysicalDensity(outputReceiver.output)
            defaultDensities[device] = defaultDensity
        }

        val outputDirectories = mutableMapOf<File, Int>()

        for (density in multiDensitySpoonExtension.densities) {
            spoonTask.outputDir = File(spoonOutputDirectory, density.toString())
            spoonTask.extension.title = "$spoonTitle${multiDensitySpoonExtension.titleDelimiter}$density"

            for (device in androidDebugBridge.devices) {
                device.executeShellCommand("wm density $density", CollectingOutputReceiver())
            }

            spoonTask.spoonTask()

            outputDirectories[spoonTask.outputDir] = density
        }

        for ((device, defaultDensity) in defaultDensities) {
            device.executeShellCommand("wm density $defaultDensity", CollectingOutputReceiver())
        }

        outputMergeDirectory(outputDirectories)
    }

    private fun outputMergeDirectory(outputDirectories: Map<File, Int>) {
        val parentDirectory = outputDirectories.keys.first().parentFile
        val mergeDirectory = File(parentDirectory, "merged").apply {
            deleteRecursively()
            mkdir()
        }

        for ((outputDirectory, density) in outputDirectories) {
            val imageDirectory = File(outputDirectory, "image")

            for (deviceDirectory in imageDirectory.listFiles() ?: emptyArray()) {
                val mergeDeviceDirectory = File(mergeDirectory, deviceDirectory.name).apply {
                    mkdir()
                }

                for (testClassDirectory in deviceDirectory.listFiles() ?: emptyArray()) {
                    val mergeTestClassDirectory = File(mergeDeviceDirectory, testClassDirectory.name).apply {
                        mkdir()
                    }

                    for (testMethodDirectory in testClassDirectory.listFiles() ?: emptyArray()) {
                        val mergeTestMethodDirectory = File(mergeTestClassDirectory, testMethodDirectory.name).apply {
                            mkdir()
                        }

                        for (imageFile in testMethodDirectory.listFiles() ?: emptyArray()) {
                            val mergeImageFile =
                                File(
                                    mergeTestMethodDirectory,
                                    replaceOutputImageTimestampToDensity(imageFile.name, density)
                                )
                            imageFile.copyTo(mergeImageFile, true)
                        }
                    }
                }
            }
        }
    }

    private fun replaceOutputImageTimestampToDensity(fileName: String, density: Int): String {
        val underscoreIndex = fileName.indexOf('_')
        val temp = fileName.substring(underscoreIndex + 1)
        val extensionIndex = temp.lastIndexOf('.')
        return "${temp.substring(0, extensionIndex)}-$density${temp.substring(extensionIndex)}"
    }

    private fun parsePhysicalDensity(input: String): Int {
        var result: Int? = null

        for (line in input.split('\n')) {
            result = line.replace("Physical density: ", "").toIntOrNull()
            if (result != null) {
                break
            }
        }

        return checkNotNull(result) { "fail parse density: $result" }
    }

    private fun createAndroidDebugBridge(): AndroidDebugBridge {
        AndroidDebugBridge.initIfNeeded(false)
        val adbPath = File(File(appExtension.sdkDirectory, "platform-tools"), "adb")
        return AndroidDebugBridge.createBridge(adbPath.absolutePath, false).also {
            waitAdb(it)
        }
    }

    private fun waitAdb(androidDebugBridge: AndroidDebugBridge) {
        var timeOutMs: Long = spoonTask.extension.adbTimeout.toLong() * 1000 * 60
        val sleepTimeMs = TimeUnit.SECONDS.toMillis(1)
        while (androidDebugBridge.hasInitialDeviceList().not() && 0 < timeOutMs) {
            try {
                Thread.sleep(sleepTimeMs)
            } catch (exception: Exception) {

            }
            timeOutMs -= sleepTimeMs
        }
        if (androidDebugBridge.hasInitialDeviceList().not()) {
            throw GradleException("adb timeout")
        }
    }
}
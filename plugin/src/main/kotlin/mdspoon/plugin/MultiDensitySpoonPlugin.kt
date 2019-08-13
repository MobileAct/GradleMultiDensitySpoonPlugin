package mdspoon.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.AndroidBasePlugin
import com.jaredsburrows.spoon.SpoonPlugin
import com.jaredsburrows.spoon.SpoonTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class MultiDensitySpoonPlugin : Plugin<Project> {

    companion object {

        private const val extensionName = "mdspoon"
    }

    override fun apply(project: Project) {
        project.plugins.findPlugin(SpoonPlugin::class.java)
            ?: throw GradleException("must apply mdspoon plugin after apply spoon plugin")
        project.plugins.withType(AndroidBasePlugin::class.java) {
            configureSpoonProject(project)
        }
    }

    private fun configureSpoonProject(project: Project) {
        val multiDensitySpoonExtension =
            project.extensions.create(extensionName, MultiDensitySpoonExtension::class.java)
        val appExtension =
            project.extensions.findByType(AppExtension::class.java) ?: throw GradleException("not found AppExtension")

        project.extensions.findByType(AppExtension::class.java)?.applicationVariants?.whenObjectAdded {
            project.tasks.filterIsInstance<SpoonTask>().forEach { spoonTask ->
                val taskName = "md${spoonTask.name}"

                if (project.tasks.findByName(taskName) != null) {
                    return@forEach
                }

                // spoonTask is initialized by first action
                spoonTask.actions.first().execute(spoonTask)

                project.tasks.create("md${spoonTask.name}", MultiDensitySpoonTask::class.java).apply {
                    description = "Run ${spoonTask.name} for each density"
                    group = "Verification"
                    setDependsOn(spoonTask.dependsOn)
                    setup(spoonTask, multiDensitySpoonExtension, appExtension)
                }
            }
        }
    }
}
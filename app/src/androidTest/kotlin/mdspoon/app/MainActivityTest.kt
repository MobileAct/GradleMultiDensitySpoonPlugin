package mdspoon.app

import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.squareup.spoon.SpoonRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val grantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    @get:Rule
    val spoonRule = SpoonRule()

    @Test
    fun screenShot() {
        val activityScenario = activityScenarioRule.scenario
        activityScenario.moveToState(Lifecycle.State.CREATED)
        activityScenario.onActivity {
            spoonRule.screenshot(it, "MainActivity")
        }
    }
}
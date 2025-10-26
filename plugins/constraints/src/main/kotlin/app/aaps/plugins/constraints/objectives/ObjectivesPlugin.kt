package app.aaps.plugins.constraints.objectives

import app.aaps.core.data.plugin.PluginType
import app.aaps.core.interfaces.constraints.Constraint
import app.aaps.core.interfaces.constraints.Objectives
import app.aaps.core.interfaces.constraints.Objectives.Companion.AUTOSENS_OBJECTIVE
import app.aaps.core.interfaces.constraints.Objectives.Companion.AUTO_OBJECTIVE
import app.aaps.core.interfaces.constraints.Objectives.Companion.FIRST_OBJECTIVE
import app.aaps.core.interfaces.constraints.Objectives.Companion.MAXBASAL_OBJECTIVE
import app.aaps.core.interfaces.constraints.Objectives.Companion.MAXIOB_ZERO_CL_OBJECTIVE
import app.aaps.core.interfaces.constraints.Objectives.Companion.SMB_OBJECTIVE
import app.aaps.core.interfaces.constraints.PluginConstraints
import app.aaps.core.interfaces.logging.AAPSLogger
import app.aaps.core.interfaces.plugin.PluginBase
import app.aaps.core.interfaces.plugin.PluginDescription
import app.aaps.core.interfaces.resources.ResourceHelper
import app.aaps.core.interfaces.sharedPreferences.SP
import app.aaps.plugins.constraints.R
import app.aaps.plugins.constraints.objectives.objectives.Objective
import app.aaps.plugins.constraints.objectives.objectives.Objective0
import app.aaps.plugins.constraints.objectives.objectives.Objective1
import app.aaps.plugins.constraints.objectives.objectives.Objective10
import app.aaps.plugins.constraints.objectives.objectives.Objective2
import app.aaps.plugins.constraints.objectives.objectives.Objective3
import app.aaps.plugins.constraints.objectives.objectives.Objective4
import app.aaps.plugins.constraints.objectives.objectives.Objective5
import app.aaps.plugins.constraints.objectives.objectives.Objective6
import app.aaps.plugins.constraints.objectives.objectives.Objective7
import app.aaps.plugins.constraints.objectives.objectives.Objective9
import dagger.android.HasAndroidInjector
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ObjectivesPlugin @Inject constructor(
    private val injector: HasAndroidInjector,
    aapsLogger: AAPSLogger,
    rh: ResourceHelper,
    private val sp: SP,
) : PluginBase(
    PluginDescription()
        .mainType(PluginType.CONSTRAINTS)
        .fragmentClass(ObjectivesFragment::class.qualifiedName)
        .pluginIcon(app.aaps.core.ui.R.drawable.ic_graduation)
        .pluginName(app.aaps.core.ui.R.string.objectives)
        .shortName(R.string.objectives_shortname)
        .description(R.string.description_objectives),
    aapsLogger, rh
), PluginConstraints, Objectives {

    var objectives: MutableList<Objective> = ArrayList()

    init {
        setupObjectives()
    }

    private fun setupObjectives() {
        objectives.clear()
        val currentTime = System.currentTimeMillis()

        // 为每个目标设置开始时间为1天前，完成时间为当前时间
        objectives.add(Objective0(injector).apply {
            startedOn = currentTime - 86400000
            accomplishedOn = currentTime
        })
        objectives.add(Objective1(injector).apply {
            startedOn = currentTime - 86400000
            accomplishedOn = currentTime
        })
        objectives.add(Objective2(injector).apply {
            startedOn = currentTime - 86400000
            accomplishedOn = currentTime
        })
        objectives.add(Objective3(injector).apply {
            startedOn = currentTime - 86400000
            accomplishedOn = currentTime
        })
        objectives.add(Objective4(injector).apply {
            startedOn = currentTime - 86400000
            accomplishedOn = currentTime
        })
        objectives.add(Objective5(injector).apply {
            startedOn = currentTime - 86400000
            accomplishedOn = currentTime
        })
        objectives.add(Objective6(injector).apply {
            startedOn = currentTime - 86400000
            accomplishedOn = currentTime
        })
        objectives.add(Objective7(injector).apply {
            startedOn = currentTime - 86400000
            accomplishedOn = currentTime
        })
        objectives.add(Objective9(injector).apply {
            startedOn = currentTime - 86400000
            accomplishedOn = currentTime
        })
        objectives.add(Objective10(injector).apply {
            startedOn = currentTime - 86400000
            accomplishedOn = currentTime
        })
    }

    fun reset() {
        for (objective in objectives) {
            objective.startedOn = 0
            objective.accomplishedOn = 0
        }
        sp.putBoolean(app.aaps.core.utils.R.string.key_objectives_bg_is_available_in_ns, false)
        sp.putBoolean(app.aaps.core.utils.R.string.key_objectives_pump_status_is_available_in_ns, false)
        sp.putInt(app.aaps.core.utils.R.string.key_ObjectivesmanualEnacts, 0)
        sp.putBoolean(app.aaps.core.utils.R.string.key_objectiveuseprofileswitch, false)
        sp.putBoolean(app.aaps.core.utils.R.string.key_objectiveusedisconnect, false)
        sp.putBoolean(app.aaps.core.utils.R.string.key_objectiveusereconnect, false)
        sp.putBoolean(app.aaps.core.utils.R.string.key_objectiveusetemptarget, false)
        sp.putBoolean(app.aaps.core.utils.R.string.key_objectiveuseactions, false)
        sp.putBoolean(app.aaps.core.utils.R.string.key_objectiveuseloop, false)
        sp.putBoolean(app.aaps.core.utils.R.string.key_objectiveusescale, false)
    }

    fun allPriorAccomplished(position: Int): Boolean {
        var accomplished = true
        for (i in 0 until position) {
            accomplished = accomplished && objectives[i].isAccomplished
        }
        return accomplished
    }

    /**
     * Constraints interface
     */
    // 移除所有约束检查，直接返回原值
    override fun isLoopInvocationAllowed(value: Constraint<Boolean>): Constraint<Boolean> {
        return value
    }

    override fun isLgsAllowed(value: Constraint<Boolean>): Constraint<Boolean> {
        return value
    }

    override fun isClosedLoopAllowed(value: Constraint<Boolean>): Constraint<Boolean> {
        return value
    }

    override fun isAutosensModeEnabled(value: Constraint<Boolean>): Constraint<Boolean> {
        return value
    }

    override fun isSMBModeEnabled(value: Constraint<Boolean>): Constraint<Boolean> {
        return value
    }

    override fun applyMaxIOBConstraints(maxIob: Constraint<Double>): Constraint<Double> {
        return maxIob
    }

    override fun isAutomationEnabled(value: Constraint<Boolean>): Constraint<Boolean> {
        return value
    }

    // 强制所有目标标记为已完成
    override fun isAccomplished(index: Int): Boolean {
        return true
    }

    // 强制所有目标标记为已开始
    override fun isStarted(index: Int): Boolean {
        return true
    }
}
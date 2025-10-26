package app.aaps.plugins.source

import android.annotation.SuppressLint
import android.content.Context
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceScreen
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import app.aaps.core.data.model.GV
import app.aaps.core.data.model.SourceSensor
import app.aaps.core.data.model.TrendArrow
import app.aaps.core.data.plugin.PluginType
import app.aaps.core.data.ue.Sources
import app.aaps.core.interfaces.db.PersistenceLayer
import app.aaps.core.interfaces.logging.AAPSLogger
import app.aaps.core.interfaces.logging.LTag
import app.aaps.core.interfaces.plugin.PluginDescription
import app.aaps.core.interfaces.resources.ResourceHelper
import app.aaps.core.interfaces.source.BgSource
import app.aaps.core.keys.BooleanKey
import app.aaps.core.objects.workflow.LoggingWorker
import app.aaps.core.validators.preferences.AdaptiveSwitchPreference
import dagger.android.HasAndroidInjector
import kotlinx.coroutines.Dispatchers
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MobaiSIAppPlugin @Inject constructor(
    rh: ResourceHelper,
    aapsLogger: AAPSLogger
) : AbstractBgSourcePlugin(
    PluginDescription()
        .mainType(PluginType.BGSOURCE)
        .fragmentClass(BGSourceFragment::class.java.name)
        .pluginIcon(app.aaps.core.objects.R.drawable.ic_syai_tag)
        .preferencesId(PluginDescription.PREFERENCE_SCREEN)
        .pluginName(R.string.mobai_si_app)
        .preferencesVisibleInSimpleMode(true)  // 改为 true 确保可见
        .description(R.string.description_source_mobai_si_app),
    aapsLogger, rh
), BgSource {

    init {
        aapsLogger.debug(LTag.CORE, "MobaiSIAppPlugin 初始化完成")
        aapsLogger.debug(LTag.CORE, "插件名称: ${rh.gs(R.string.mobai_si_app)}")
        aapsLogger.debug(LTag.CORE, "插件类型: ${pluginDescription.mainType}")
    }

    // 添加重写的 addPreferenceScreen 方法
    override fun addPreferenceScreen(preferenceManager: PreferenceManager, parent: PreferenceScreen, context: Context, requiredKey: String?) {
        aapsLogger.debug(LTag.CORE, "MobaiSIAppPlugin addPreferenceScreen called, requiredKey: $requiredKey")

        // 临时注释掉限制逻辑进行测试
        // if (requiredKey != null) return

        val category = PreferenceCategory(context)
        parent.addPreference(category)
        category.apply {
            key = "mobai_bg_source_settings"
            title = rh.gs(R.string.mobai_si_app)
            initialExpandedChildrenCount = 0
            addPreference(AdaptiveSwitchPreference(ctx = context,
                                                   booleanKey = BooleanKey.BgSourceUploadToNs,
                                                   title = app.aaps.core.ui.R.string.do_ns_upload_title))
        }

        aapsLogger.debug(LTag.CORE, "MobaiSIAppPlugin preference category added successfully")
    }

    class MobaiSIAppWorker(
        context: Context,
        params: WorkerParameters
    ) : LoggingWorker(context, params, Dispatchers.IO) {

        @Inject lateinit var injector: HasAndroidInjector
        @Inject lateinit var mobaiPlugin: MobaiSIAppPlugin
        @Inject lateinit var persistenceLayer: PersistenceLayer

        @SuppressLint("CheckResult")
        override suspend fun doWorkAndLog(): Result {
            var ret = Result.success()
            if (!mobaiPlugin.isEnabled()) return Result.success(workDataOf("Result" to "Plugin not enabled"))

            val collection = inputData.getString("collection") ?:
            return Result.failure(workDataOf("Error" to "missing collection"))

            if (collection == "entries") {
                val data = inputData.getString("data")
                aapsLogger.debug(LTag.BGSOURCE, "Received Mobai SI App Data: $data")

                if (!data.isNullOrEmpty()) {
                    try {
                        val glucoseValues = mutableListOf<GV>()
                        val jsonArray = JSONArray(data)

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            when (val type = jsonObject.getString("type")) {
                                "sgv" -> glucoseValues += createGlucoseValue(jsonObject)
                                else -> aapsLogger.debug(LTag.BGSOURCE, "Unknown entries type: $type")
                            }
                        }

                        persistenceLayer.insertCgmSourceData(
                            Sources.Mobai,
                            glucoseValues,
                            emptyList(),
                            null
                        ).doOnError { error ->
                            aapsLogger.error(LTag.DATABASE, "Error saving Mobai SI App values", error)
                            ret = Result.failure(workDataOf("Error" to error.toString()))
                        }.blockingGet()

                    } catch (e: JSONException) {
                        aapsLogger.error("JSON Parsing Exception", e)
                        ret = Result.failure(workDataOf("Error" to e.toString()))
                    }
                }
            }
            return ret
        }

        private fun createGlucoseValue(jsonObject: JSONObject): GV {
            return GV(
                timestamp = jsonObject.getLong("date"),
                value = jsonObject.getDouble("sgv"),
                raw = jsonObject.getDouble("sgv"),
                noise = null,
                trendArrow = TrendArrow.fromString(jsonObject.getString("direction")),
                sourceSensor = SourceSensor.MOBAI
            )
        }
    }
}
package app.aaps.plugins.source.di

// 添加缺失的导入
import dagger.multibindings.IntoSet
import app.aaps.core.interfaces.plugin.PluginBase
import app.aaps.core.interfaces.source.BgSource

// 其他现有导入
import app.aaps.core.interfaces.source.DexcomBoyda
import app.aaps.core.interfaces.source.NSClientSource
import app.aaps.core.interfaces.source.XDripSource
import app.aaps.plugins.source.BGSourceFragment
import app.aaps.plugins.source.DexcomPlugin
import app.aaps.plugins.source.GlimpPlugin
import app.aaps.plugins.source.MM640gPlugin
import app.aaps.plugins.source.NSClientSourcePlugin
import app.aaps.plugins.source.OttaiPlugin
import app.aaps.plugins.source.PoctechPlugin
import app.aaps.plugins.source.SyaiTagPlugin
import app.aaps.plugins.source.TomatoPlugin
import app.aaps.plugins.source.XdripSourcePlugin
import app.aaps.plugins.source.activities.RequestDexcomPermissionActivity
import app.aaps.plugins.source.MobaiSIAppPlugin // 确保导入新插件
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(
    includes = [
        SourceModule.Bindings::class
    ]
)
@Suppress("unused", "SpellCheckingInspection") // 添加拼写检查抑制
abstract class SourceModule {

    @ContributesAndroidInjector abstract fun contributesBGSourceFragment(): BGSourceFragment

    @ContributesAndroidInjector abstract fun contributesXdripWorker(): XdripSourcePlugin.XdripSourceWorker
    @ContributesAndroidInjector abstract fun contributesDexcomWorker(): DexcomPlugin.DexcomWorker
    @ContributesAndroidInjector abstract fun contributesMM640gWorker(): MM640gPlugin.MM640gWorker
    @ContributesAndroidInjector abstract fun contributesGlimpWorker(): GlimpPlugin.GlimpWorker
    @ContributesAndroidInjector abstract fun contributesPoctechWorker(): PoctechPlugin.PoctechWorker
    @ContributesAndroidInjector abstract fun contributesTomatoWorker(): TomatoPlugin.TomatoWorker
    @ContributesAndroidInjector abstract fun contributesOttaiWorker(): OttaiPlugin.OttaiWorker
    @ContributesAndroidInjector abstract fun contributesOTAppWorker(): SyaiTagPlugin.SyaiTagWorker
    @ContributesAndroidInjector abstract fun contributesMobaiSIAppWorker(): MobaiSIAppPlugin.MobaiSIAppWorker

    @ContributesAndroidInjector abstract fun contributesRequestDexcomPermissionActivity(): RequestDexcomPermissionActivity

    @Module
    interface Bindings {

        @Binds fun bindNSClientSource(nsClientSourcePlugin: NSClientSourcePlugin): NSClientSource
        @Binds fun bindDexcomBoyda(dexcomPlugin: DexcomPlugin): DexcomBoyda
        @Binds fun bindXDrip(xdripSourcePlugin: XdripSourcePlugin): XDripSource
        @Binds fun bindMobaiBgSource(mobaiSIAppPlugin: MobaiSIAppPlugin): BgSource

        // 修正第52-53行的绑定方法
        @Binds
        @IntoSet
        fun bindMobaiPluginToSet(impl: MobaiSIAppPlugin): PluginBase // 删除 abstract 关键字
    }
}
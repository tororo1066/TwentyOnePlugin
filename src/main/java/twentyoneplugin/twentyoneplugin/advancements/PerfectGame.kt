package twentyoneplugin.twentyoneplugin.advancements

import advancement.Advancement
import advancement.display.FrameType
import advancement.trigger.TriggerType
import twentyoneplugin.twentyoneplugin.TOP.Companion.plugin
import twentyoneplugin.twentyoneplugin.TOP.Companion.spcardmaterial
import twentyoneplugin.twentyoneplugin.TOP.Companion.spcards

object PerfectGame : Advancement(plugin,"perfect_game") {

    init {
        addCriteria("perfectgame",TriggerType.IMPOSSIBLE){}
        setParent(Complete21.key)
        setDisplay {
            it.setTitle("パーフェクトゲーム")
            it.setDescription("相手のチップを全て奪い取る")
            it.setIcon(spcardmaterial, "{CustomModelData:${spcards[6]}}")
        }
    }
}
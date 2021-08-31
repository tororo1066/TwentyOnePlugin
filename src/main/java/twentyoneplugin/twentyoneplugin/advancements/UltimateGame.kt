package twentyoneplugin.twentyoneplugin.advancements

import advancement.Advancement
import advancement.display.FrameType
import advancement.trigger.TriggerType
import twentyoneplugin.twentyoneplugin.TOP.Companion.plugin
import twentyoneplugin.twentyoneplugin.TOP.Companion.spcardmaterial
import twentyoneplugin.twentyoneplugin.TOP.Companion.spcards

object UltimateGame : Advancement(plugin,"ultimate_game") {

    init {
        addCriteria("ultimate",TriggerType.IMPOSSIBLE){}
        setParent(PerfectGame.key)
        setDisplay {
            it.setTitle("アルティメットゲーム")
            it.setDescription("相手のチップを1ターンで全て奪い取る")
            it.setHidden(true)
            it.setIcon(spcardmaterial, "{CustomModelData:${spcards[7]}}")
            it.setFrame(FrameType.CHALLENGER)
        }
    }
}
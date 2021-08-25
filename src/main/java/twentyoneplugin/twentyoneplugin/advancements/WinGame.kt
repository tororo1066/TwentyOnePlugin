package twentyoneplugin.twentyoneplugin.advancements

import advancement.Advancement
import advancement.display.FrameType
import advancement.trigger.TriggerType
import twentyoneplugin.twentyoneplugin.TOP.Companion.cardcsm
import twentyoneplugin.twentyoneplugin.TOP.Companion.cardmaterial
import twentyoneplugin.twentyoneplugin.TOP.Companion.plugin

object WinGame : Advancement(plugin,"win_game") {

    init {
        addCriteria("win", TriggerType.IMPOSSIBLE){}
        setParent(UseSp.key)
        setDisplay {
            it.setTitle("勝利")
            it.setDescription("試合に勝利する")
            it.setFrame(FrameType.GOAL)
            it.setIcon(cardmaterial,"{CustomModelData:${cardcsm[1]}}")
        }
    }
}
package twentyoneplugin.twentyoneplugin.advancements

import advancement.Advancement
import advancement.trigger.TriggerType
import twentyoneplugin.twentyoneplugin.TOP
import twentyoneplugin.twentyoneplugin.TOP.Companion.cardmaterial
import twentyoneplugin.twentyoneplugin.TOP.Companion.plugin

object UseSp : Advancement(plugin,"use_sp") {

    init {
        addCriteria("usesp",TriggerType.IMPOSSIBLE){}
        setParent(JoinGame.key)
        setDisplay {
            it.setTitle("特殊カード")
            it.setDescription("spカードを使う")
            it.setIcon(cardmaterial,"{CustomModelData:${TOP.cardcsm[0]}}")
        }
    }
}
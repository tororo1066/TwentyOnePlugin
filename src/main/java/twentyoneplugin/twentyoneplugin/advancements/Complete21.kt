package twentyoneplugin.twentyoneplugin.advancements

import advancement.Advancement
import advancement.trigger.TriggerType
import twentyoneplugin.twentyoneplugin.TOP.Companion.cardcsm
import twentyoneplugin.twentyoneplugin.TOP.Companion.cardmaterial
import twentyoneplugin.twentyoneplugin.TOP.Companion.plugin
import twentyoneplugin.twentyoneplugin.TOP.Companion.spcardmaterial
import twentyoneplugin.twentyoneplugin.TOP.Companion.spcards

object Complete21 : Advancement(plugin,"comp_21") {

    init {
        setParent(LoginServer.key)
        addCriteria("comp21",TriggerType.IMPOSSIBLE){}
        setDisplay {
            it.setTitle("完成")
            it.setDescription("21を完成させる")
            it.setIcon(spcardmaterial,"{CustomModelData:${spcards[8]}}")
        }
    }
}
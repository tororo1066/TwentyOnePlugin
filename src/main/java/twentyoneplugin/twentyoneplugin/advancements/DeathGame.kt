package twentyoneplugin.twentyoneplugin.advancements

import advancement.Advancement
import advancement.trigger.TriggerType
import twentyoneplugin.twentyoneplugin.TOP.Companion.plugin
import twentyoneplugin.twentyoneplugin.TOP.Companion.spcardmaterial
import twentyoneplugin.twentyoneplugin.TOP.Companion.spcards

object DeathGame : Advancement(plugin,"death_game") {

    init {
        addCriteria("deathgame",TriggerType.IMPOSSIBLE){}
        setParent(LoginServer.key)
        setDisplay {
            it.setTitle("生か死か")
            it.setDescription("デスぺレーションを使用する")
            it.setIcon(spcardmaterial, "{CustomModelData:${spcards[23]}}")
        }
    }
}
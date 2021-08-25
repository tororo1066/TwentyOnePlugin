package twentyoneplugin.twentyoneplugin.advancements

import advancement.Advancement
import advancement.trigger.TriggerType
import twentyoneplugin.twentyoneplugin.TOP.Companion.plugin

object HowToGetHere : Advancement(plugin,"how_to_get_here") {

    init {
        addCriteria("howto",TriggerType.IMPOSSIBLE){}
        setParent(UltimateGame.key)
        setDisplay {
            it.setTitle("どうやってここまで？")
            it.setDescription("")
        }
    }
}
package twentyoneplugin.twentyoneplugin.advancements

import advancement.Advancement
import advancement.display.Icon
import advancement.trigger.TriggerType
import org.bukkit.Material
import twentyoneplugin.twentyoneplugin.TOP.Companion.plugin

object Addict : Advancement(plugin,"addict") {

    init {
        addCriteria("addict",TriggerType.IMPOSSIBLE){}
        setParent(JoinGame.key)
        setDisplay {
            it.setTitle("中毒者")
            it.setDescription("21を10回プレイする")
            it.setIcon(Icon(Material.PLAYER_HEAD,"{SkullOwner:MHF_Creeper}"))
        }
    }
}
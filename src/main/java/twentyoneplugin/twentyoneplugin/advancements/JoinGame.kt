package twentyoneplugin.twentyoneplugin.advancements

import advancement.Advancement
import advancement.trigger.TriggerType
import org.bukkit.Material
import twentyoneplugin.twentyoneplugin.TOP.Companion.plugin

object JoinGame : Advancement(plugin,"join_game") {

    init {
        addCriteria("join", TriggerType.IMPOSSIBLE){}
        setParent(LoginServer.key)
        setDisplay {
            it.setTitle("死のゲーム")
            it.setDescription("BJPをプレイする")
            it.setIcon(Material.TOTEM_OF_UNDYING)
        }
    }
}
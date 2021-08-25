package twentyoneplugin.twentyoneplugin.advancements

import advancement.Advancement
import advancement.display.FrameType
import advancement.trigger.TriggerType
import org.bukkit.Material
import twentyoneplugin.twentyoneplugin.TOP.Companion.plugin

object HundredBattles : Advancement(plugin,"hundred_battles") {

    init {
        addCriteria("hundred",TriggerType.IMPOSSIBLE){}
        setParent(Addict.key)
        setDisplay {
            it.setTitle("百戦錬磨")
            it.setDescription("100回以上試合をする\n暇人")
            it.setFrame(FrameType.CHALLENGER)
            it.setIcon(Material.PLAYER_HEAD,"{SkullOwner:MHF_WSkeleton}")
        }
    }
}
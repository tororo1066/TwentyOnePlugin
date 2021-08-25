package twentyoneplugin.twentyoneplugin.advancements

import advancement.Advancement
import advancement.display.FrameType
import advancement.trigger.TriggerType
import org.bukkit.Material
import twentyoneplugin.twentyoneplugin.TOP.Companion.plugin

object ProGamer : Advancement(plugin,"progamer") {

    init {
        addCriteria("pro_gamer",TriggerType.IMPOSSIBLE){}
        setParent(HundredBattles.key)
        setDisplay {
            it.setTitle("プロゲーマー")
            it.setDescription("100戦以上して勝率を70%以上にする\n真の暇人")
            it.setFrame(FrameType.CHALLENGER)
            it.setIcon(Material.PLAYER_HEAD,"{SkullOwner:MHF_Herobrine}")
        }
    }
}
package twentyoneplugin.twentyoneplugin

import advancement.Advancement
import advancement.AdvancementManager
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import twentyoneplugin.twentyoneplugin.TOP.Companion.plugin
import twentyoneplugin.twentyoneplugin.advancements.*

class AdvancementUtils : AdvancementManager(plugin), Listener {

    fun loadAdvancements(){
        registerAll(LoginServer,JoinGame,UseSp,WinGame,Complete21,PerfectGame,UltimateGame,DeathGame,Addict,HundredBattles,ProGamer)
        createAll(false)
    }

    private fun registerAll(vararg advancement: Advancement){
        advancement.forEach { register(it) }
    }

    companion object{
        fun Player.awardAdvancement(key: NamespacedKey) {
            val advancement = Bukkit.getAdvancement(key)!!
            val progress = getAdvancementProgress(advancement)
            advancement.criteria.forEach { progress.awardCriteria(it) }
        }
    }

    @EventHandler
    fun onJoin(e : PlayerJoinEvent){
        e.player.awardAdvancement(LoginServer.key)
    }

}
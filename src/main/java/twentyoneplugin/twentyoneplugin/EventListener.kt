package twentyoneplugin.twentyoneplugin


import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import twentyoneplugin.twentyoneplugin.Inventory.checkitem
import twentyoneplugin.twentyoneplugin.Inventory.replaceaction
import twentyoneplugin.twentyoneplugin.Inventory.setcard
import twentyoneplugin.twentyoneplugin.Util.getdata
import twentyoneplugin.twentyoneplugin.Util.turnchange

object EventListener : Listener {



    @EventHandler
    fun invclick(e : InventoryClickEvent){
        if (!e.view.title().contains(Component.text("§0§l§kaaa§5§l2§0§l§kaa§6§l1§0§l§kaaa")))return
        e.isCancelled = true
        if (e.currentItem == null)return
        val p = e.whoClicked as Player
        if (checkitem(e.inventory,e.slot,"§f§lカードを引く")){
            if (!setcard(p.uniqueId))return
            return
        }
        if (checkitem(e.inventory,e.slot,"§a§lカードを引かない")){
            turnchange(getdata(p.uniqueId).enemy)
        }
    }
}
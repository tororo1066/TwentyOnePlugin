package twentyoneplugin.twentyoneplugin


import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import twentyoneplugin.twentyoneplugin.Inventory.checkitem
import twentyoneplugin.twentyoneplugin.Inventory.createitem
import twentyoneplugin.twentyoneplugin.Inventory.replaceaction
import twentyoneplugin.twentyoneplugin.Inventory.setcard
import twentyoneplugin.twentyoneplugin.Inventory.spuse
import twentyoneplugin.twentyoneplugin.TOP.Companion.cansp
import twentyoneplugin.twentyoneplugin.Util.getdata
import twentyoneplugin.twentyoneplugin.Util.turnchange

object EventListener : Listener {



    @EventHandler
    fun invclick(e : InventoryClickEvent){
        if (!e.view.title.contains("21table"))return
        e.isCancelled = true
        if (e.currentItem == null)return
        val p = e.whoClicked as Player
        if (checkitem(e.inventory,e.slot,"§f§lカードを引く")){
            if (!setcard(p.uniqueId))return
            getdata(p.uniqueId).action = "draw"
            return
        }
        if (checkitem(e.inventory,e.slot,"§a§lカードを引かない")){
            getdata(p.uniqueId).action = "through"
            return
        }
        if (e.slot in 36..44){
            spuse(p.uniqueId,e.currentItem!!)
            return
        }
    }
}
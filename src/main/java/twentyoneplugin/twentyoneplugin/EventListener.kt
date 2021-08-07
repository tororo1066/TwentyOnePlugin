package twentyoneplugin.twentyoneplugin


import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.persistence.PersistentDataType
import twentyoneplugin.twentyoneplugin.Inventory.betchange
import twentyoneplugin.twentyoneplugin.Inventory.checkitem
import twentyoneplugin.twentyoneplugin.Inventory.createitem
import twentyoneplugin.twentyoneplugin.Inventory.setcard
import twentyoneplugin.twentyoneplugin.Inventory.showcardcount
import twentyoneplugin.twentyoneplugin.Inventory.spuse
import twentyoneplugin.twentyoneplugin.TOP.Companion.datamap
import twentyoneplugin.twentyoneplugin.Util.getdata
import twentyoneplugin.twentyoneplugin.Util.getenemy
import twentyoneplugin.twentyoneplugin.Util.sendmsg

object EventListener : Listener {



    @EventHandler
    fun invclick(e : InventoryClickEvent){
        if (e.view.title.contains("21Result"))e.isCancelled = true
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
        if (e.slot == 8 || e.slot == 27){
            showcardcount(p.uniqueId)
            return
        }
        if (e.slot == 26 || e.slot == 9){
            betchange(p.uniqueId)
            betchange(getenemy(p.uniqueId))
            return
        }
        if (e.slot in 36..44){
            if (!e.inventory.contains(createitem(Material.BLACK_STAINED_GLASS_PANE,"§f§lカードを引く")))return
            if (!getdata(p.uniqueId).spuse){
                p.sendmsg("§cspカード使用が封じられています")
                return
            }
            spuse(p.uniqueId,e.currentItem!!,e.slot)
            return
        }
    }

    @EventHandler
    fun quit(e : PlayerQuitEvent){
        if (datamap.containsKey(e.player.uniqueId)){
            val playerinv = e.player.inventory
            for (content in playerinv.contents){
                if (content == null)continue
                if (content.itemMeta.persistentDataContainer.has(
                        NamespacedKey(TOP.plugin,"sp"),
                        PersistentDataType.INTEGER)){
                    playerinv.remove(content)
                }
            }
        }
    }
}
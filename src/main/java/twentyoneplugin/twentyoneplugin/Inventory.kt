package twentyoneplugin.twentyoneplugin

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import twentyoneplugin.twentyoneplugin.TOP.Companion.cardcsm
import twentyoneplugin.twentyoneplugin.TOP.Companion.datamap
import twentyoneplugin.twentyoneplugin.TOP.Companion.plugin
import twentyoneplugin.twentyoneplugin.TOP.Companion.spcards
import twentyoneplugin.twentyoneplugin.Util.allplaysound
import twentyoneplugin.twentyoneplugin.Util.getdata
import twentyoneplugin.twentyoneplugin.Util.getplayer
import twentyoneplugin.twentyoneplugin.Util.per
import twentyoneplugin.twentyoneplugin.Util.sendmsg
import twentyoneplugin.twentyoneplugin.Util.turnchange
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random
import kotlin.random.nextInt

object Inventory {

    fun invsetup(p : UUID, enemy : UUID): Inventory {
        val inv = Bukkit.createInventory(null,54, Component.text("§0§l§kaaa§5§l2§0§l§kaa§6§l1§0§l§kaaa"))
        inv.setItem(8,createhead(enemy))
        inv.setItem(27,createhead(p))
        inv.setItem(17, ItemStack(Material.CLOCK,plugin.config.getInt("clocktime")))
        return inv
    }
    //cccccccch
    //OOsssss0ti
    //OOsssssOyama
    //hcccccccc
    //sssssssss
    //OOOOOOtds

    //0  1  2  3  4  5  6  7  8
    //9  10 11 12 13 14 15 16 17
    //18 19 20 21 22 23 24 25 26
    //27 28 29 30 31 32 33 34 35
    //36 37 38 39 40 41 42 43 44
    //45 46 47 48 49 50 51 52 53


    fun fillair(inv : Inventory,slot: IntRange){
        for (i in slot){
            inv.setItem(i, ItemStack(Material.AIR))
        }
        return
    }

    fun createitem(material: Material, name : String): ItemStack {
        val item = ItemStack(material)
        val meta = item.itemMeta
        meta.displayName(Component.text(name))
        item.itemMeta = meta
        return item
    }

    fun createitem(material: Material, name : String, slot : IntRange, inv : Inventory){
        val item = ItemStack(material)
        val meta = item.itemMeta
        meta.displayName(Component.text(name))
        item.itemMeta = meta
        for (i in slot){
            inv.setItem(i,item)
        }
        return
    }

    fun createitem(material: Material, name : String, lore : MutableList<Component>): ItemStack {
        val item = ItemStack(material)
        val meta = item.itemMeta
        meta.displayName(Component.text(name))
        meta.lore(lore)
        item.itemMeta = meta
        return item
    }

    fun createitem(material: Material, name : String, csm : Int): ItemStack {
        val item = ItemStack(material)
        val meta = item.itemMeta
        meta.displayName(Component.text(name))
        meta.setCustomModelData(csm)
        item.itemMeta = meta
        return item
    }

    fun intrangeitem(inv : Inventory, item : ItemStack, slot : IntRange){
        for (i in slot){
            inv.setItem(i,item)
        }
        return
    }

    fun createhead(p : UUID): ItemStack {
        val item = ItemStack(Material.PLAYER_HEAD)
        val meta = item.itemMeta as SkullMeta
        meta.owningPlayer = Bukkit.getOfflinePlayer(p)
        item.itemMeta = meta
        return item
    }

    fun checkitem(inv : Inventory, slot : Int, name : String): Boolean {
        if (inv.getItem(slot)?.itemMeta?.displayName() == Component.text(name))return true
        return false
    }

    fun setnbt(item : ItemStack, namespacedKey: String, num : Int): ItemStack {
        val meta = item.itemMeta
        meta.persistentDataContainer.set(NamespacedKey(plugin,namespacedKey), PersistentDataType.INTEGER,num)
        item.itemMeta = meta
        return item
    }

    fun yamahudacheck(p : UUID) : ArrayList<Int>?{
        val inv = getinv(p)
        val check = ArrayList<Int>()
        for (cardnum in 1..11){
            if (inv.getItem(26)!!.itemMeta.persistentDataContainer[NamespacedKey(plugin,"$cardnum"),PersistentDataType.INTEGER] == 1)check.add(cardnum)
        }
        if (check.isEmpty()){
            Bukkit.getPlayer(p)?.sendmsg("§c山札が空です！")
            return null
        }
        return check
    }

    fun setcard(p: UUID) : Boolean{
        val inv = getinv(p)
        val eninv = getinv(getdata(p).enemy)
        if (inv.getItem(35) != null){
            Bukkit.getPlayer(p)?.sendmsg("§cあなたはもうカードを引くことはできません！")
            return false
        }
        val yama = yamahudacheck(p) ?:return false
        if (per(10.0)){
            if (checkplayersp(p) != -1){
                inv.setItem(checkplayersp(p), drawspcard())
            }
        }
        val card = drawcard(yama,false)
        inv.setItem(checkplayercard(p), card)
        eninv.setItem(checkenemycard(p),card)
        allplaysound(Sound.ITEM_BOOK_PAGE_TURN,p)
        getdata(p).through = false
        turnchange(getdata(p).enemy)
        return true
    }

    fun checkplayercard(p : UUID): Int {
        val inv = getinv(p) //28~35
        for (i in 28..35){
            if (inv.getItem(i) != null)continue
            return i
        }
        getplayer(p).sendmsg("§cあなたはもうカードを引くことはできません！")
        return -1

    }

    fun checkenemycard(p : UUID): Int {
        val inv = getinv(getdata(p).enemy) //7~0
        for (i in 7 downTo 0){
            if (inv.getItem(i) != null)continue
            return i
        }
        return -1

    }
    fun countcard(p: UUID): Int {
        val inv = getinv(p) //28~35
        var count = 0
        for (i in 28..35){
            if (inv.getItem(i) == null)continue
            count+= inv.getItem(i)!!.itemMeta.persistentDataContainer[NamespacedKey(plugin,"cardnum"), PersistentDataType.INTEGER]!!
        }
        return count
    }

    fun checkplayersp(p : UUID): Int {
        val inv = getinv(p) //36~44
        for (i in 36..44){
            if (inv.getItem(i) != null)continue
            return i
        }
        getplayer(p).sendmsg("§cあなたはもうspカードを引くことはできません！")
        return -1
    }

    fun getinv(p : UUID): Inventory {
        return getdata(p).inv
    }

    fun drawspcard(): ItemStack {
        when(spcards.random()){
            1->{
                val random = Random.nextInt(1..11)
                val item = createitem(Material.ENCHANTED_BOOK,"§6ドロー$random")
                setnbt(item,"sp",1)
                setnbt(item,"spdraw",random)
                return item
            }

            2->{

            }

        }
        return ItemStack(Material.AIR)
    }

    fun drawcard(cardlist : ArrayList<Int>, invisible : Boolean) : ItemStack{
        val cardnum = cardlist.random()
        val item = createitem(if (invisible) Material.BOOK else Material.PAPER,cardnum.toString(), cardcsm[cardnum-1])
        setnbt(item,"cardnum",cardnum)
        return item
    }

    fun replaceaction(p : UUID){
        val inv = getinv(p)
        fillair(inv,51..53)
        return
    }

    fun fillaction(p : UUID){
        val inv = getinv(p)
        inv.setItem(51,createitem(Material.YELLOW_STAINED_GLASS_PANE,"§e§lSPカードを使う"))
        inv.setItem(52,createitem(Material.BLACK_STAINED_GLASS_PANE,"§f§lカードを引く"))
        inv.setItem(53,createitem(Material.GREEN_STAINED_GLASS_PANE,"§a§lカードを引かない"))
        return
    }

    fun setallplayer(p : UUID,set : Int, item : ItemStack){
        val inv = getinv(p)
        val eninv = getinv(getdata(p).enemy)
        inv.setItem(set,item)
        eninv.setItem(set,item)
        return
    }
}
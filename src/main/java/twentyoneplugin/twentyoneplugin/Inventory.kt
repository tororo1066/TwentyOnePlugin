package twentyoneplugin.twentyoneplugin

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.inventory.EquipmentSlot
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
import twentyoneplugin.twentyoneplugin.Util.playsound
import twentyoneplugin.twentyoneplugin.Util.sendmsg
import twentyoneplugin.twentyoneplugin.Util.turnchange
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random
import kotlin.random.nextInt

object Inventory {

    fun invsetup(p : UUID, enemy : UUID): Inventory {
        val inv = Bukkit.createInventory(null,54, Component.text("21table"))
        inv.setItem(8,createhead(enemy))
        inv.setItem(27,createhead(p))
        val item = ItemStack(Material.BOOKSHELF)
        for (loop in 1..11){
            setnbt(item,"$loop",1)
        }
        inv.setItem(18,item)
        inv.setItem(17, ItemStack(Material.CLOCK,plugin.config.getInt("clocktime")))
        inv.setItem(9, createitem(Material.GOLD_NUGGET,"§c${getplayer(enemy).name}の賭け数/チップ", mutableListOf(Component.text("§e0/10枚"))))
        inv.setItem(26, createitem(Material.GOLD_NUGGET,"§c${getplayer(p).name}の賭け数/チップ", mutableListOf(Component.text("§e0/10枚"))))
        return inv
    }
    //cccccccch
    //tOsssss0ti
    //yOsssssOtip
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


    fun createitem(material: Material, name : String, lore : MutableList<Component>): ItemStack {
        val item = ItemStack(material)
        val meta = item.itemMeta
        meta.displayName(Component.text(name))
        meta.lore(lore)
        item.itemMeta = meta
        return item
    }

    fun createitem(material: Material, name : String, lore : MutableList<Component>, csm : Int): ItemStack {
        val item = ItemStack(material)
        val meta = item.itemMeta
        meta.displayName(Component.text(name))
        meta.lore(lore)
        meta.setCustomModelData(csm)
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
            if (inv.getItem(18)!!.itemMeta.persistentDataContainer[NamespacedKey(plugin,"$cardnum"),PersistentDataType.INTEGER] == 1)check.add(cardnum)
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
        if (per(10.0)){
            if (checkplayersp(p) != -1){
                inv.setItem(checkplayersp(p), drawspcard())
                getplayer(p).playsound(Sound.BLOCK_ANVIL_PLACE)
            }
        }
        val card = drawcard(p,false)
        inv.setItem(checkplayercard(p), card)
        eninv.setItem(checkenemycard(getdata(p).enemy),card)
        allplaysound(Sound.ITEM_BOOK_PAGE_TURN,p)
        getdata(p).through = false
        turnchange(getdata(p).enemy)
        return true
    }

    fun checkplayercard(p : UUID): Int {//調べる対象を選択
        val inv = getinv(p) //28~35
        for (i in 28..35){
            if (inv.getItem(i) != null)continue
            return i
        }
        getplayer(p).sendmsg("§cあなたはもうカードを引くことはできません！")
        return -1

    }

    fun checkenemycard(p : UUID): Int {//エネミーじゃないほうを選択
        val inv = getinv(p) //7~0
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

    fun checkplayersp(p : UUID): Int {//調べる対象を選択
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
        Thread.sleep(1000)

        when(val sprandom = spcards.keys.random()){
            1->{
                val random = Random.nextInt(1..11)
                val item = createitem(Material.TOTEM_OF_UNDYING,"§6ドロー$random", mutableListOf(Component.text("山札に残っている場合のみ、${random}のカードを引く。")) ,spcards[sprandom]!!)
                setnbt(item,"sp",1)
                setnbt(item,"spdraw",random)
                return item
            }

            2->{

            }

        }
        return ItemStack(Material.AIR)
    }

    fun spuse(p: UUID, item: ItemStack){
        Bukkit.getScheduler().runTask(plugin, Runnable {
            getplayer(p).closeInventory()
            getplayer(getdata(p).enemy).closeInventory()
            return@Runnable
        })
        if (getplayer(p).inventory.itemInMainHand.type != Material.AIR) getplayer(p).location.world.dropItemNaturally(getplayer(p).location, getplayer(p).inventory.itemInMainHand)
        if (getplayer(getdata(p).enemy).inventory.itemInMainHand.type != Material.AIR) getplayer(getdata(p).enemy).location.world.dropItemNaturally(getplayer(getdata(p).enemy).location, getplayer(getdata(p).enemy).inventory.itemInMainHand)
        getplayer(p).inventory.setItem(EquipmentSlot.CHEST,item)
        getplayer(getdata(p).enemy).inventory.setItemInMainHand(item)
        getplayer(p).damage(999999.0)
        getplayer(getdata(p).enemy).damage(999999.0)

        when(item.itemMeta.persistentDataContainer[NamespacedKey(plugin,"sp"), PersistentDataType.INTEGER]){
            1->{
                val inv = getinv(p)
                val drawint = item.itemMeta.persistentDataContainer[NamespacedKey(plugin,"spdraw"), PersistentDataType.INTEGER]!!
                if (inv.getItem(18)!!.itemMeta.persistentDataContainer[NamespacedKey(plugin,"$drawint"), PersistentDataType.INTEGER]!! == 1){

                }
            }

            2->{

            }
        }
    }

    fun drawcard(p : UUID, invisible : Boolean) : ItemStack? {
        val cardnum = yamahudacheck(p)?.random()?:return null
        val item = createitem(if (invisible) Material.BOOK else Material.PAPER,cardnum.toString(), cardcsm[cardnum-1])
        setnbt(item,"cardnum",cardnum)
        setnbt(getinv(p).getItem(18)!!,"$cardnum",0)
        setnbt(getinv(getdata(p).enemy).getItem(18)!!,"$cardnum",0)
        return item
    }

    fun nullcarddis(item : ItemStack): ItemStack {
        val meta = item.itemMeta
        meta.displayName(Component.text("§l？"))
        item.itemMeta = meta
        return item
    }

    fun replaceaction(p : UUID){
        val inv = getinv(p)
        fillair(inv,52..53)
        return
    }

    fun fillaction(inv: Inventory){
        inv.setItem(52,createitem(Material.BLACK_STAINED_GLASS_PANE,"§f§lカードを引く"))
        inv.setItem(53,createitem(Material.GREEN_STAINED_GLASS_PANE,"§a§lカードを引かない"))
        return
    }

    fun setallplayer(p : UUID,set : Int, item : ItemStack){
        getinv(p).setItem(set,item)
        getinv(getdata(p).enemy).setItem(set,item)
        return
    }

    fun betchange(p: UUID, bet : Int, tip : Int){//変える側
        getinv(p).setItem(26,createitem(Material.GOLD_NUGGET,"§c${getplayer(p).name}の賭け数/チップ", mutableListOf(Component.text("§e$bet/$tip"))))
        getinv(getdata(p).enemy).setItem(9,createitem(Material.GOLD_NUGGET,"§c${getplayer(p).name}の賭け数/チップ", mutableListOf(Component.text("§e$bet/$tip"))))
        setnbt(getinv(p).getItem(26)!!,"bet",bet)
        setnbt(getinv(p).getItem(26)!!,"tip",tip)
        setnbt(getinv(getdata(p).enemy).getItem(26)!!,"bet",bet)
        setnbt(getinv(getdata(p).enemy).getItem(26)!!,"tip",tip)
        return
    }

    fun getbet(p : UUID) : Pair<Int,Int>{
        return Pair(getinv(p).getItem(26)!!.itemMeta.displayName.split("/")[0].toInt(),getinv(p).getItem(26)!!.itemMeta.displayName.split("/")[2].toInt())
    }
}
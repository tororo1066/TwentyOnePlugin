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
import twentyoneplugin.twentyoneplugin.AdvancementUtils.Companion.awardAdvancement
import twentyoneplugin.twentyoneplugin.TOP.Companion.cardcsm
import twentyoneplugin.twentyoneplugin.TOP.Companion.cardmaterial
import twentyoneplugin.twentyoneplugin.TOP.Companion.invisiblecardcsm
import twentyoneplugin.twentyoneplugin.TOP.Companion.plugin
import twentyoneplugin.twentyoneplugin.TOP.Companion.spcardmaterial
import twentyoneplugin.twentyoneplugin.TOP.Companion.spcards
import twentyoneplugin.twentyoneplugin.Util.allplayersend
import twentyoneplugin.twentyoneplugin.Util.allplaysound
import twentyoneplugin.twentyoneplugin.Util.getdata
import twentyoneplugin.twentyoneplugin.Util.getenemy
import twentyoneplugin.twentyoneplugin.Util.getplayer
import twentyoneplugin.twentyoneplugin.Util.isDone
import twentyoneplugin.twentyoneplugin.Util.per
import twentyoneplugin.twentyoneplugin.Util.playsound
import twentyoneplugin.twentyoneplugin.Util.sendmsg
import twentyoneplugin.twentyoneplugin.advancements.DeathGame
import twentyoneplugin.twentyoneplugin.advancements.JoinGame
import twentyoneplugin.twentyoneplugin.advancements.LoginServer
import twentyoneplugin.twentyoneplugin.advancements.UseSp
import java.util.*
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
        inv.setItem(17, ItemStack(Material.CLOCK, getdata(p).clocktime))
        getdata(p).bjnumber = 21
        getdata(enemy).bjnumber = 21
        getdata(p).bet = getdata(p).firstbet
        getdata(enemy).bet = getdata(p).firstbet

        inv.setItem(26, createitem(Material.GOLD_NUGGET,"§c${getdata(getenemy(p)).name}の賭け数/チップ", mutableListOf(Component.text("§e${getdata(p).firstbet}/${getdata(enemy).tipcoin}枚"))))
        inv.setItem(9, createitem(Material.GOLD_NUGGET,"§c${getdata(p).name}の賭け数/チップ", mutableListOf(Component.text("§e${getdata(p).firstbet}/${getdata(p).tipcoin}枚"))))
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
        inv.getItem(1)
        if (inv.getItem(slot)?.itemMeta?.displayName() == Component.text(name))return true
        return false
    }

    fun setnbt(item : ItemStack, namespacedKey: String, num : Int): ItemStack {
        val meta = item.itemMeta
        meta.persistentDataContainer.set(NamespacedKey(plugin,namespacedKey), PersistentDataType.INTEGER,num)
        item.itemMeta = meta
        return item
    }

    fun getnbt(item : ItemStack, namespacedKey: String): Int {
        return item.itemMeta.persistentDataContainer[NamespacedKey(plugin,namespacedKey), PersistentDataType.INTEGER]!!
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
        if (countcard(p) > getdata(p).bjnumber){
            p.sendmsg("§cあなたはすでにバーストしています！")
            return false
        }

        val card = drawcard(p,false) ?: return false
        inv.setItem(checkplayercard(p), card)
        eninv.setItem(checkenemycard(getdata(p).enemy),card)
        if (per(plugin.config.getDouble("spdrawchance"))){
            if (checkplayersp(p) != -1){
                inv.setItem(checkplayersp(p), drawspcard(p))
                getplayer(p)?.playsound(Sound.BLOCK_ANVIL_PLACE)
            }
        }
        allplaysound(Sound.ITEM_BOOK_PAGE_TURN,p)
        getdata(p).through = false
        return true
    }

    fun checkplayercard(p : UUID): Int {//調べる対象を選択
        val inv = getinv(p) //28~35
        for (i in 28..35){
            if (inv.getItem(i) != null)continue
            return i
        }
        getplayer(p)?.sendmsg("§cあなたはもうカードを引くことはできません！")
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

    fun checkplayerspput(p : UUID): Int {//調べる対象を選択
        val inv = getinv(p)
        for (i in 20..24){
            if (inv.getItem(i) != null)continue
            return i
        }
        return -1
    }

    fun checkenemyspput(p : UUID): Int {//調べる対象を選択
        val inv = getinv(p)
        for (i in 15 downTo 11){
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
        return -1
    }


    fun getinv(p : UUID): Inventory {
        return getdata(p).inv
    }

    fun showcardcount(p : UUID){
        val item1 = getinv(p).getItem(27)!!
        val meta1 = item1.itemMeta
        val item2 = getinv(p).getItem(8)!!
        val meta2 = item2.itemMeta
        val item3 = getinv(getenemy(p)).getItem(27)!!
        val meta3 = item3.itemMeta
        val item4 = getinv(getenemy(p)).getItem(8)!!
        val meta4 = item4.itemMeta
        meta1.displayName(Component.text("§e${getdata(p).name}の合計数字 ${countcard(p)} / ${getdata(p).bjnumber}"))
        meta3.displayName(Component.text("§e${getdata(getenemy(p)).name}の合計数字 ${countcard(
            getenemy(p))} / ${getdata(p).bjnumber}"))

        meta2.displayName(Component.text("§e${getdata(getenemy(p)).name}の合計数字 ${countcard(
            getenemy(p)) - getnbt(getinv(p).getItem(7)!!,"cardnum")} + ? / ${getdata(p).bjnumber}"))

        meta4.displayName(Component.text("§e${getdata(p).name}の合計数字 ${countcard(
            p) - getnbt(getinv(getenemy(p)).getItem(7)!!,"cardnum")} + ? / ${getdata(p).bjnumber}"))

        item1.itemMeta = meta1
        item2.itemMeta = meta2
        item3.itemMeta = meta3
        item4.itemMeta = meta4
        return
    }

    fun drawspcard(p: UUID): ItemStack {

        allplaysound(Sound.ENTITY_PLAYER_LEVELUP,p)
        allplayersend(p,"§a${getdata(p).name}はspカードを引いた")

        when(val sprandom = spcards.keys.random()){
            1->{
                val random = Random.nextInt(3..7)
                val item = createitem(spcardmaterial,"§6ドロー$random", mutableListOf(
                    Component.text("§e山札に残っている場合のみ、${random}のカードを引く。")) ,
                    plugin.config.getIntegerList("sp.1.drawcsm")[random-1])
                setnbt(item,"sp",1)
                setnbt(item,"spdraw",random)
                return item
            }

            2->{
                val item = createitem(spcardmaterial,"§6リムーブ",mutableListOf(
                    Component.text("§e相手が最後にひいたカードを山札に戻す。"),
                    Component.text("§e相手の残りのカードが一枚だと使えない。")),
                    spcards[sprandom]!!)
                setnbt(item,"sp",2)
                return item
            }

            3->{
                val item = createitem(spcardmaterial,"§6デストロイ", mutableListOf(
                    Component.text("§e相手が最後に場に置いたSPカードを取り除く。")),
                    spcards[sprandom]!!)
                setnbt(item,"sp",3)
                return item
            }

            4->{
                val item = createitem(spcardmaterial,"§6デストロイ+", mutableListOf(
                    Component.text("§e相手が場に置いた全てのSPカードを取り除く。")),
                    spcards[sprandom]!!)
                setnbt(item,"sp",4)
                return item
            }

            5->{
                val item = createitem(spcardmaterial,"§6パーフェクトドロー", mutableListOf(
                    Component.text("§e山札の中から、一番良い数字のカードを引く。"),
                    Component.text("§e適切なカードが見つからなければ引かない。")),
                    spcards[sprandom]!!)
                setnbt(item,"sp",5)
                setnbt(item,"betup",5)
                return item
            }

            6->{
                val item = createitem(spcardmaterial,"§6パーフェクトドロー+", mutableListOf(
                    Component.text("§e山札の中から、一番良い数字のカードを引く。"),
                    Component.text("§e適切なカードが見つからなければ引かない。"),
                    Component.text("§eさらに場に置かれている間、相手の賭け数を5つ増やす。")),
                    spcards[sprandom]!!)
                setnbt(item,"sp",6)
                setnbt(item,"betup",5)
                return item
            }

            7->{
                val item = createitem(spcardmaterial,"§6アルティメットドロー", mutableListOf(
                    Component.text("§e山札の中から、一番良い数字のカードを引く。"),
                    Component.text("§e適切なカードが見つからなければ引かない。"),
                    Component.text("§eさらに、spカードを2枚引く。")),

                    spcards[sprandom]!!)
                setnbt(item,"sp",7)
                return item
            }

            8->{
                val item = createitem(spcardmaterial,"§6ベットアップ1", mutableListOf(
                    Component.text("§eSPカードを1枚引く。"),
                    Component.text("§eさらに場に置かれている間、相手の賭け数を1つ増やす。")),
                    spcards[sprandom]!!)
                setnbt(item,"sp",8)
                setnbt(item,"betup",1)
                return item
            }

            9->{
                val item = createitem(spcardmaterial,"§6ベットアップ2", mutableListOf(
                    Component.text("§eSPカードを1枚引く。"),
                    Component.text("§eさらに場に置かれている間、相手の賭け数を2つ増やす。")),
                    spcards[sprandom]!!)
                setnbt(item,"sp",9)
                setnbt(item,"betup",2)
                return item
            }

            10->{
                val item = createitem(spcardmaterial,"§6ベットアップ2+", mutableListOf(
                    Component.text("§e相手の最後にひいたカードを山札に戻す。"),
                    Component.text("§eさらに場に置かれている間、相手の賭け数を2つ増やす。")),
                    spcards[sprandom]!!)
                setnbt(item,"sp",10)
                setnbt(item,"betup",2)
                return item
            }

            11->{
                val item = createitem(spcardmaterial,"§6シールド", mutableListOf(
                    Component.text("§e場に置かれている間、自分の賭け数を1つ減らす。")),
                    spcards[sprandom]!!)
                setnbt(item,"sp",11)
                setnbt(item,"betup",-1)
                return item
            }

            12->{
                val item = createitem(spcardmaterial,"§6シールド+", mutableListOf(
                    Component.text("§e場に置かれている間、自分の賭け数を2つ減らす。")),
                    spcards[sprandom]!!)
                setnbt(item,"sp",12)
                setnbt(item,"betup",-2)
                return item
            }

            13->{
                val item = createitem(spcardmaterial,"§6spチェンジ", mutableListOf(
                    Component.text("§e自分のSPカードをランダムで2枚捨てる。"),
                    Component.text("§eさらにSPカードを3枚引く。")),
                    spcards[sprandom]!!)
                setnbt(item,"sp",13)
                return item
            }

            14->{
                val item = createitem(spcardmaterial,"§6spチェンジ+", mutableListOf(
                    Component.text("§e自分のSPカードをランダムで1枚捨てる。"),
                    Component.text("§eさらにSPカードを3枚引く。")),
                    spcards[sprandom]!!)
                setnbt(item,"sp",14)
                return item
            }

            15->{
                val item = createitem(spcardmaterial,"§6デストロイ++", mutableListOf(
                    Component.text("§e相手の場に出てるspカードを全て消す。"),
                    Component.text("§eさらに、場に置かれている間相手のspカードの仕様を封じる。")),
                    spcards[sprandom]!!)
                setnbt(item,"sp",15)
                return item
            }

            16->{
                val item = createitem(spcardmaterial,"§6ラブ・ユア・エネミー", mutableListOf(
                    Component.text("§e相手は1枚カードを引く。"),
                    Component.text("§eそのカードの数字は、相手にとって一番良い数字が選ばれる。")),
                    spcards[sprandom]!!)
                setnbt(item,"sp",16)
                return item
            }

            17->{
                val item = createitem(spcardmaterial,"§6ハーヴェスト", mutableListOf(
                    Component.text("§e場に置かれている間、SPカードを使う度にSPカードを1枚引く。")),
                    spcards[sprandom]!!)
                setnbt(item,"sp",17)
                return item
            }

            18->{
                val item = createitem(spcardmaterial,"§6エクスチェンジ", mutableListOf(
                    Component.text("§e両プレイヤーがそれぞれ最後に引いたカードを交換する。")),
                    spcards[sprandom]!!)
                setnbt(item,"sp",18)
                return item
            }

            19->{
                val item = createitem(spcardmaterial,"§6ゴール17", mutableListOf(
                    Component.text("§e場に置かれている間、勝利条件を17にする。"),
                    Component.text("§e他の「ゴール」系カードが場にある場合、それを取り除く。")),
                    spcards[sprandom]!!)
                setnbt(item,"sp",19)
                setnbt(item,"goal",17)
                return item
            }

            20->{
                val item = createitem(spcardmaterial,"§6ゴール24", mutableListOf(
                    Component.text("§e場に置かれている間、勝利条件を24にする。"),
                    Component.text("§e他の「ゴール」系カードが場にある場合、それを取り除く。")),
                    spcards[sprandom]!!)
                setnbt(item,"sp",20)
                setnbt(item,"goal",24)
                return item
            }

            21->{
                val item = createitem(spcardmaterial,"§6ゴール27", mutableListOf(
                    Component.text("§e場に置かれている間、勝利条件を27にする。"),
                    Component.text("§e他の「ゴール」系カードが場にある場合、それを取り除く。")),
                    spcards[sprandom]!!)
                setnbt(item,"sp",21)
                setnbt(item,"goal",27)
                return item
            }


            22->{
                val item = createitem(spcardmaterial,"§6リターン",mutableListOf(
                    Component.text("§e自分が最後にひいたカードを山札に戻す。"),
                    Component.text("§e自分の残りのカードが一枚だと使えない。")),
                    spcards[sprandom]!!)
                setnbt(item,"sp",22)
                return item
            }

            23->{
                val item = createitem(spcardmaterial,"§6デスぺレーション",mutableListOf(
                    Component.text("§e場に置かれている間、互いの賭け数が100上がる。"),
                    Component.text("§eまた、相手はカードを引けない。")),
                    spcards[sprandom]!!)
                setnbt(item,"sp",23)
                return item
            }

        }
        return ItemStack(Material.AIR)
    }


    fun spuse(p: UUID, item: ItemStack, slot: Int){

        when(getnbt(item,"sp")){

            1->{
                if (checkplayercard(p) == -1){
                    p.sendmsg("§cあなたはこれ以上カードは引けません！！")
                    return
                }
            }

            2->{
                if (checkenemycard(p) == 6){
                    p.sendmsg("§c相手のカードは1枚しかありません！")
                    return
                }
            }

            3->{
                if (checkenemyspput(p) == 15){
                    p.sendmsg("§c相手はspカードを場に出していません！")
                    return
                }
            }

            5->{
                if (yamahudacheck(p) == null){
                    p.sendmsg("§c山札が空です！")
                    return
                }
                if (checkplayercard(p) == -1){
                    p.sendmsg("§cあなたはこれ以上カードは引けません！！")
                    return
                }
            }

            6->{
                if (yamahudacheck(p) == null){
                    p.sendmsg("§c山札が空です！")
                    return
                }
                if (checkplayercard(p) == -1){
                    p.sendmsg("§cあなたはこれ以上カードは引けません！！")
                    return
                }
                if (checkplayerspput(p) == -1){
                    p.sendmsg("§cもうspカードを場に出すことはできません！")
                    return
                }

            }

            7->{
                if (yamahudacheck(p) == null){
                    p.sendmsg("§c山札が空です！")
                    return
                }
                if (checkplayercard(p) == -1){
                    p.sendmsg("§cあなたはこれ以上カードは引けません！！")
                    return
                }
            }

            8,9,11,12->{
                if (checkplayerspput(p) == -1){
                    p.sendmsg("§cもうspカードを場に出すことはできません！")
                    return
                }
            }

            10->{
                if (checkplayerspput(p) == -1){
                    p.sendmsg("§cもうspカードを場に出すことはできません！")
                    return
                }
                if (checkenemycard(p) == 7){
                    p.sendmsg("§c相手のカードは1枚しかありません！")
                    return
                }
            }

            13->{
                getinv(p).clear(slot)
                tumespcards(p)
                if (checkplayersp(p) < 38 || checkplayersp(p) == -1){
                    p.sendmsg("§cspカードが2枚必要です！")
                    val returnitem = createitem(spcardmaterial,"§6spチェンジ", mutableListOf(
                        Component.text("§e自分のSPカードをランダムで2枚捨てる。"),
                        Component.text("§eさらにSPカードを3枚引く。")),
                        spcards[13]!!)
                    setnbt(returnitem,"sp",13)
                    getinv(p).setItem(checkplayersp(p),returnitem)
                    return
                }
            }

            14->{
                getinv(p).clear(slot)
                tumespcards(p)
                if (checkplayersp(p) < 37 || checkplayersp(p) == -1){
                    p.sendmsg("§cspカードが1枚必要です！")
                    val returnitem = createitem(spcardmaterial,"§6spチェンジ+", mutableListOf(
                        Component.text("§e自分のSPカードをランダムで1枚捨てる。"),
                        Component.text("§eさらにSPカードを3枚引く。")),
                        spcards[14]!!)
                    setnbt(returnitem,"sp",14)
                    getinv(p).setItem(checkplayersp(p),returnitem)
                    return
                }
            }

            15->{
                if (checkenemyspput(p) == 15){
                    p.sendmsg("§c相手はspカードを場に出していません！")
                    return
                }
            }

            16->{
                if (yamahudacheck(p) == null){
                    p.sendmsg("§c山札が空です！")
                    return
                }

                if (checkenemycard(p) == -1){
                    p.sendmsg("§c相手はもうカードを引けません！")
                    return
                }
            }

            17->{

                for (i in 20..24){
                    if (getinv(p).getItem(i) == null)continue
                    if (getnbt(getinv(p).getItem(i)!!,"sp") == 17){
                        p.sendmsg("§cすでにハーヴェストが出されています！")
                        return
                    }
                }
                if (checkplayerspput(p) == -1){
                    p.sendmsg("§cあなたはもうspカードを場に出すことができません！")
                    return
                }
            }

            18->{
                if (checkplayercard(p) == 28){
                    p.sendmsg("§cあなたのカードは1枚しかありません！")
                    return
                }

                if (checkenemycard(p) == 7){
                    p.sendmsg("§c相手のカードは1枚しかありません！")
                    return
                }
            }

            19,20,21->{
                if (checkplayerspput(p) == -1){
                    p.sendmsg("§cあなたはもうspカードを場に出すことができません！")
                    return
                }
            }


            22->{
                if (checkplayercard(p) == 28){
                    p.sendmsg("§cあなたのカードは1枚しかありません！")
                    return
                }
            }

            23->{
                if (checkplayerspput(p) == -1){
                    p.sendmsg("§cあなたはもうspカードを場に出すことができません！")
                    return
                }

                for (i in 20..24){
                    if (getinv(p).getItem(i) == null)continue
                    if (getnbt(getinv(p).getItem(i)!!,"sp") == 23){
                        p.sendmsg("§cすでにデスぺレーションが出されています！")
                        return
                    }
                }
            }


        }


        Bukkit.getScheduler().runTask(plugin, Runnable {
            if (getplayer(p)?.let { JoinGame.key.isDone(it) } == true) getplayer(p)?.awardAdvancement(UseSp.key)
        })

        replaceaction(getinv(p))
        getinv(p).clear(slot)
        getdata(p).action = "spuse"
        if (getdata(p).harvest) getinv(p).setItem(checkplayersp(p), drawspcard(p))

        if (getplayer(p) != null) getplayer(p)?.closeInventory()
        if (getplayer(getenemy(p)) != null) getplayer(getenemy(p))?.closeInventory()

        allplaysound(Sound.ITEM_TOTEM_USE,p)

        allplayersend(p,item.itemMeta.displayName)
        for (l in item.lore!!){
            allplayersend(p,l)
        }
        
        Thread.sleep(2000)

        getplayer(p)?.openInventory(getinv(p))
        getplayer(getdata(p).enemy)?.openInventory(getinv(getdata(p).enemy))

        val inv = getinv(p)
        when(getnbt(item,"sp")){
            1->{//draw系
                val drawint = getnbt(item,"spdraw")

                if (getnbt(getinv(p).getItem(18)!!,"$drawint") == 1){

                    val card = drawcard(p,drawint)
                    inv.setItem(checkplayercard(p),card)
                    getinv(getdata(p).enemy).setItem(checkenemycard(getdata(p).enemy),card)
                    allplayersend(p,"§d${getdata(p).name}は${drawint}のカードを引いた")
                }else{
                    allplayersend(p,"§b${drawint}は山札にないため、除外された")
                }
            }

            2->{//リムーブ
                val enemyslot = if (checkenemycard(p) == -1) 20 else checkenemycard(p) +1
                val enemymyslot = if (checkplayercard(getenemy(p)) == -1) 44 else checkplayercard(getenemy(p)) -1

                allplaysound(Sound.BLOCK_BEACON_ACTIVATE,p)

                setnbt(getinv(getenemy(p)).getItem(18)!!,"${getinv(getenemy(p)).getItem(enemymyslot)
                    ?.let { getnbt(it,"cardnum") }}",1)//ここで山札にnbtを戻す

                setnbt(getinv(p).getItem(18)!!,"${getinv(p).getItem(enemyslot)
                    ?.let { getnbt(it,"cardnum") }}",1)//これも

                inv.clear(enemyslot)//アイテム削除
                getinv(getenemy(p)).clear(enemymyslot)//アイテム(ry
                allplayersend(p,"§d${getdata(getenemy(p)).name}の最後にひいたカードは山札に戻された")

            }

            3->{//デストロイ
                val enemyspslot = if (checkenemyspput(p) == -1) 20 else checkenemyspput(p) +1
                val enemymyspslot = if (checkplayerspput(getenemy(p)) == -1) 11 else checkplayerspput(getenemy(p)) -1

                allplaysound(Sound.ENTITY_GENERIC_EXPLODE,p)

                when(getnbt(inv.getItem(enemyspslot)!!,"sp")){
                    6,8,9,10->{
                        getdata(p).bet -= getnbt(inv.getItem(enemyspslot)!!,"betup")
                        betchange(p)
                    }

                    11,12->{
                        getdata(getenemy(p)).bet -= getnbt(inv.getItem(enemyspslot)!!,"betup")
                        betchange(getenemy(p))
                    }

                    19,20,21->{
                        getdata(p).bjnumber = 21
                        getdata(getenemy(p)).bjnumber = 21
                    }

                    17->{
                        getdata(getenemy(p)).harvest = false
                    }

                    23->{
                        getdata(p).death = false
                        getdata(p).bet -= 100
                        getdata(getenemy(p)).bet -= 100
                        betchange(getenemy(p))
                        betchange(p)
                    }
                }

                allplayersend(p,"§d${getdata(getenemy(p)).name}の最後に出したspカードは消えた")
                inv.clear(enemyspslot)
                getinv(getenemy(p)).clear(enemymyspslot)
            }

            4->{//デストロイ+
                for (i in 15 downTo 11){
                    if (inv.getItem(i) == null)continue
                    when(getnbt(inv.getItem(i)!!,"sp")){
                        6,8,9,10->{
                            getdata(p).bet -= getnbt(inv.getItem(i)!!,"betup")
                            betchange(p)
                        }

                        11,12->{
                            getdata(getenemy(p)).bet -= getnbt(inv.getItem(i)!!,"betup")
                            betchange(getenemy(p))
                        }

                        19,20,21->{
                            getdata(p).bjnumber = 21
                            getdata(getenemy(p)).bjnumber = 21
                        }

                        17->{
                            getdata(getenemy(p)).harvest = false
                        }
                        23->{
                            getdata(p).death = false
                            getdata(p).bet -= 100
                            getdata(getenemy(p)).bet -= 100
                            betchange(getenemy(p))
                            betchange(p)
                        }
                    }
                    inv.clear(i)
                }

                for (i in 20..24){
                    if (getinv(getenemy(p)).getItem(i) == null)continue
                    getinv(getenemy(p)).clear(i)
                }

                allplayersend(p,"§d${getdata(getenemy(p)).name}の出したspカードは消えた")
            }

            5->{//パーフェクトドロー
                val count = countcard(p)
                val yamahuda = yamahudacheck(p)!!

                for (i in getdata(p).bjnumber-count downTo 0){//21-13=8
                    if (i == 0){
                        allplayersend(p,"§b適切なカードが見つからなかったので、カードは引かれなかった")
                        break
                    }
                    if (!yamahuda.contains(i))continue
                    val card = drawcard(p,i)
                    inv.setItem(checkplayercard(p), card)
                    getinv(getenemy(p)).setItem(checkenemycard(getenemy(p)),card)
                    allplaysound(Sound.BLOCK_BEACON_ACTIVATE,p)
                    allplayersend(p,"§d${getdata(p).name}は${i}のカードを引いた")
                    break
                }
            }

            6->{//パーフェクトドロー+
                val count = countcard(p)
                val yamahuda = yamahudacheck(p)!!

                for (i in getdata(p).bjnumber-count downTo 0){
                    if (i == 0){
                        allplayersend(p,"§b適切なカードが見つからなかったので、カードは引かれなかった")
                        break
                    }
                    if (!yamahuda.contains(i))continue
                    val card = drawcard(p,i)
                    inv.setItem(checkplayercard(p), card)
                    getinv(getenemy(p)).setItem(checkenemycard(getenemy(p)),card)
                    allplaysound(Sound.BLOCK_BEACON_ACTIVATE,p)
                    allplayersend(p,"§d${getdata(p).name}は${i}のカードを引いた")
                    break
                }

                inv.setItem(checkplayerspput(p),item)
                getinv(getenemy(p)).setItem(checkenemyspput(getenemy(p)),item)
                getdata(getenemy(p)).bet += getnbt(item,"betup")
                betchange(getenemy(p))
            }

            7->{//アルティメットドロー
                val count = countcard(p)
                val yamahuda = yamahudacheck(p)!!

                for (i in getdata(p).bjnumber-count downTo 0){
                    if (i == 0){
                        allplayersend(p,"§b適切なカードが見つからなかったので、カードは引かれなかった")
                        break
                    }
                    if (!yamahuda.contains(i))continue
                    val card = drawcard(p,i)
                    inv.setItem(checkplayercard(p), card)
                    getinv(getenemy(p)).setItem(checkenemycard(getenemy(p)),card)
                    allplaysound(Sound.BLOCK_BEACON_ACTIVATE,p)
                    allplayersend(p,"§d${getdata(p).name}は${i}のカードを引いた")
                    break
                }

                if (checkplayersp(p) == -1){
                    p.sendmsg("§bspカードはすでに一杯なので受取れませんでした")
                }else{
                    if (checkplayersp(p) == 44){
                        inv.setItem(checkplayersp(p), drawspcard(p))
                        p.sendmsg("§bspカードの空きが少なかったので一枚しか受取れませんでした")
                    }else{
                        inv.setItem(checkplayersp(p), drawspcard(p))
                        inv.setItem(checkplayersp(p), drawspcard(p))
                    }
                }

            }

            8,9->{//ベッドアップ1,2
                val pslot = if (checkplayerspput(p) == -1) 20 else checkplayerspput(p)
                val eslot = if (checkenemyspput(getenemy(p)) == -1) 11 else checkenemyspput(getenemy(p))

                if (checkplayersp(p) == -1){
                    p.sendmsg("§bspカードはすでに一杯なので受取れませんでした")
                }else{
                    inv.setItem(checkplayersp(p), drawspcard(p))
                }
                inv.setItem(pslot, item.clone())
                getinv(getenemy(p)).setItem(eslot,item.clone())
                allplaysound(Sound.ENTITY_LIGHTNING_BOLT_THUNDER,p)

                getdata(getenemy(p)).bet += getnbt(item,"betup")
                betchange(getenemy(p))
                allplayersend(p,"§d${getdata(getenemy(p)).name}の賭け数が${getnbt(item,"betup")}増加した")

            }

            10->{//ベットアップ2+
                val enemyslot = if (checkenemycard(p) == -1) 20 else checkenemycard(p) +1
                val enemymyslot = if (checkplayercard(getenemy(p)) == -1) 44 else checkplayercard(getenemy(p)) -1

                allplaysound(Sound.BLOCK_BEACON_DEACTIVATE,p)

                setnbt(getinv(getenemy(p)).getItem(18)!!,"${getinv(getenemy(p)).getItem(enemymyslot)
                    ?.let { getnbt(it,"cardnum") }}",1)//ここで山札にnbtを戻す

                setnbt(getinv(p).getItem(18)!!,"${getinv(p).getItem(enemyslot)
                    ?.let { getnbt(it,"cardnum") }}",1)//これも

                inv.clear(enemyslot)//アイテム削除
                getinv(getenemy(p)).clear(enemymyslot)//アイテム(ry
                allplayersend(p,"§d${getdata(getenemy(p)).name}の最後にひいたカードは山札に戻された")


                val pslot = if (checkplayerspput(p) == -1) 20 else checkplayerspput(p)
                val eslot = if (checkenemyspput(getenemy(p)) == -1) 11 else checkenemyspput(getenemy(p))

                inv.setItem(pslot, item.clone())
                getinv(getenemy(p)).setItem(eslot,item.clone())
                allplaysound(Sound.ENTITY_LIGHTNING_BOLT_THUNDER,p)

                getdata(getenemy(p)).bet += getnbt(item,"betup")
                betchange(getenemy(p))

            }

            11,12->{//シールド,+
                val pslot = if (checkplayerspput(p) == -1) 20 else checkplayerspput(p)
                val eslot = if (checkenemyspput(getenemy(p)) == -1) 11 else checkenemyspput(getenemy(p))

                inv.setItem(pslot, item.clone())
                getinv(getenemy(p)).setItem(eslot,item.clone())
                allplaysound(Sound.ENTITY_ARROW_HIT_PLAYER,p)

                getdata(p).bet += getnbt(item,"betup")
                betchange(p)
            }

            13->{//spチェンジ
                for (i in 1..2){
                    val delete = if (checkplayersp(p) == -1) 44 else checkplayersp(p)-1
                    inv.clear(delete)
                }
                for (i in 1..3){
                    if (checkplayersp(p) == -1)break
                    inv.setItem(checkplayersp(p), drawspcard(p))
                }
                allplaysound(Sound.BLOCK_IRON_DOOR_OPEN,p)
            }

            14->{//spチェンジ+
                val delete = if (checkplayersp(p) == -1) 44 else checkplayersp(p)-1
                inv.clear(delete)
                for (i in 1..3){
                    if (checkplayersp(p) == -1)break
                    inv.setItem(checkplayersp(p), drawspcard(p))
                }
                allplaysound(Sound.BLOCK_IRON_DOOR_OPEN,p)
            }

            15->{//デストロイ++
                for (i in 15 downTo 11){
                    if (inv.getItem(i) == null)continue
                    when(getnbt(inv.getItem(i)!!,"sp")){
                        6,8,9,10->{
                            getdata(p).bet -= getnbt(inv.getItem(i)!!,"betup")
                            betchange(p)
                        }

                        11,12->{
                            getdata(getenemy(p)).bet -= getnbt(inv.getItem(i)!!,"betup")
                            betchange(getenemy(p))
                        }

                        19,20,21->{
                            getdata(p).bjnumber = 21
                            getdata(getenemy(p)).bjnumber = 21
                        }

                        17->{
                            getdata(getenemy(p)).harvest = false
                        }

                        23->{
                            getdata(p).death = false
                            getdata(p).bet -= 100
                            getdata(getenemy(p)).bet -= 100
                            betchange(getenemy(p))
                            betchange(p)
                        }
                    }
                    inv.clear(i)
                }

                for (i in 20..24){
                    if (getinv(getenemy(p)).getItem(i) == null)continue
                    getinv(getenemy(p)).clear(i)
                }

                getinv(p).setItem(checkplayerspput(p),item)
                getinv(getenemy(p)).setItem(checkenemyspput(getenemy(p)),item)
                allplaysound(Sound.ENTITY_GENERIC_EXPLODE,p)
                allplayersend(p,"§d${getdata(getenemy(p)).name}の出したspカードは消えた")

                getdata(getenemy(p)).spuse = false

            }

            16->{//ラブ・ユア・エネミー
                val count = countcard(getenemy(p))
                val yamahuda = yamahudacheck(getenemy(p))!!

                if (count >= getdata(p).bjnumber){
                    for (int in 1..12){
                        if (int == 12){
                            allplayersend(p,"§b適切なカードが見つからなかったので、カードは引かれなかった")
                            break
                        }
                        if (!yamahuda.contains(int))continue
                        val card = drawcard(getenemy(p),int)
                        getinv(getenemy(p)).setItem(checkplayercard(getenemy(p)), card)
                        inv.setItem(checkenemycard(p),card)
                        allplayersend(p,"§d${getdata(getenemy(p)).name}は${int}のカードを引いた")
                        allplaysound(Sound.ENTITY_SHEEP_AMBIENT,p)
                        break
                    }
                }else{
                    for (i in getdata(getenemy(p)).bjnumber-count downTo 0){//21-13=8
                        if (i == 0){
                            allplayersend(p,"§b適切なカードが見つからなかったので、カードは引かれなかった")
                            break
                        }
                        if (!yamahuda.contains(i))continue
                        val card = drawcard(getenemy(p),i)
                        getinv(getenemy(p)).setItem(checkplayercard(getenemy(p)), card)
                        inv.setItem(checkenemycard(p),card)
                        allplayersend(p,"§d${getdata(getenemy(p)).name}は${i}のカードを引いた")
                        allplaysound(Sound.ENTITY_SHEEP_AMBIENT,p)
                        break
                    }
                }


            }

            17->{//ハーヴェスト
                inv.setItem(checkplayerspput(p),item)
                getinv(getenemy(p)).setItem(checkenemyspput(getenemy(p)),item)
                getdata(p).harvest = true
                allplaysound(Sound.BLOCK_SLIME_BLOCK_BREAK,p)
                allplayersend(p,"§d${getplayer(p)?.name}はspカード使用時にspカードを引くようになった")
            }

            18->{//エクスチェンジ
                val enemyslot = if (checkenemycard(p) == -1) 20 else checkenemycard(p) +1
                val enemymyslot = if (checkplayercard(getenemy(p)) == -1) 44 else checkplayercard(getenemy(p)) -1

                val myslot = if (checkplayercard(p) == -1) 44 else checkplayercard(p) -1
                val enemysslot = if (checkenemycard(getenemy(p)) == -1) 20 else checkenemycard(getenemy(p)) +1

                val item1 = inv.getItem(myslot)?.clone()
                val item2 = inv.getItem(enemyslot)?.clone()

                inv.setItem(myslot,item2)
                inv.setItem(enemyslot,item1)

                getinv(getenemy(p)).setItem(enemymyslot,item1)
                getinv(getenemy(p)).setItem(enemysslot,item2)
                allplaysound(Sound.BLOCK_BEACON_ACTIVATE,p)
                allplayersend(p,"§d最後にひいたカードを入れ替わった")
            }

            19,20,21->{//ゴール17,24,27  //15..11
                for (i in 15 downTo 11){
                    if (inv.getItem(i) == null)continue
                    if (getnbt(inv.getItem(i)!!,"sp") in 19..21){
                        inv.clear(i)
                    }
                }

                for (i in 20..24){
                    if (getinv(getenemy(p)).getItem(i) == null)continue
                    if (getnbt(getinv(getenemy(p)).getItem(i)!!,"sp") in 19..21){
                        getinv(getenemy(p)).clear(i)
                    }
                }

                getdata(p).bjnumber = getnbt(item,"goal")
                getdata(getenemy(p)).bjnumber = getnbt(item,"goal")
                inv.setItem(checkplayerspput(p),item)
                getinv(getenemy(p)).setItem(checkenemyspput(p),item)

                allplaysound(Sound.BLOCK_PISTON_CONTRACT,p)
                allplayersend(p,"§dゴールが${getnbt(item,"goal")}になった")
            }

            22->{//リターン
                val myslot = if (checkplayercard(p) == -1) 44 else checkplayercard(p) -1
                val enemyslot = if (checkenemycard(getenemy(p)) == -1) 20 else checkenemycard(getenemy(p)) +1

                allplaysound(Sound.BLOCK_BEACON_ACTIVATE,p)

                setnbt(getinv(getenemy(p)).getItem(18)!!,"${getinv(getenemy(p)).getItem(enemyslot)
                    ?.let { getnbt(it,"cardnum") }}",1)//ここで山札にnbtを戻す

                setnbt(getinv(p).getItem(18)!!,"${getinv(p).getItem(myslot)
                    ?.let { getnbt(it,"cardnum") }}",1)//これも

                inv.clear(myslot)//アイテム削除
                getinv(getenemy(p)).clear(enemyslot)//これも
                allplayersend(p,"§d${getplayer(p)?.name}の最後にひいたカードは山札に戻された")
            }

            23->{
                Bukkit.getScheduler().runTask(plugin, Runnable {
                    if (getplayer(p)?.let { LoginServer.key.isDone(it) } == true) getplayer(p)?.awardAdvancement(DeathGame.key)
                })
                inv.setItem(checkplayerspput(p),item)
                getinv(getenemy(p)).setItem(checkenemyspput(p),item)
                getdata(getenemy(p)).bet += 100
                getdata(p).bet += 100
                getdata(getenemy(p)).death = true
                betchange(getenemy(p))
                betchange(p)
                allplayersend(p,"§d互いの賭け数が100になり、${getdata(getenemy(p)).name}はカードを引けなくなった")
            }


        }

        tumespcards(p)
        getdata(getenemy(p)).through = false

        return
    }

    fun drawcard(p : UUID, invisible : Boolean) : ItemStack? {
        if (getdata(p).death)return null
        val cardnum = yamahudacheck(p)?.random()?:return null
        val item = createitem(cardmaterial,cardnum.toString(), if (invisible) invisiblecardcsm else cardcsm[cardnum-1])
        setnbt(item,"cardnum",cardnum)
        setnbt(getinv(p).getItem(18)!!,"$cardnum",0)
        setnbt(getinv(getdata(p).enemy).getItem(18)!!,"$cardnum",0)
        return item
    }

    fun drawcard(p : UUID, card : Int) : ItemStack {
        val item = createitem(cardmaterial,card.toString(), cardcsm[card-1])
        setnbt(item,"cardnum",card)
        setnbt(getinv(p).getItem(18)!!,"$card",0)
        setnbt(getinv(getdata(p).enemy).getItem(18)!!,"$card",0)
        return item
    }

    fun nullcarddis(item : ItemStack): ItemStack {
        val meta = item.itemMeta
        meta.displayName(Component.text("§l？"))
        item.itemMeta = meta
        return item
    }

    fun replaceaction(inv: Inventory){
        fillair(inv,52..53)
        return
    }

    fun fillaction(inv: Inventory){
        inv.setItem(52,createitem(Material.BLACK_STAINED_GLASS_PANE,"§f§lカードを引く"))
        inv.setItem(53,createitem(Material.GREEN_STAINED_GLASS_PANE,"§a§lカードを引かない"))
        return
    }


    fun betchange(p: UUID){//変える側
        getinv(p).setItem(9,createitem(Material.GOLD_NUGGET,"§c${getdata(p).name}の賭け数/チップ", mutableListOf(Component.text("§e${getdata(p).bet}/${getdata(p).tipcoin}枚"))))
        getinv(getdata(p).enemy).setItem(26,createitem(Material.GOLD_NUGGET,"§c${getdata(p).name}の賭け数/チップ", mutableListOf(Component.text("§e${getdata(p).bet}/${getdata(p).tipcoin}枚"))))
        return
    }

    fun tumespcards(p : UUID){
        val itemlist = ArrayList<ItemStack>()
        for (i in 36..44){
            if (getinv(p).getItem(i) != null)itemlist.add(getinv(p).getItem(i)!!)
            getinv(p).clear(i)
        }

        for (i in itemlist){
            getinv(p).setItem(checkplayersp(p),i)
        }

        return
    }

}
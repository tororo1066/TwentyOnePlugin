package twentyoneplugin.twentyoneplugin

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.annotations.Nullable
import twentyoneplugin.twentyoneplugin.Inventory.checkenemycard
import twentyoneplugin.twentyoneplugin.Inventory.checkplayercard
import twentyoneplugin.twentyoneplugin.Inventory.checkplayersp
import twentyoneplugin.twentyoneplugin.Inventory.countcard
import twentyoneplugin.twentyoneplugin.Inventory.createitem
import twentyoneplugin.twentyoneplugin.Inventory.drawcard
import twentyoneplugin.twentyoneplugin.Inventory.drawspcard
import twentyoneplugin.twentyoneplugin.Inventory.fillaction
import twentyoneplugin.twentyoneplugin.Inventory.getinv
import twentyoneplugin.twentyoneplugin.Inventory.intrangeitem
import twentyoneplugin.twentyoneplugin.Inventory.nullcarddis
import twentyoneplugin.twentyoneplugin.Inventory.replaceaction
import twentyoneplugin.twentyoneplugin.Inventory.setallplayer
import twentyoneplugin.twentyoneplugin.Inventory.yamahudacheck
import twentyoneplugin.twentyoneplugin.TOP.Companion.plugin
import twentyoneplugin.twentyoneplugin.TOP.Companion.spcards
import java.util.*

object Util {
    private val datamap = TOP.datamap
    private const val prefix = TOP.prefix

    fun Player.sendmsg(s : String){
        this.sendMessage(prefix + s)
        return
    }

    fun per(i : Double): Boolean {
        return Math.random() <= i/100
    }

    fun Player.playsound(sound : Sound){
        this.playSound(this.location,sound,2f,2f)
        return
    }
    fun Player.playsound(sound : String){
        this.playSound(this.location,sound,2f,2f)
        return
    }
    fun allplaysound(sound: Sound, uuid: UUID){
        getplayer(uuid).playsound(sound)
        getplayer(getdata(uuid).enemy).playsound(sound)
        return
    }
    fun allplaysound(sound: String, uuid: UUID){
        getplayer(uuid).playsound(sound)
        getplayer(getdata(uuid).enemy).playsound(sound)
        return
    }

    fun getplayer(p : UUID): @Nullable Player {
        return Bukkit.getPlayer(p)!!
    }

    fun getdata(p : UUID): PlayerData {
        return datamap[p]!!
    }



    fun gamestart(startplayer : UUID,joinplayer : UUID){
        val startinv = getinv(startplayer)
        val joininv = getinv(joinplayer)
        getplayer(startplayer).openInventory(startinv)
        getplayer(joinplayer).openInventory(joininv)
        Thread{
            startinv.setItem(checkplayersp(startplayer), drawspcard())
            joininv.setItem(checkplayersp(joinplayer), drawspcard())
            allplaysound(Sound.BLOCK_ANVIL_PLACE,startplayer)
            Thread.sleep(1000)

            var card = drawcard(startplayer,true)?:return@Thread
            startinv.setItem(checkplayercard(startplayer), card)
            nullcarddis(card)
            joininv.setItem(checkenemycard(joinplayer), card)
            allplaysound(Sound.ITEM_BOOK_PAGE_TURN,startplayer)

            Thread.sleep(1000)

            card = drawcard(joinplayer,true)?:return@Thread
            joininv.setItem(checkplayercard(joinplayer), card)
            nullcarddis(card)
            startinv.setItem(checkenemycard(startplayer), card)
            allplaysound(Sound.ITEM_BOOK_PAGE_TURN,startplayer)


            Thread.sleep(1000)

            card = drawcard(startplayer,false)?:return@Thread
            startinv.setItem(checkplayercard(startplayer), card)
            joininv.setItem(checkenemycard(joinplayer), card)
            allplaysound(Sound.ITEM_BOOK_PAGE_TURN,startplayer)

            Thread.sleep(1000)

            card = drawcard(joinplayer,false)?:return@Thread
            joininv.setItem(checkplayercard(joinplayer), card)
            startinv.setItem(checkenemycard(startplayer), card)
            allplaysound(Sound.ITEM_BOOK_PAGE_TURN,startplayer)

            fillaction(startinv)
        }.start()


    }

    fun timecount(p: UUID, time : Int) : Boolean{ //どちらでも可
        val inv = getinv(p)
        val eninv = getinv(getdata(p).enemy)
        if (inv.getItem(17) == null)return false
        inv.setItem(17, ItemStack(Material.CLOCK,time))
        eninv.setItem(17, ItemStack(Material.CLOCK,time))
        allplaysound(Sound.BLOCK_STONE_BUTTON_CLICK_ON,p)
        if (inv.getItem(17)?.amount == 1)return false
        return true
    }


    fun endtwoturn(p : UUID): Boolean? { //どちらでも可 pの勝利はtrue、敗北はfalse、drowはnull
        val pcount = countcard(p)
        val enemycount = countcard(getdata(p).enemy)
        if (pcount == enemycount) return null
        if (pcount > getdata(p).bjnumber && enemycount > getdata(p).bjnumber)
            return pcount <= enemycount
        return if (pcount > enemycount){
            pcount < getdata(p).bjnumber
        }else{
            enemycount >= getdata(p).bjnumber
        }
    }

    fun win(p : UUID){ //指定したプレイヤーを勝利にする
        val inv = Bukkit.createInventory(null,54, Component.text("21Result"))
        intrangeitem(inv, createitem(Material.BLACK_STAINED_GLASS_PANE,"§6§l${getplayer(p).name}の勝利！", mutableListOf(Component.text("§e${getplayer(p).name}は"))),0..53)
        Bukkit.getScheduler().runTask(plugin, Runnable {
            getplayer(p).openInventory(inv)
            getplayer(getdata(p).enemy).openInventory(inv)
        })
        Thread.sleep(3000)
        return
    }

    fun drow(p: UUID){
        val inv = Bukkit.createInventory(null,54, Component.text("21Result"))
        intrangeitem(inv, createitem(Material.BLACK_STAINED_GLASS_PANE,"§l引き分け", mutableListOf(Component.text("§e${getplayer(p).name}は"))),0..53)
        Bukkit.getScheduler().runTask(plugin, Runnable {
            getplayer(p).openInventory(inv)
            getplayer(getdata(p).enemy).openInventory(inv)
        })
        Thread.sleep(3000)
    }




}
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
import twentyoneplugin.twentyoneplugin.Inventory.replaceaction
import twentyoneplugin.twentyoneplugin.Inventory.setallplayer
import twentyoneplugin.twentyoneplugin.Inventory.yamahudacheck
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
            Thread.sleep(10000)
            var card = drawcard(yamahudacheck(startplayer)?: return@Thread,true)
            startinv.setItem(28, card)
            joininv.setItem(7, card)
            allplaysound(Sound.ITEM_BOOK_PAGE_TURN,startplayer)
            Thread.sleep(10000)
            card = drawcard(yamahudacheck(startplayer)?:return@Thread,false)
            startinv.setItem(checkplayercard(startplayer), card)
            joininv.setItem(checkenemycard(startplayer), card)
            allplaysound(Sound.ITEM_BOOK_PAGE_TURN,startplayer)
            timecount(startplayer)
            fillaction(startplayer)
        }.start()


    }

    private fun timecount(p: UUID){ //どちらでも可
        if (datamap[p] == null)return
        val inv = getinv(p)
        val eninv = getinv(getdata(p).enemy)
        if (inv.getItem(17)?.amount!! == 0){
            turnchange(getdata(p).enemy)
        }
        inv.setItem(17, ItemStack(Material.CLOCK,inv.getItem(17)?.amount!!.minus(1)))
        eninv.setItem(17, ItemStack(Material.CLOCK,inv.getItem(17)?.amount!!.minus(1)))
        allplaysound(Sound.BLOCK_STONE_BUTTON_CLICK_ON,p)
        timecount(p)
        return
    }

    fun turnchange(p: UUID){ //ターンをチェンジする対象を指定
        if (getdata(p).through && getdata(getdata(p).enemy).through){
            endtwoturn(p)
            return
        }
        getdata(p).through = true
        replaceaction(getdata(p).enemy)
        fillaction(p)
        setallplayer(p,17, ItemStack(Material.CLOCK, TOP.plugin.config.getInt("clocktime")))
        return

    }

    fun endtwoturn(p : UUID){ //どちらでも可
        val pcount = countcard(p)
        val enemycount = countcard(getdata(p).enemy)
        if (pcount == enemycount) drow(p)
        if (pcount > getdata(p).bjnumber && enemycount > getdata(p).bjnumber)
            if (pcount > enemycount) win(getdata(p).enemy) else win(p)
        if (pcount > enemycount){
            if (pcount < getdata(p).bjnumber) win(p) else win(getdata(p).enemy)
        }else{
            if (enemycount < getdata(p).bjnumber) win(getdata(p).enemy) else win(p)
        }
        return
    }

    fun win(p : UUID){ //指定したプレイヤーを勝利にする
        val inv = Bukkit.createInventory(null,54, Component.text("§0§l§kaaa§5§l2§0§l§kaa§6§l1§0§l§kaaa"))
        intrangeitem(inv, createitem(Material.BLACK_STAINED_GLASS_PANE,"§6${getplayer(p).name}の勝利！", mutableListOf(Component.text("§e${getplayer(p).name}は"))),0..53)
        getdata(p).gamecount+=1
        getdata(getdata(p).enemy).gamecount+=1
        if (getdata(p).gamecount == 2)return
        gamestart(p, getdata(p).enemy)
    }

    fun drow(p: UUID){

    }




}
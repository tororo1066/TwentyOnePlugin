package twentyoneplugin.twentyoneplugin

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.advancement.Advancement
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.checkerframework.checker.nullness.qual.NonNull
import org.jetbrains.annotations.Nullable
import twentyoneplugin.twentyoneplugin.AdvancementUtils.Companion.awardAdvancement
import twentyoneplugin.twentyoneplugin.Inventory.countcard
import twentyoneplugin.twentyoneplugin.Inventory.createitem
import twentyoneplugin.twentyoneplugin.Inventory.getinv
import twentyoneplugin.twentyoneplugin.Inventory.intrangeitem
import twentyoneplugin.twentyoneplugin.TOP.Companion.plugin
import twentyoneplugin.twentyoneplugin.advancements.Complete21
import twentyoneplugin.twentyoneplugin.advancements.LoginServer
import twentyoneplugin.twentyoneplugin.advancements.WinGame
import java.util.*
import kotlin.math.floor

object Util {
    private val datamap = TOP.datamap
    private const val prefix = TOP.prefix

    fun Player.sendmsg(s : String){
        this.sendMessage(prefix + s)
        return
    }

    fun UUID.sendmsg(s : String){
        getplayer(this)?.sendmsg(s)
    }

    fun format(double: Double):String{
        return String.format("%,.0f",double)
    }

    fun allplayersend(p : UUID, s : String){
        getplayer(p)?.sendMessage(s)
        getplayer(getdata(p).enemy)?.sendMessage(s)
        return
    }


    fun per(i : Double): Boolean {
        return Math.random() <= i/100
    }

    private fun clocksound(uuid: UUID){
        getplayer(uuid)?.location?.let { getplayer(uuid)?.playSound(it,Sound.BLOCK_STONE_BUTTON_CLICK_ON,2f,2f) }
        getplayer(getenemy(uuid))?.location?.let { getplayer(getenemy(uuid))?.playSound(it,Sound.BLOCK_STONE_BUTTON_CLICK_ON,2f,2f) }
    }

    fun Player.playsound(sound : Sound){
        this.playSound(this.location,sound,2f,1f)
        return
    }

    fun allplaysound(sound: Sound, uuid: UUID){
        getplayer(uuid)?.playsound(sound)
        getplayer(getdata(uuid).enemy)?.playsound(sound)
        return
    }


    fun getplayer(p : UUID): @Nullable Player? {
        return Bukkit.getPlayer(p)
    }

    fun getdata(p : UUID): PlayerData {
        return datamap[p]!!
    }

    fun runcmd(s : String, cmd : String, hover : String): @NonNull Component {
        return Component.text(s).clickEvent(ClickEvent.runCommand(cmd)).hoverEvent(HoverEvent.showText(Component.text(hover))).asComponent()
    }

    fun hokancmd(s : String, cmd: String, hover: String): @NonNull Component {
        return Component.text(s).clickEvent(ClickEvent.suggestCommand(cmd)).hoverEvent(HoverEvent.showText(Component.text(hover))).asComponent()
    }

    fun getenemy(p : UUID) : UUID{
        return datamap[p]?.enemy!!
    }

    fun NamespacedKey.isDone(p : Player): Boolean {
        return p.getAdvancementProgress(Bukkit.getAdvancement(this)!!).isDone
    }




    fun timecount(p: UUID, time : Int) : Boolean{ //どちらでも可
        val inv = getinv(p)
        val eninv = getinv(getdata(p).enemy)
        if (inv.getItem(17) == null)return false
        inv.setItem(17, ItemStack(Material.CLOCK,time))
        eninv.setItem(17, ItemStack(Material.CLOCK,time))
        clocksound(p)
        if (inv.getItem(17)?.amount == 1)return false
        return true
    }


    fun endtwoturn(p : UUID): Boolean? { //どちらでも可 pの勝利はtrue、敗北はfalse、drawはnull
        val pcount = countcard(p)
        val enemycount = countcard(getdata(p).enemy)
        if (pcount == enemycount) return null
        if (pcount > getdata(p).bjnumber && enemycount > getdata(p).bjnumber)
            return null
        if (pcount > getdata(p).bjnumber)return false
        if (enemycount > getdata(p).bjnumber)return true
        return pcount > enemycount
    }

    fun win(p : UUID){ //指定したプレイヤーを勝利にする

        val inv = Bukkit.createInventory(null,54, Component.text("BJPResult"))
        intrangeitem(inv, createitem(Material.BLACK_STAINED_GLASS_PANE,"§6§l${getdata(p).name}の勝利！", mutableListOf(Component.text("§e${getdata(p).name}の合計：${countcard(p)}"),
            Component.text("§e${getdata(getenemy(p)).name}の合計：${countcard(getdata(p).enemy)}"))),0..53)
        Bukkit.getScheduler().runTask(plugin, Runnable {
            if (getplayer(p) != null) getplayer(p)?.openInventory(inv)
            if (getplayer(getdata(p).enemy) != null) getplayer(getdata(p).enemy)?.openInventory(inv)
            if (getplayer(p)?.let { LoginServer.key.isDone(it) } == true && !getdata(p).customsetting && countcard(p) == getdata(p).bjnumber) getplayer(p)?.awardAdvancement(Complete21.key)
        })
        Thread.sleep(5000)
        return
    }

    fun draw(p: UUID){
        val inv = Bukkit.createInventory(null,54, Component.text("BJPResult"))
        intrangeitem(inv, createitem(Material.BLACK_STAINED_GLASS_PANE,"§l引き分け", mutableListOf(Component.text("§e${getdata(p).name}の合計：${countcard(p)}"),
            Component.text("§e${getdata(getenemy(p)).name}の合計：${countcard(getdata(p).enemy)}"))),0..53)
        Bukkit.getScheduler().runTask(plugin, Runnable {
            if (getplayer(p) != null) getplayer(p)?.openInventory(inv)
            if (getplayer(getdata(p).enemy) != null) getplayer(getdata(p).enemy)?.openInventory(inv)
        })
        Thread.sleep(5000)
        return
    }

    fun gamelatersetting(p : UUID, battle : Boolean?): Boolean {//trueだとpの勝利、falseだとenemyの勝利、nullでdraw
        getdata(p).death = false
        getdata(getenemy(p)).death = false
        getdata(p).through = false
        getdata(getenemy(p)).through = false
        getdata(p).spuse = true
        getdata(getenemy(p)).spuse = true
        getdata(p).harvest = false
        getdata(getenemy(p)).harvest = false
        if (battle?:return true){
            return if (getdata(getenemy(p)).tipcoin - getdata(getenemy(p)).bet <= 0){
                getdata(p).tipcoin += getdata(getenemy(p)).tipcoin
                getdata(getenemy(p)).tipcoin = 0
                false
            }else{
                getdata(getdata(p).enemy).tipcoin -= getdata(getdata(p).enemy).bet
                getdata(p).tipcoin += getdata(getdata(p).enemy).bet
                true
            }
        }else{
            return if (getdata(p).tipcoin - getdata(p).bet <= 0){
                getdata(getdata(p).enemy).tipcoin += getdata(p).tipcoin
                getdata(p).tipcoin = 0
                false
            }else{
                getdata(p).tipcoin -= getdata(p).bet
                getdata(getdata(p).enemy).tipcoin += getdata(p).bet
                true
            }
        }
    }

    fun getLog(uuid : UUID): LogData {
        val mysql = MySQLManager(plugin, "bjpGetLog")
        val rs = mysql.query("SELECT * FROM bjp_battle_log WHERE uuid = '${uuid}';")
        if (rs == null || !rs.next()) {
            rs?.close()
            mysql.close()
            return LogData()
        }
        val win = rs.getInt("win")
        val draw = rs.getInt("draw")
        val lose = rs.getInt("lose")
        val winper =
            floor((win.toDouble() / (win.toDouble() + draw.toDouble() + lose.toDouble())) * 100)

        val logdata = LogData()
        logdata.mcid = rs.getString("mcid")
        logdata.uuid = uuid
        logdata.win = win
        logdata.draw = draw
        logdata.lose = lose
        logdata.winper = winper

        rs.close()
        mysql.close()

        return logdata
    }

    class LogData{
        var mcid = ""
        lateinit var uuid : UUID
        var win = 0
        var draw = 0
        var lose = 0
        var winper = 0.0
    }


}
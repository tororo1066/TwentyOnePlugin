package twentyoneplugin.twentyoneplugin

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.*
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.FireworkMeta
import org.bukkit.util.Vector
import org.checkerframework.checker.nullness.qual.NonNull
import org.jetbrains.annotations.Nullable
import twentyoneplugin.twentyoneplugin.Inventory.countcard
import twentyoneplugin.twentyoneplugin.Inventory.createitem
import twentyoneplugin.twentyoneplugin.Inventory.getinv
import twentyoneplugin.twentyoneplugin.Inventory.intrangeitem
import twentyoneplugin.twentyoneplugin.TOP.Companion.plugin
import java.util.*
import kotlin.random.Random

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

    fun allplayersend(p : UUID, s : String){
        getplayer(p)!!.sendMessage(s)
        getplayer(getdata(p).enemy)!!.sendMessage(s)
        return
    }


    fun per(i : Double): Boolean {
        return Math.random() <= i/100
    }

    fun Player.playsound(sound : Sound){
        this.playSound(this.location,sound,3f,1f)
        return
    }
    fun Player.playsound(sound : String){
        this.playSound(this.location,sound,3f,1f)
        return
    }
    fun allplaysound(sound: Sound, uuid: UUID){
        getplayer(uuid)!!.playsound(sound)
        getplayer(getdata(uuid).enemy)!!.playsound(sound)
        return
    }
    fun allplaysound(sound: String, uuid: UUID){
        getplayer(uuid)!!.playsound(sound)
        getplayer(getdata(uuid).enemy)!!.playsound(sound)
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
        val inv = Bukkit.createInventory(null,54, Component.text("21Result"))
        intrangeitem(inv, createitem(Material.BLACK_STAINED_GLASS_PANE,"§6§l${getplayer(p)?.name}の勝利！", mutableListOf(Component.text("§e${getplayer(p)?.name}の合計：${countcard(p)}"),
            Component.text("§e${getplayer(getdata(p).enemy)?.name}の合計：${countcard(getdata(p).enemy)}"))),0..53)
        Bukkit.getScheduler().runTask(plugin, Runnable {
            getplayer(p)?.openInventory(inv)!!
            getplayer(getdata(p).enemy)?.openInventory(inv)!!

        })
        Thread.sleep(5000)
        return
    }

    fun draw(p: UUID){
        val inv = Bukkit.createInventory(null,54, Component.text("21Result"))
        intrangeitem(inv, createitem(Material.BLACK_STAINED_GLASS_PANE,"§l引き分け", mutableListOf(Component.text("§e${getplayer(p)?.name}の合計：${countcard(p)}"),
            Component.text("§e${getplayer(getdata(p).enemy)?.name}の合計：${countcard(getdata(p).enemy)}"))),0..53)
        Bukkit.getScheduler().runTask(plugin, Runnable {
            getplayer(p)?.openInventory(inv)!!
            getplayer(getdata(p).enemy)?.openInventory(inv)!!
        })
        Thread.sleep(5000)
        return
    }

    fun gamelatersetting(p : UUID, battle : Boolean?): Boolean {//trueだとpの勝利、falseだとenemyの勝利、nullでdraw
        getdata(p).death = false
        getdata(getenemy(p)).death = false
        getdata(p).through = false
        getdata(getdata(p).enemy).through = false
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

    fun spawnFireworks(location: Location, amount: Int) {
        val loc: Location = location
        val fw: Firework = loc.world.spawnEntity(loc, EntityType.FIREWORK) as Firework
        val fwm: FireworkMeta = fw.fireworkMeta
        fwm.power = 2
        fwm.addEffect(FireworkEffect.builder().withColor(Color.YELLOW).flicker(true).build())
        fw.fireworkMeta = fwm
        fw.detonate()
        for (i in 0 until amount) {
            val fw2: Firework = loc.world.spawnEntity(loc.add(Vector (Random.nextDouble(-2.0,2.0),0.0, Random.nextDouble(-2.0,2.0))), EntityType.FIREWORK) as Firework
            fw2.fireworkMeta = fwm
        }
    }




}
package twentyoneplugin.twentyoneplugin

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.Sound
import twentyoneplugin.twentyoneplugin.Inventory.fillaction
import twentyoneplugin.twentyoneplugin.Inventory.getinv
import twentyoneplugin.twentyoneplugin.TOP.Companion.canjoin
import twentyoneplugin.twentyoneplugin.TOP.Companion.datamap
import twentyoneplugin.twentyoneplugin.TOP.Companion.plugin
import twentyoneplugin.twentyoneplugin.Util.getdata
import twentyoneplugin.twentyoneplugin.Util.getplayer
import twentyoneplugin.twentyoneplugin.Util.timecount
import java.util.*
import kotlin.random.Random

class TwentyOne(private val player : UUID) : Thread(){


    override fun run() {
        plugin.logger.info("testttt")
        for (i in 59 downTo 0){
            if (!canjoin.contains(player))break
            if (i == 0){
                Bukkit.broadcast(Component.text("§l${getplayer(player).name}§aの§521§aは人が集まらなかったので中止しました"), Server.BROADCAST_CHANNEL_USERS)
                VaultManager(plugin).deposit(player, getdata(player).tip * plugin.config.getInt("tipcoin"))
                return
            }
            if (i % 10 == 0) Bukkit.broadcast(Component.text("§l${getplayer(player).name}§aが§5§l21§aを募集中...残り${i}秒\n" +
                    "§f/21 join ${getplayer(player).name} §4最低必須金額 ${getdata(player).tip * plugin.config.getInt("tipcoin")}"),Server.BROADCAST_CHANNEL_USERS)
            sleep(1000)
        }



        for (loops in 1..plugin.config.getInt("round")){
            val startplayer = player
            val joinplayer = getdata(player).enemy
            val startinv = getinv(startplayer)
            val joininv = getinv(joinplayer)
            Bukkit.getScheduler().runTask(plugin, Runnable {
                getplayer(player).openInventory(startinv)
                getplayer(getdata(player).enemy).openInventory(joininv)
            })
            startinv.setItem(Inventory.checkplayersp(startplayer), Inventory.drawspcard())
            joininv.setItem(Inventory.checkplayersp(joinplayer), Inventory.drawspcard())
            Util.allplaysound(Sound.BLOCK_ANVIL_PLACE, startplayer)
            sleep(1000)

            var card = Inventory.drawcard(startplayer, true) ?:return
            startinv.setItem(Inventory.checkplayercard(startplayer), card)
            Inventory.nullcarddis(card)
            joininv.setItem(Inventory.checkenemycard(joinplayer), card)
            Util.allplaysound(Sound.ITEM_BOOK_PAGE_TURN, startplayer)

            sleep(1000)

            card = Inventory.drawcard(joinplayer, true) ?:return
            joininv.setItem(Inventory.checkplayercard(joinplayer), card)
            Inventory.nullcarddis(card)
            startinv.setItem(Inventory.checkenemycard(startplayer), card)
            Util.allplaysound(Sound.ITEM_BOOK_PAGE_TURN, startplayer)


            sleep(1000)

            card = Inventory.drawcard(startplayer, false) ?:return
            startinv.setItem(Inventory.checkplayercard(startplayer), card)
            joininv.setItem(Inventory.checkenemycard(joinplayer), card)
            Util.allplaysound(Sound.ITEM_BOOK_PAGE_TURN, startplayer)

            sleep(1000)

            card = Inventory.drawcard(joinplayer, false) ?:return
            joininv.setItem(Inventory.checkplayercard(joinplayer), card)
            startinv.setItem(Inventory.checkenemycard(startplayer), card)
            Util.allplaysound(Sound.ITEM_BOOK_PAGE_TURN, startplayer)

            var first = player
            first = if (loops == 1){
                if (Random.nextInt(1) == 1) player else getdata(player).enemy
            }else{
                if (first == player) getdata(player).enemy else player
            }
            fillaction(getinv(first))


            while (!getdata(startplayer).through || !getdata(joinplayer).through){
                for (i in plugin.config.getInt("clocktime")*20 downTo 0){
                    if (getdata(first).action != ""){
                        when(getdata(first).action){
                            "spuse"->break
                            "through"->{
                                getdata(first).through = true
                                getdata(first).action = ""
                                break
                            }
                            "draw"->{
                                getdata(first).through = false
                                getdata(first).action = ""
                                break
                            }
                        }

                    }
                    sleep(50)
                    if (i == 0){

                    }
                    if (i % 20 == 0){
                        timecount(first,i / 20)
                    }

                }
                if (getdata(first).action == "spuse"){
                    getdata(first).action = ""
                    continue
                }
                if (first == player) getdata(player).enemy else player
                fillaction(getinv(first))

            }




        }
    }
}
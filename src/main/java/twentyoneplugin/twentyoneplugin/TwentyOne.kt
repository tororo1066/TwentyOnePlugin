package twentyoneplugin.twentyoneplugin

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.Sound
import org.bukkit.inventory.ItemStack
import twentyoneplugin.twentyoneplugin.AdvancementUtils.Companion.awardAdvancement
import twentyoneplugin.twentyoneplugin.Inventory.checkplayersp
import twentyoneplugin.twentyoneplugin.Inventory.fillaction
import twentyoneplugin.twentyoneplugin.Inventory.getinv
import twentyoneplugin.twentyoneplugin.Inventory.invsetup
import twentyoneplugin.twentyoneplugin.Inventory.replaceaction
import twentyoneplugin.twentyoneplugin.Inventory.showcardcount
import twentyoneplugin.twentyoneplugin.TOP.Companion.canjoin
import twentyoneplugin.twentyoneplugin.TOP.Companion.datamap
import twentyoneplugin.twentyoneplugin.TOP.Companion.plugin
import twentyoneplugin.twentyoneplugin.TOP.Companion.vault
import twentyoneplugin.twentyoneplugin.Util.allplayersend
import twentyoneplugin.twentyoneplugin.Util.allplaysound
import twentyoneplugin.twentyoneplugin.Util.draw
import twentyoneplugin.twentyoneplugin.Util.endtwoturn
import twentyoneplugin.twentyoneplugin.Util.format
import twentyoneplugin.twentyoneplugin.Util.gamelatersetting
import twentyoneplugin.twentyoneplugin.Util.getdata
import twentyoneplugin.twentyoneplugin.Util.getenemy
import twentyoneplugin.twentyoneplugin.Util.getplayer
import twentyoneplugin.twentyoneplugin.Util.isDone
import twentyoneplugin.twentyoneplugin.Util.runcmd
import twentyoneplugin.twentyoneplugin.Util.timecount
import twentyoneplugin.twentyoneplugin.Util.win
import twentyoneplugin.twentyoneplugin.advancements.*
import java.util.*
import kotlin.random.Random
import kotlin.random.nextInt


class TwentyOne(private val player : UUID) : Thread(){


    override fun run() {
        for (i in 59 downTo 0){
            if (!canjoin.contains(player))break
            if (getplayer(player) == null){
                datamap.remove(player)
                canjoin.remove(player)
                return
            }
            if (i == 0){
                Bukkit.broadcast(Component.text("??l${getplayer(player)?.name}??a?????5BJP??a??????????????????????????????????????????????????????"), Server.BROADCAST_CHANNEL_USERS)
                vault.deposit(player,getdata(player).tip * getdata(player).tipcoin)
                datamap.remove(player)
                canjoin.remove(player)
                return
            }
            if (i % 20 == 0) Bukkit.broadcast(runcmd("??l${getplayer(player)?.name}??a?????5??lBJP??a????????????...??????${i}???\n" +
                    "??f/bjp join ${getplayer(player)?.name} ??e?????????????????? ${format(getdata(player).tip * getdata(player).tipcoin)}\n" +
                    "??b???????????? 1???????????????????????????:${format(getdata(player).tip)}??????Round???:${datamap[player]?.round}?????????????????????:${datamap[player]?.settipcoin}????????????????????????:${datamap[player]?.firstbet}??????1??????????????????:${datamap[player]?.clocktime}???","/bjp join ${getplayer(player)?.name}","??6?????????????????????????????????")
                ,Server.BROADCAST_CHANNEL_USERS)
            sleep(1000)
        }


        val startplayer = player
        val joinplayer = getdata(player).enemy

        var firstturn = player
        for (loops in 1..getdata(player).round){

            val startinv = getinv(startplayer)
            val joininv = getinv(joinplayer)


            if (loops == 1){
                startinv.setItem(checkplayersp(startplayer), Inventory.drawspcard(player))
                joininv.setItem(checkplayersp(joinplayer), Inventory.drawspcard(joinplayer))
            }else{
                Bukkit.getScheduler().runTask(plugin, Runnable {
                    getplayer(joinplayer)?.openInventory(getinv(joinplayer))
                    getplayer(startplayer)?.openInventory(getinv(startplayer))
                    return@Runnable
                })
            }
            startinv.setItem(checkplayersp(startplayer), Inventory.drawspcard(player))
            joininv.setItem(checkplayersp(joinplayer), Inventory.drawspcard(joinplayer))
            allplaysound(Sound.BLOCK_ANVIL_PLACE, startplayer)
            sleep(1000)

            var card = Inventory.drawcard(startplayer, true) ?:return
            startinv.setItem(Inventory.checkplayercard(startplayer), card)
            Inventory.nullcarddis(card)
            joininv.setItem(Inventory.checkenemycard(joinplayer), card)
            allplaysound(Sound.ITEM_BOOK_PAGE_TURN, startplayer)

            sleep(1000)

            card = Inventory.drawcard(joinplayer, true) ?:return
            joininv.setItem(Inventory.checkplayercard(joinplayer), card)
            Inventory.nullcarddis(card)
            startinv.setItem(Inventory.checkenemycard(startplayer), card)
            allplaysound(Sound.ITEM_BOOK_PAGE_TURN, startplayer)


            sleep(1000)

            card = Inventory.drawcard(startplayer, false) ?:return
            startinv.setItem(Inventory.checkplayercard(startplayer), card)
            joininv.setItem(Inventory.checkenemycard(joinplayer), card)
            allplaysound(Sound.ITEM_BOOK_PAGE_TURN, startplayer)

            sleep(1000)

            card = Inventory.drawcard(joinplayer, false) ?:return
            joininv.setItem(Inventory.checkplayercard(joinplayer), card)
            startinv.setItem(Inventory.checkenemycard(startplayer), card)
            allplaysound(Sound.ITEM_BOOK_PAGE_TURN, startplayer)

            showcardcount(player)


            var first: UUID


            firstturn = if (loops == 1){
                if (Random.nextInt(1..2) == 1) player else getdata(player).enemy
            }else{
                if (firstturn == player) getdata(player).enemy else player
            }
            first = firstturn
            fillaction(getinv(firstturn))


            while (!getdata(startplayer).through || !getdata(joinplayer).through){
                for (i in getdata(player).clocktime*20 downTo 0){
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
                        getdata(first).through = true
                        getdata(first).action = ""
                        break
                    }
                    if (i % 20 == 0){
                        timecount(first,i / 20)
                    }

                }
                showcardcount(player)
                if (getdata(first).action == "spuse"){
                    sleep(5000)
                    getdata(first).action = ""
                    fillaction(getinv(first))
                    continue
                }
                replaceaction(getinv(first))
                first = if (first == player) getdata(player).enemy else player

                if (!getdata(startplayer).through || !getdata(joinplayer).through)
                fillaction(getinv(first))

            }

            sleep(500)
            allplaysound(Sound.BLOCK_ANVIL_PLACE,player)
            sleep(500)
            allplaysound(Sound.BLOCK_ANVIL_PLACE,player)
            sleep(500)
            allplaysound(Sound.BLOCK_ANVIL_PLACE,player)
            sleep(1000)
            allplaysound(Sound.ENTITY_GENERIC_EXPLODE,player)


            val gameend = endtwoturn(player)
            if (gameend == null)draw(player) else if (gameend == true) win(player) else win(getenemy(player))

            if (!gamelatersetting(player,gameend)){
                if (getdata(player).customsetting)break

                val tipcoin = getdata(player).tipcoin

                Bukkit.getScheduler().runTask(plugin, Runnable {
                    if (tipcoin == 0){
                        if (getplayer(joinplayer)?.let { Complete21.key.isDone(it) } == true) getplayer(joinplayer)?.awardAdvancement(PerfectGame.key)
                    }else{
                        if (getplayer(player)?.let { Complete21.key.isDone(it) } == true) getplayer(player)?.awardAdvancement(PerfectGame.key)
                    }
                    if (loops == 1){
                        if (tipcoin == 0){
                            if (getplayer(joinplayer)?.let { PerfectGame.key.isDone(it) } == true) getplayer(joinplayer)?.awardAdvancement(UltimateGame.key)
                        }else{
                            if (getplayer(player)?.let { PerfectGame.key.isDone(it) } == true) getplayer(player)?.awardAdvancement(UltimateGame.key)
                        }
                    }
                })
                break
            }

            if (getdata(player).round != loops){
                val spsave = ArrayList<ItemStack>()
                val spsave2 = ArrayList<ItemStack>()
                var wi = 36
                while (getinv(player).getItem(wi) != null){
                    spsave.add(getinv(player).getItem(wi)!!)
                    wi++
                }
                wi = 36
                while (getinv(getenemy(player)).getItem(wi) != null){
                    spsave2.add(getinv(getenemy(player)).getItem(wi)!!)
                    wi++
                }
                getdata(player).inv = invsetup(player, getdata(player).enemy)
                getdata(getdata(player).enemy).inv = invsetup(getdata(player).enemy, player)
                for (item in spsave){
                    getinv(player).setItem(checkplayersp(player),item)
                }

                for (item in spsave2){
                    getinv(getenemy(player)).setItem(checkplayersp(getenemy(player)),item)
                }

            }



        }



        val tipcoin = getdata(player).tipcoin
        val enemytipcoin = getdata(getenemy(player)).tipcoin


        allplayersend(player,"??5===============??????===============")
        allplayersend(player,"??e${getdata(player).name}???${getdata(player).tipcoin}/${getdata(player).settipcoin}???")
        allplayersend(player,"??e${getdata(getenemy(player)).name}???${getdata(getdata(player).enemy).tipcoin}/${getdata(
            getenemy(player)).settipcoin}???")
        allplayersend(player,"??5===============??????===============")

        Bukkit.getScheduler().runTask(plugin, Runnable {
            if (tipcoin != enemytipcoin){
                if (tipcoin > enemytipcoin){
                    if (getplayer(player)?.let { UseSp.key.isDone(it) } == true) getplayer(player)?.awardAdvancement(WinGame.key)
                }else{
                    if (getplayer(joinplayer)?.let { UseSp.key.isDone(it) } == true) getplayer(joinplayer)?.awardAdvancement(WinGame.key)
                }
            }
        })

        if (getplayer(player) != null) vault.deposit(player, getdata(player).tipcoin * getdata(player).tip)
        if (getplayer(getenemy(player)) != null) vault.deposit(getdata(player).enemy, getdata(getdata(player).enemy).tipcoin * getdata(getdata(player).enemy).tip)

        val mysql = MySQLManager(plugin,"save21log")

        val rs = mysql.query("SELECT * FROM bjp_battle_log WHERE uuid = '${player}';")
        if (rs == null || !rs.next()){
            mysql.execute("INSERT INTO bjp_battle_log VALUES ('${player}', '${getdata(player).name}', ${if (tipcoin > enemytipcoin) 1 else 0}, ${if (tipcoin == enemytipcoin) 1 else 0}, ${if (tipcoin < enemytipcoin) 1 else 0});")
        }else{
            mysql.execute("UPDATE bjp_battle_log SET ${if (tipcoin > enemytipcoin) "win" else if (tipcoin == enemytipcoin) "draw" else "lose"} = ${if (tipcoin > enemytipcoin) "win" else if (tipcoin == enemytipcoin) "draw" else "lose"} + 1 WHERE uuid = '${player}';")
        }

        val rs2 = mysql.query("SELECT * FROM bjp_battle_log WHERE uuid = '${getenemy(player)}';")
        if (rs2 == null || !rs2.next()){
            mysql.execute("INSERT INTO bjp_battle_log VALUES ('${getenemy(player)}', '${getdata(getenemy(player)).name}', ${if (tipcoin < enemytipcoin) 1 else 0}, ${if (tipcoin == enemytipcoin) 1 else 0}, ${if (tipcoin > enemytipcoin) 1 else 0});")
        }else{
            mysql.execute("UPDATE bjp_battle_log SET ${if (tipcoin < enemytipcoin) "win" else if (tipcoin == enemytipcoin) "draw" else "lose"} = ${if (tipcoin < enemytipcoin) "win" else if (tipcoin == enemytipcoin) "draw" else "lose"} + 1 WHERE uuid = '${getenemy(player)}';")
        }

        mysql.execute("INSERT INTO bjp_log VALUES " + "('${getdata(player).name}', '${getdata(getenemy(player)).name}', ${getdata(player).tip}, ${getdata(player).settipcoin}, ${getdata(player).tipcoin}, ${getdata(getdata(player).enemy).tipcoin});")
        rs?.close()
        rs2?.close()
        mysql.close()

        datamap.remove(getenemy(player))
        datamap.remove(player)

        return
    }
}
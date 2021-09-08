package twentyoneplugin.twentyoneplugin.advancements

import advancement.Advancement
import advancement.display.BackgroundType
import advancement.display.Display
import advancement.trigger.TriggerType
import org.bukkit.NamespacedKey
import twentyoneplugin.twentyoneplugin.TOP.Companion.cardmaterial
import twentyoneplugin.twentyoneplugin.TOP.Companion.invisiblecardcsm
import twentyoneplugin.twentyoneplugin.TOP.Companion.plugin
import java.util.function.Consumer

object LoginServer : Advancement(plugin,"login_server") {

    init {
        addCriteria("login",TriggerType.IMPOSSIBLE){}
        setDisplay {
            it.setTitle("BJP")
            it.setDescription("命を賭けるカードゲーム")
            it.setAnnounce(false)
            it.setIcon(cardmaterial,"{CustomModelData:$invisiblecardcsm}")
            it.setBackground(BackgroundType.BEDROCK)
        }
    }
}
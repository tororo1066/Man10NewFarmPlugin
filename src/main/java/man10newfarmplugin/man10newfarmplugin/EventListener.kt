package man10newfarmplugin.man10newfarmplugin

import man10newfarmplugin.man10newfarmplugin.MNF.Companion.da
import man10newfarmplugin.man10newfarmplugin.MNF.Companion.plugin
import man10newfarmplugin.man10newfarmplugin.Util.plantcrops
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Skull
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockGrowEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.EquipmentSlot
import java.util.*
import kotlin.random.Random
import kotlin.random.nextInt


object EventListener : Listener {

    @EventHandler
    fun click(e: PlayerInteractEvent) {
        if (e.hand == EquipmentSlot.OFF_HAND) return
        if (e.clickedBlock == null || e.action != Action.RIGHT_CLICK_BLOCK) return
        e.player.sendMessage(e.action.toString())
        plantcrops(e.clickedBlock!!, e.player.inventory.itemInMainHand, e.player)
    }

    @EventHandler
    fun growth(e: BlockGrowEvent) {

        val age = e.newState.blockData as Ageable


        for (i in da) {

            var c = 1
            while (plugin.config.isSet("farm.No$c")) {
                if (i.key.x.toInt() == e.block.location.blockX && i.key.y.toInt() == e.block.location.blockY && i.key.z.toInt() == e.block.location.blockZ && i.key.world.uid == e.block.world.uid){
                    Bukkit.broadcastMessage(c.toString())

                    if (plugin.config.getInt("farm.No$c.ll") > e.block.lightLevel){
                        Bukkit.broadcastMessage("jaodf")
                        e.isCancelled = true
                        return
                    }
                    if (age.age == age.maximumAge) {
                        Bukkit.broadcastMessage(c.toString())
                        if (plugin.config.getString("farm.No$c.cropblock") == "PLAYER_HEAD"){
                            val m = Material.PLAYER_HEAD
                            val data = m.data as Skull
                            Bukkit.getPlayer(UUID.fromString(plugin.config.getString("farm.No$c.crophead")))?.let { data.setOwningPlayer(it) }
                            e.block.type = m
                        }else{
                            Bukkit.broadcastMessage(c.toString())
                            e.block.type = plugin.config.getString("farm.No$c.cropblock")?.let { Material.valueOf(it) }!!
                            return
                        }
                    } else {
                        Bukkit.broadcastMessage("^^^^")
                        if (Random.nextInt(0..plugin.config.getInt("farm.No$c.growc")) != 1)e.isCancelled = true
                    }
                }else{
                    Bukkit.broadcastMessage("${i.key.x} ${i.key.y} ${i.key.z} ${i.key.world.uid} ${e.block.location.x} ${e.block.location.y} ${e.block.location.z} ${e.block.location.world.uid}")
                }

                c++
            }
        }


    }

    @EventHandler
    fun breakblock(e : BlockBreakEvent) {

            if (e.block.type == Material.POTATOES || e.block.type == Material.CARROTS || e.block.type == Material.SWEET_BERRY_BUSH || e.block.type == Material.WHEAT || e.block.type == Material.PUMPKIN_STEM || e.block.type == Material.MELON_STEM || e.block.type == Material.BEETROOTS){




            }else{

            }


    }

    @EventHandler
    fun move(e : PlayerMoveEvent){

    }

}

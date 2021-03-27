package man10newfarmplugin.man10newfarmplugin

import man10newfarmplugin.man10newfarmplugin.MNF.Companion
import man10newfarmplugin.man10newfarmplugin.Util.gotcrop
import man10newfarmplugin.man10newfarmplugin.Util.parm
import man10newfarmplugin.man10newfarmplugin.Util.per
import man10newfarmplugin.man10newfarmplugin.Util.plantcrops
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Skull
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.event.block.BlockGrowEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.random.Random
import kotlin.random.nextInt


object EventListener : Listener {
    private const val prefix = Companion.prefix
    private val plugin = Companion.plugin
    private val configdata = Companion.configdata
    private val farmdata = Companion.farmdata

    @EventHandler
    fun click(e: PlayerInteractEvent) {
        if (e.hand == EquipmentSlot.OFF_HAND) return
        if (e.clickedBlock == null || e.action != Action.RIGHT_CLICK_BLOCK) return
        if (e.clickedBlock?.type == Material.FARMLAND && e.player.inventory.itemInMainHand.type == Material.WHEAT_SEEDS)e.isCancelled = true
        plantcrops(e.clickedBlock!!, e.player.inventory.itemInMainHand, e.player)
    }

    @EventHandler
    fun growth(e: BlockGrowEvent) {

        if (e.block.type != Material.WHEAT)return
        val age = e.newState.blockData as Ageable
        plugin.server.logger.info(farmdata.keys.toString() + " " + e.block.location.toString())
        val i = configdata.crop.indexOf(farmdata[e.block.location])
        plugin.server.logger.info("${configdata.growc[i]}")
        if (age.age == age.maximumAge){
            if (configdata.cropblock[i] is UUID){
                Bukkit.getScheduler().runTask(plugin, Runnable {
                    e.block.type = Material.PLAYER_HEAD
                    val meta = e.block.state as Skull
                    meta.setOwningPlayer(Bukkit.getOfflinePlayer(configdata.cropblock[i] as UUID))
                    meta.update()
                    return@Runnable
                })
                return
            }else{
                Bukkit.getScheduler().runTask(plugin, Runnable {
                    e.block.type = configdata.cropblock[i] as Material
                    return@Runnable
                })
            }


        }else if (!per(configdata.growc[i].toDouble()))e.isCancelled = true
    }

    @EventHandler
    fun breakblock(e : BlockBreakEvent) {
        if (farmdata.containsKey(e.block.location.add(0.0,1.0,0.0)))e.isCancelled = true
        if (!farmdata.containsKey(e.block.location))return
        if (e.block.type == Material.WHEAT){
            val age = e.block.blockData as Ageable
            if (age.age != age.maximumAge){
                gotcrop(e.block.location)
                return
            }
        }

        e.isCancelled = true
        val i = configdata.crop.indexOf(farmdata[e.block.location])
        if (e.player.inventory.itemInMainHand == configdata.cropbreakitem[i] && !parm(e.player)){
            e.player.sendMessage(prefix + "この作物は壊すのに別のアイテムが必要です！")
            return
        }
        e.block.world.dropItemNaturally(e.block.location,configdata.crop[i])
        if (!configdata.othercrops[i].contains(ItemStack(Material.AIR))) {
            for (int in 0 until configdata.othercrops[i].size) {
                if (!per(configdata.othercropsdropchance[i][int].toDouble())) continue
                val item = configdata.othercrops[i][int]
                item.amount = Random.nextInt(configdata.othercropschance[i][int])
                e.block.world.dropItemNaturally(e.block.location,item)
            }
        }
        e.block.type = Material.AIR
        e.block.location.subtract(0.0,1.0,0.0).block.type = configdata.canb[i]
        gotcrop(e.block.location)
    }


    @EventHandler
    fun breakblockbyfiuld(e : BlockFromToEvent){
        if (e.toBlock.type == Material.WHEAT){
            e.isCancelled = true
        }
        if (farmdata.containsKey(e.toBlock.location)){
            e.isCancelled = true
        }
    }
}

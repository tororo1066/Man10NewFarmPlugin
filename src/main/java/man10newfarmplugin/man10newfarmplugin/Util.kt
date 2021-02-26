package man10newfarmplugin.man10newfarmplugin

import man10newfarmplugin.man10newfarmplugin.MNF.Companion.da
import man10newfarmplugin.man10newfarmplugin.MNF.Companion.plugin
import man10newfarmplugin.man10newfarmplugin.MNF.Companion.prefix
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.Skull
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


object Util {

    var wait = hashMapOf<Player,Boolean>()

    fun changeint(s: String): Int? {
        try {
            return s.toInt()
        } catch (e: NumberFormatException) {
            plugin.server.logger.warning(prefix + "文字列の数字化に失敗しました")
        }
        return null
    }

    fun givecrops(i: Int, p: Player) {
        var count = 1
        while (plugin.config.isSet("farm.No$count")){
            if (i == count){
                p.inventory.setItemInMainHand(plugin.config.getString("farm.No$count.seed")?.let { itemFromBase64(it) })
                return
            }
            count++
        }
    }

    fun plantcrops(clicked: Block, item: ItemStack, p: Player) {


        var c = 1
        while (plugin.config.isSet("farm.No$c")) {
            if (plugin.config.getString("farm.No$c.seed") == itemToBase64(item)) {
                if (plugin.config.getString("farm.No$c.canb") == clicked.type.toString()) {
                    if (clicked.location.add(0.0,1.0,0.0).block.lightLevel.toInt() >= plugin.config.getInt("farm.No$c.ll")){
                        wait[p] = true
                        clicked.type = Material.FARMLAND
                        clicked.location.add(0.0,1.0,0.0).block.type = Material.valueOf(plugin.config.getString("farm.No$c.seedm")!!)
                        p.sendMessage(prefix + "${item.itemMeta.displayName}を植えました")
                        da[clicked.location.add(0.0,1.0,0.0)] = plugin.config.getString("farm.No$c.crop")!!


                        wait[p] = false
                        return
                    }else{
                        if (wait[p] != true) {
                            p.sendMessage(prefix + "光の強さが足りません！")
                            return
                        }
                    }
                } else {
                    if (wait[p] != true) {
                        p.sendMessage(prefix + "適切な場所に植えてください！")
                        return
                    }


                }
            }
            c++
        }

        return
    }



    fun addcrops(i: Int, seed : ItemStack, seedm : Material, crop : ItemStack, cropblock : Material, canb : Material, growc : Int, ll : Int, p: Player) {

        plugin.config.set("farm.No$i.seed", itemToBase64(seed))
        when(seedm){
            Material.POTATO-> plugin.config.set("farm.No$i.seedm", "POTATOES")
            Material.CARROT-> plugin.config.set("farm.No$i.seedm", "CARROTS")
            Material.SWEET_BERRIES-> plugin.config.set("farm.No$i.seedm", "SWEET_BERRY_BUSH")
            Material.BEETROOT-> plugin.config.set("farm.No$i.seedm", "BEETROOTS")
            Material.MELON-> plugin.config.set("farm.No$i.seedm", "MELON_STEM")
            Material.PUMPKIN-> plugin.config.set("farm.No$i.seedm", "PUMPKIN_STEM")

            else->plugin.config.set("farm.No$i.seedm", seedm.name)
        }


        plugin.config.set("farm.No$i.crop", itemToBase64(crop))
        if (cropblock == Material.PLAYER_HEAD){
            val head = cropblock as Skull
            plugin.config.set("farm.No$i.cropblock", cropblock.name)
            plugin.config.set("farm.No$i.crophead",head.owningPlayer?.uniqueId)
        }else{
            plugin.config.set("farm.No$i.cropblock", cropblock.name)
        }
        plugin.config.set("farm.No$i.canb",canb.name)
        plugin.config.set("farm.No$i.growc",growc)
        plugin.config.set("farm.No$i.ll",ll)
        plugin.saveConfig()
        p.sendMessage(prefix + "コンフィグへの書き込みが完了しました")
    }


    ///////////////////////////////
    //base 64
    //////////////////////////////
    fun itemFromBase64(data: String): ItemStack? = try {
        val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
        val dataInput = BukkitObjectInputStream(inputStream)
        val items = arrayOfNulls<ItemStack>(dataInput.readInt())

        // Read the serialized inventory
        for (i in items.indices) {
            items[i] = dataInput.readObject() as ItemStack
        }

        dataInput.close()
        items[0]
    } catch (e: Exception) {
        null
    }

    @Throws(IllegalStateException::class)
    fun itemToBase64(item: ItemStack): String {
        try {
            val outputStream = ByteArrayOutputStream()
            val dataOutput = BukkitObjectOutputStream(outputStream)
            val items = arrayOfNulls<ItemStack>(1)
            items[0] = item
            dataOutput.writeInt(items.size)

            for (i in items.indices) {
                dataOutput.writeObject(items[i])
            }

            dataOutput.close()

            return Base64Coder.encodeLines(outputStream.toByteArray())

        } catch (e: Exception) {
            throw IllegalStateException("Unable to save item stacks.", e)
        }
    }




}

package ru.tesmio.perimeter.blocks.devices.redstonefurnace;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import ru.tesmio.perimeter.blocks.devices.redstonefurnace.recipe.RedstoneFurnaceRecipe;
import ru.tesmio.perimeter.core.registration.RegBlockEntitys;

import java.util.Optional;

public class RedstoneFurnaceEntity extends BlockEntity implements MenuProvider {
    private final Container inventory = new SimpleContainer(6); // 0,1 - основной input/output, 2 - upgrade, 3,4 - расширение input/output, 5 - upgrade расширения
    private boolean isPowered = false;
    private int cookTime = 0;
    private int cookTimeTotal = 200;


    public RedstoneFurnaceEntity(BlockPos pos, BlockState state) {
        super(RegBlockEntitys.REDSTONE_FURNACE_ENTITY.get(), pos, state);
    }

    public void tick() {
        if (!isPowered) return;
        processSlot(0, 1); // основной input/output

        if (hasExpansionUpgrade()) {
            processSlot(3, 4); // расширенный input/output
        }
    }

    private void processSlot(int inputSlot, int outputSlot) {
        ItemStack input = inventory.getItem(inputSlot);
        ItemStack output = inventory.getItem(outputSlot);

        Optional<RedstoneFurnaceRecipe> recipeOpt = level.getRecipeManager().getRecipeFor(RedstoneFurnaceRecipe.Type.INSTANCE, new SimpleContainer(input), level);

        if (recipeOpt.isPresent()) {
            RedstoneFurnaceRecipe recipe = recipeOpt.get();
            ItemStack result = recipe.getResultItem(level.registryAccess());

            if (output.isEmpty() || (ItemStack.isSameItemSameTags(output, result) && output.getCount() + result.getCount() <= output.getMaxStackSize())) {
                cookTime += hasSpeedUpgrade() ? 5 : 1;
                // System.out.println("tick" + cookTime % 40);
                if (cookTime >= 200) {
                    input.shrink(1);
                    if (output.isEmpty()) {

                        inventory.setItem(outputSlot, result.copy());
                    } else {
                        output.grow(result.getCount());
                    }
                    cookTime = 0;
                }
            } else {
                cookTime = 0;
            }
        } else {
            cookTime = 0;
        }
    }

    public void setPowered(boolean powered) {
        this.isPowered = powered;
    }

    private boolean hasSpeedUpgrade() {
        return inventory.getItem(2).is(Items.STICK);
    }

    private boolean hasExpansionUpgrade() {
        return inventory.getItem(5).is(Items.APPLE);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Redstone Furnace");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new RedstoneFurnaceMenu(id, playerInventory, this);
    }

    public Container getInventory() {
        return inventory;
    }
}

package ru.tesmio.perimeter.blocks.devices.redstonefurnace;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
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
import ru.tesmio.perimeter.core.PerimeterItems;
import ru.tesmio.perimeter.core.registration.RegBlockEntitys;
import ru.tesmio.perimeter.items.UpgradeSpeed;

import java.util.Optional;

public class RedstoneFurnaceEntity extends BlockEntity implements MenuProvider {
    private final Container inventory = new FilteredContainer(7); // 0,1 - основной input/output, 2 - upgrade, 3,4 - расширение input/output, 5 - upgrade расширения
    private boolean isPowered = false;
    private int[] cookTimes = new int[2]; // 0 - основной, 1 - расширение
    private final int cookTimeTotal = 200;


    public RedstoneFurnaceEntity(BlockPos pos, BlockState state) {
        super(RegBlockEntitys.REDSTONE_FURNACE_ENTITY.get(), pos, state);
    }

    public void tick() {
        if (!isPowered) return;
        processSlot(0, 1, 0); // основной input/output

        if (hasExpansionUpgrade()) {
            processSlot(3, 4, 1); // расширенный input/output
        }
    }

    public NonNullList<ItemStack> drops() {
        NonNullList<ItemStack> list = NonNullList.withSize(inventory.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            list.set(i, inventory.getItem(i));
        }
        return list;
    }

    private void processSlot(int inputSlot, int outputSlot, int cookIndex) {
        ItemStack input = inventory.getItem(inputSlot);
        ItemStack output = inventory.getItem(outputSlot);

        Optional<RedstoneFurnaceRecipe> recipeOpt = level.getRecipeManager().getRecipeFor(
                RedstoneFurnaceRecipe.Type.INSTANCE, new SimpleContainer(input), level
        );

        if (recipeOpt.isPresent()) {

            RedstoneFurnaceRecipe recipe = recipeOpt.get();
            ItemStack result = recipe.getResultItem(level.registryAccess());
            boolean hasRedstoneBoost = hasRedstoneBoost();
            int multiplier = hasRedstoneBoost ? 2 : 1;
            ItemStack boostedResult = result.copy();
            boostedResult.setCount(result.getCount() * multiplier);

            if (output.isEmpty() || (ItemStack.isSameItemSameTags(output, boostedResult) &&
                    output.getCount() + boostedResult.getCount() <= output.getMaxStackSize())) {

                cookTimes[cookIndex] += hasSpeedUpgrade() ? 5 : 1;

                if (cookTimes[cookIndex] >= cookTimeTotal) {
                    input.shrink(1);

                    if (hasRedstoneBoost) {
                        ItemStack redstone = inventory.getItem(6);
                        redstone.shrink(1); // потратить 1 редстоун
                    }

                    if (output.isEmpty()) {
                        inventory.setItem(outputSlot, boostedResult.copy());
                    } else {
                        output.grow(boostedResult.getCount());
                    }

                    cookTimes[cookIndex] = 0;
                    if (hasSpeedUpgrade()) {
                        ItemStack speedUpgrade = inventory.getItem(2);
                        speedUpgrade.hurt(1, level.getRandom(), null);
                        if (speedUpgrade.getDamageValue() >= speedUpgrade.getMaxDamage()) {
                            inventory.setItem(2, ItemStack.EMPTY);
                        }
                    }
                }
            } else {
                cookTimes[cookIndex] = 0;
            }
        } else {
            cookTimes[cookIndex] = 0;
        }
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public int getCookProgressScaled(int index, int scale) {
        if (cookTimes[index] > 0) {
            return cookTimes[index] * scale / cookTimeTotal;
        } else {
            return 0;
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata(); // или saveWithFullMetadata()
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.load(tag);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
    }

    private boolean hasRedstoneBoost() {
        return hasExpansionUpgrade() && inventory.getItem(6).is(Items.REDSTONE);
    }

    public void setPowered(boolean powered) {
        this.isPowered = powered;
    }

    private boolean hasSpeedUpgrade() {
        return inventory.getItem(2).getItem() instanceof UpgradeSpeed;
    }

    boolean hasExpansionUpgrade() {
        return inventory.getItem(5).is(PerimeterItems.UPGRADE_SPACE.get());
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

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        // сохраняем инвентарь
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            CompoundTag itemTag = new CompoundTag();
            inventory.getItem(i).save(itemTag);
            tag.put("Item_" + i, itemTag);
        }
        // cookTimes
        tag.putIntArray("CookTimes", cookTimes);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        // загружаем инвентарь
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (tag.contains("Item_" + i)) {
                ItemStack stack = ItemStack.of(tag.getCompound("Item_" + i));
                inventory.setItem(i, stack);
            }
        }
        // cookTimes
        if (tag.contains("CookTimes")) {
            int[] savedCookTimes = tag.getIntArray("CookTimes");
            for (int i = 0; i < Math.min(cookTimes.length, savedCookTimes.length); i++) {
                cookTimes[i] = savedCookTimes[i];
            }
        }
    }

    private class FilteredContainer extends SimpleContainer {

        public FilteredContainer(int size) {
            super(size);
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack stack) {
            // Запретить вставку в выходные слоты
            if (slot == 1 || slot == 4) {
                return false;
            }

            // Слот ускорения принимает только палку
            if (slot == 2) {
                return stack.is(PerimeterItems.UPGRADE_SPEED.get());
            }

            // Слот расширения принимает только яблоко
            if (slot == 5) {
                return stack.is(PerimeterItems.UPGRADE_SPACE.get());
            }

            // Слот редстоуна принимает только редстоун
            if (slot == 6) {
                return stack.is(Items.REDSTONE);
            }

            // Остальные слоты — без ограничений
            return true;
        }
    }
}

package ru.tesmio.perimeter.blocks.devices.redstoneworkbench;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import ru.tesmio.perimeter.core.PerimeterItems;
import ru.tesmio.perimeter.core.registration.RegBlockEntitys;
import ru.tesmio.perimeter.items.CraftingTemplateItem;

import java.util.Optional;

public class RedstoneWorkbenchEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(12) { // 9 крафт + 1 выход + 1 шаблон + 1 входной шаблон
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide) {
                if (slot < 9) { // изменение сетки крафта
                    tryCraft();
                } else if (slot == 10) { // изменение входного шаблона
                    loadTemplate();
                }
            }
        }
    };

    public RedstoneWorkbenchEntity(BlockPos pos, BlockState state) {
        super(RegBlockEntitys.WORKBENCH_ENTITY.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Custom Workbench");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new RedstoneWorkbenchMenu(containerId, playerInventory, this);
    }

    // Для доступа к инвентарю из меню
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    // Сохранение данных
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", itemHandler.serializeNBT());
    }

    // Загрузка данных
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("inventory")) {
            itemHandler.deserializeNBT(tag.getCompound("inventory"));
        }
    }

    public void onTakeResult(Player player) {
        // Сохраняем рецепт в шаблон, если он есть в слоте сохранения
        saveTemplate();

        // Стандартная логика расходования ингредиентов
        consumeIngredients();
    }

    private void checkAndLoadTemplate() {
        ItemStack template = itemHandler.getStackInSlot(10); // Слот загрузки (x7 y16)

        if (template.is(PerimeterItems.CRAFTING_TEMPLATE.get()) &&
                CraftingTemplateItem.isFilled(template)) {

            NonNullList<ItemStack> recipe = CraftingTemplateItem.getRecipeFromTemplate(template);

            // Загружаем рецепт в сетку крафта
            for (int i = 0; i < 9; i++) {
                itemHandler.setStackInSlot(i, recipe.get(i).copy());
            }
        }
    }

    public void tryCraft() {
        // Проверяем, есть ли шаблон в слоте сохранения (11)
        ItemStack template = itemHandler.getStackInSlot(11);
        if (!template.isEmpty() && template.is(PerimeterItems.CRAFTING_TEMPLATE.get())) {
            saveTemplate();
        }

        // Обычная логика крафта
        NonNullList<ItemStack> craftingGrid = NonNullList.withSize(9, ItemStack.EMPTY);
        for (int i = 0; i < 9; i++) {
            craftingGrid.set(i, itemHandler.getStackInSlot(i));
        }

        Optional<CraftingRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(RecipeType.CRAFTING, new RedstoneWorkbenchContainer(craftingGrid), level);

        itemHandler.setStackInSlot(9,
                recipe.map(r -> r.assemble(new RedstoneWorkbenchContainer(craftingGrid), level.registryAccess()))
                        .orElse(ItemStack.EMPTY));
    }

//    public void saveTemplate() {
//        ItemStack template = itemHandler.getStackInSlot(11);
//        if (!template.is(PerimeterItems.CRAFTING_TEMPLATE.get())) return;
//
//        CompoundTag tag = new CompoundTag();
//        for (int i = 0; i < 9; i++) {
//            ItemStack stack = itemHandler.getStackInSlot(i);
//            if (!stack.isEmpty()) {
//                tag.put("slot_" + i, stack.save(new CompoundTag()));
//            }
//        }
//        template.setTag(tag);
//    }

    public void loadTemplate() {
        ItemStack template = itemHandler.getStackInSlot(10);
        if (!template.is(PerimeterItems.CRAFTING_TEMPLATE.get()) || !template.hasTag()) return;

        CompoundTag tag = template.getTag();
        for (int i = 0; i < 9; i++) {
            String key = "slot_" + i;
            itemHandler.setStackInSlot(i,
                    tag.contains(key) ? ItemStack.of(tag.getCompound(key)) : ItemStack.EMPTY);
        }
    }

    public void autoCraft() {
        if (level == null || level.isClientSide) return;

        // 1. Проверяем входной шаблон
        ItemStack inputTemplate = itemHandler.getStackInSlot(10);
        if (!inputTemplate.is(PerimeterItems.CRAFTING_TEMPLATE.get()) || !inputTemplate.hasTag()) return;

        // 2. Загружаем рецепт из шаблона
        loadTemplate();

        // 3. Собираем ингредиенты из соседних контейнеров
        gatherIngredients();

        // 4. Пытаемся скрафтить
        tryCraft();

        // 5. Выдаем результат
        ItemStack result = itemHandler.getStackInSlot(9);
        if (!result.isEmpty()) {
            pushItemToNearbyInventories(result);
            itemHandler.setStackInSlot(9, ItemStack.EMPTY);
        }
    }

    private void saveTemplate() {
        ItemStack templateStack = itemHandler.getStackInSlot(11); // Слот сохранения (x92 y60)

        // Проверяем что это наш шаблон и слот не пустой
        if (templateStack.is(PerimeterItems.CRAFTING_TEMPLATE.get())) {
            CompoundTag recipeTag = new CompoundTag();

            // Сохраняем все непустые слоты крафта
            for (int i = 0; i < 9; i++) {
                ItemStack slotStack = itemHandler.getStackInSlot(i);
                if (!slotStack.isEmpty()) {
                    recipeTag.put("slot_" + i, slotStack.save(new CompoundTag()));
                }
            }

            // Записываем NBT в шаблон
            templateStack.getOrCreateTag().put("Recipe", recipeTag);

            // Помечаем что шаблон заполнен (для визуального отличия)
            templateStack.getTag().putBoolean("Filled", true);
        }
    }

    private void gatherIngredients() {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(direction);
            BlockEntity be = level.getBlockEntity(neighborPos);

            if (be instanceof Container container) {
                // Логика поиска нужных предметов...
            }
        }
    }

    private boolean pushItemToNearbyInventories(ItemStack stack) {
        // Логика выдачи предмета в соседние контейнеры...
        return true;
    }

    public void consumeIngredients() {
        if (level == null || level.isClientSide) return;

        NonNullList<ItemStack> craftingGrid = NonNullList.withSize(9, ItemStack.EMPTY);
        for (int i = 0; i < 9; i++) {
            craftingGrid.set(i, itemHandler.getStackInSlot(i));
        }

        RecipeManager recipeManager = level.getRecipeManager();
        Optional<CraftingRecipe> recipeOptional = recipeManager.getRecipeFor(
                RecipeType.CRAFTING,
                new RedstoneWorkbenchContainer(craftingGrid),
                level
        );

        if (recipeOptional.isPresent()) {
            CraftingRecipe recipe = recipeOptional.get();
            NonNullList<Ingredient> ingredients = recipe.getIngredients();

            // Обработка каждого слота
            for (int i = 0; i < 9; i++) {
                ItemStack stack = itemHandler.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    // Проверяем, является ли предмет контейнером (ведро и т.д.)
                    if (stack.getItem().hasCraftingRemainingItem()) {
                        ItemStack containerItem = stack.getItem().getCraftingRemainingItem(stack);
                        itemHandler.setStackInSlot(i, containerItem);
                    } else {
                        stack.shrink(1);
                        if (stack.isEmpty()) {
                            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
                        }
                    }
                }
            }

            tryCraft();
        }
    }
}

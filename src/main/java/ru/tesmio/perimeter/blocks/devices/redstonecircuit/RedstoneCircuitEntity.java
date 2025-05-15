package ru.tesmio.perimeter.blocks.devices.redstonecircuit;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import ru.tesmio.perimeter.core.PerimeterItems;
import ru.tesmio.perimeter.core.registration.RegBlockEntitys;
import ru.tesmio.perimeter.items.CircuitComponent;

import java.util.ArrayList;
import java.util.List;

import static ru.tesmio.perimeter.blocks.devices.redstonecircuit.CircuitComponents.*;

public class RedstoneCircuitEntity extends BlockEntity {
    private final List<CircuitComponents> components = new ArrayList<>();

    public RedstoneCircuitEntity(BlockPos pos, BlockState state) {
        super(RegBlockEntitys.CIRCUIT_BOARD_ENTITY.get(), pos, state);
    }

    public void addComponent(Item item) {
        if (item instanceof CircuitComponent comp && components.size() < 4) {
            components.add(comp.getType());
            setChanged();
        }
    }

    public ItemStack getResult() {
        // Простая логика определения результата
        if (components.size() == 4) {
            if (components.equals(List.of(RESISTOR, LAMP, CONDENSER, TRANSISTOR))) {
                return new ItemStack(PerimeterItems.LIGHT_CIRCUIT.get(), 8);
            }
            if (components.equals(List.of(LAMP, RESISTOR, CONDENSER, TRANSISTOR))) {
                return new ItemStack(PerimeterItems.PROCESSING_CIRCUIT.get(), 8);
            }
            if (components.equals(List.of(LAMP, CONDENSER, RESISTOR, TRANSISTOR))) {
                return new ItemStack(PerimeterItems.SIGNAL_CIRCUIT.get(), 8);
            }

        }
        return ItemStack.EMPTY;
    }

    public List<CircuitComponents> getComponents() {
        return components;
    }
}


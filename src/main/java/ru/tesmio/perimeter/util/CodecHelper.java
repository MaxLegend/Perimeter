package ru.tesmio.perimeter.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;

public class CodecHelper {

    public static <T> CompoundTag encode(Codec<T> codec, T value) {
        DataResult<CompoundTag> result = codec.encodeStart(NbtOps.INSTANCE, value)
                .mapError(error -> "Failed to encode: " + error)
                .flatMap(tag -> {
                    if (tag instanceof CompoundTag ctag) return DataResult.success(ctag);
                    else return DataResult.error(() -> "Expected CompoundTag but got: " + tag);
                });

        return result.result().orElseGet(() -> {
            System.err.println("Encoding failed for value: " + value);
            return new CompoundTag();
        });
    }

    public static <T> T decode(Codec<T> codec, CompoundTag tag) {
        return codec.parse(NbtOps.INSTANCE, tag)
                .resultOrPartial(System.err::println)
                .orElseThrow(() -> new IllegalStateException("Failed to decode capability from NBT"));
    }
}

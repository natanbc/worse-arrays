package com.github.natanbc.worsearrays;

public interface Array<T> {
    int size();
    T get(int index);
    void set(int index, T element);

    static <T> Array<T> allocate(int size) {
        return ArrayConstructor.create(size, Object.class, Array.class);
    }

    static BooleanArray allocateBooleans(int size) {
        return ArrayConstructor.create(size, boolean.class, BooleanArray.class);
    }

    static ByteArray allocateBytes(int size) {
        return ArrayConstructor.create(size, byte.class, ByteArray.class);
    }

    static CharArray allocateChars(int size) {
        return ArrayConstructor.create(size, char.class, CharArray.class);
    }

    static DoubleArray allocateDoubles(int size) {
        return ArrayConstructor.create(size, double.class, DoubleArray.class);
    }

    static FloatArray allocateFloats(int size) {
        return ArrayConstructor.create(size, float.class, FloatArray.class);
    }

    static IntArray allocateInts(int size) {
        return ArrayConstructor.create(size, int.class, IntArray.class);
    }

    static LongArray allocateLongs(int size) {
        return ArrayConstructor.create(size, long.class, LongArray.class);
    }

    static ShortArray allocateShorts(int size) {
        return ArrayConstructor.create(size, short.class, ShortArray.class);
    }
}
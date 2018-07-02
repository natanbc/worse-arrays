package com.github.natanbc.worsearrays;

public interface Array<T> {
    int size();
    T get(int index);
    void set(int index, T element);

    static <T> Array<T> allocate(int size) {
        if(size < 0) {
            throw new NegativeArraySizeException("size < 0");
        }
        return ArrayConstructor.create(size);
    }
}
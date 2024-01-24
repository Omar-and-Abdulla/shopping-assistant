package com.example.shoppingassistant.ui.home.util;

public class DoubleType<K, V> {
    private K k;
    private V v;

    public DoubleType(K k, V v) {
        this.k = k;
        this.v = v;
    }

    public K getK() {
        return k;
    }

    public void setK(K k) {
        this.k = k;
    }

    public V getV() {
        return v;
    }

    public void setV(V v) {
        this.v = v;
    }
}

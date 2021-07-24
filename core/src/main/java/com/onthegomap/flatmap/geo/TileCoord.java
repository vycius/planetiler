package com.onthegomap.flatmap.geo;

import org.jetbrains.annotations.NotNull;

public record TileCoord(int encoded, int x, int y, int z) implements Comparable<TileCoord> {

  public TileCoord {
    assert z <= 14;
  }

  public static TileCoord ofXYZ(int x, int y, int z) {
    return new TileCoord(encode(x, y, z), x, y, z);
  }

  public static TileCoord decode(int encoded) {
    return new TileCoord(encoded, decodeX(encoded), decodeY(encoded), decodeZ(encoded));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    TileCoord tileCoord = (TileCoord) o;

    return encoded == tileCoord.encoded;
  }

  @Override
  public int hashCode() {
    return encoded;
  }

  public static int decodeZ(int key) {
    return (key >> 28) + 8;
  }

  public static int decodeX(int key) {
    return (key >> 14) & ((1 << 14) - 1);
  }

  public static int decodeY(int key) {
    return (key) & ((1 << 14) - 1);
  }

  private static int encode(int x, int y, int z) {
    int max = 1 << z;
    if (x >= max) {
      x %= max;
    }
    if (x < 0) {
      x += max;
    }
    if (y < 0) {
      y = 0;
    }
    if (y >= max) {
      y = max;
    }
    // since most significant bit is treated as the sign bit, make:
    // z0-7 get encoded from 8 (0b1000) to 15 (0b1111)
    // z8-14 get encoded from 0 (0b0000) to 6 (0b0110)
    // so that encoded tile coordinates are ordered by zoom level
    if (z < 8) {
      z += 8;
    } else {
      z -= 8;
    }
    return (z << 28) | (x << 14) | y;
  }

  @Override
  public String toString() {
    return "{x=" + x + " y=" + y + " z=" + z + '}';
  }

  @Override
  public int compareTo(@NotNull TileCoord o) {
    return Long.compare(encoded, o.encoded);
  }
}
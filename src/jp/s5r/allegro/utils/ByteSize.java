package jp.s5r.allegro.utils;

public class ByteSize {
  private final static String[] UNIT = {
      "B", "KiB", "MiB", "GiB"
  };

  long mByteSize;

  public ByteSize(long byteSize) {
    mByteSize = byteSize;
  }

  public long getByteSize() {
    return mByteSize;
  }

  @Override
  public String toString() {
    String readableBytes = mByteSize + UNIT[0];
    long baseNumber = 1024;
    for (int i = 1; i < UNIT.length; i++) {
      double readableNum = mByteSize / baseNumber;
      if (readableNum < 1024) {
        readableBytes = readableNum + UNIT[i];
        break;
      }
      baseNumber *= 1024;
    }

    return readableBytes;
  }
}

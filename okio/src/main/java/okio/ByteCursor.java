package okio;

/**
 * A class that provides the ability to parse byte arrays.
 *
 * Current version accepts a preexisting byte array as a constructor parameter. Future versions will expand
 * that to allow for manipulation of objects.
 *
 * Not thread safe.
 */
public class ByteCursor {
  private final byte[] data;
  private int idxAnchor = 0; // This is the starting index of any parse searches

  public ByteCursor(byte[] data) { this.data = data; }

  public int getIndex() { return idxAnchor; }

  public void setIndex(int idx) {
    if (idx >= 0) {
      this.idxAnchor = idx;
    } else {
      this.idxAnchor = 0;
    }
  }

  public void reset() { setIndex(0); }

  // find returns the index of the first occurrence of 'byte b' in search range. if no match is found, returns -1
  public int find(byte[] ba, int start, int end) {
    if (start > end) throw new IndexOutOfBoundsException("start > end");
    if (end >= data.length) end = (data.length - 1);
    for (int i = start ; i <= end ; i++) {
      int idx = i + ba.length;
      int sl = 0;
      while (i < idx) {
        if (data[i] != ba[sl]) { i = i - sl; break; } // Resets indices to previous values and exits while loop
        if (sl == (ba.length - 1)) return i - sl; // Return starting 'data' index once the entire pattern matches
        i++; sl++;
      }
    }
    return -1;
  }

  public int find(byte[] b, int start) {
    return find(b, start, data.length);
  }

  public int find(byte[] b) {
    return find(b, 0, data.length);
  }

  public byte byteAt(int i) {
    if (i >= (data.length - 1)) throw new IndexOutOfBoundsException("i > data.length");
    return data[i];
  }

  public byte nextByte() {
    if (idxAnchor++ >= (data.length - 1)) throw new IndexOutOfBoundsException("end of array");
    return data[idxAnchor];
  }

  // getSubArray returns an inclusive sub array
  public byte[] getSubArray(int start, int end) {
    if (start > end) throw new IndexOutOfBoundsException("start > end");
    if (end >= data.length) end = (data.length - 1);
    byte[] temp = new byte[(data.length - start)];
    int ix = 0;
    for (int i = start ; i <= end ; i++) {
      temp[ix] = data[i];
      ix++;
    }
    return temp;
  }

  // getFirstToken returns the first exclusive token, or an empty array if delimiter not found
  public byte[] getToken(byte[] delim) {
    if (getIndex() != 0) reset();
    return getNextToken(delim);
  }

  // getNextToken returns the next exclusive token, or an empty array if delimiter not found
  public byte[] getNextToken(byte[] delim) {
    int jump;
    if (getIndex() >= (data.length - 1)) { reset(); return new byte[0]; } // end of array
    // If no additional delimiters are found, return the remainder of the array
    if ((jump = find(delim, getIndex())) == -1) { return getSubArray(getIndex(), (data.length - 1)); }
    byte[] result = getSubArray(getIndex(), (jump - 1));
    setIndex(jump + delim.length);
    return result;
  }
}

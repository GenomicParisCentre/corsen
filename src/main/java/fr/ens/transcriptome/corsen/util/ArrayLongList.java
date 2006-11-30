package fr.ens.transcriptome.corsen.util;
public final class ArrayLongList {

  public static int count = 0;

  private int id;
  private long[] _data;
  private int _size;
  private int _firstElement;

  private static final int MERGE_BUFFER_SIZE = 500000;

  public long get(final int index) {
    checkRange(index);
    return this._data[index + this._firstElement];
  }

  /**
   * Replaces the element at the specified position in me with the specified
   * element (optional operation).
   * @param index the index of the element to change
   * @param element the value to be stored at the specified position
   * @throws UnsupportedOperationException when this operation is not supported
   * @throws IndexOutOfBoundsException if the specified index is out of range
   */
  public void set(final int index, final long element) {
    checkRange(index);
    this._data[index + this._firstElement] = element;

  }

  public int size() {
    return this._size;
  }

  /**
   * Add the specified element at the end of the list.
   * @param element the value to insert
   * @throws UnsupportedOperationException when this operation is not supported
   * @throws IllegalArgumentException if some aspect of the specified element
   *           prevents it from being added to me
   * @throws IndexOutOfBoundsException if the specified index is out of range
   */
  public void add(final long element) {

    ensureCapacity(this._size + 1);
    this._data[this._size] = element;
    this._size++;
  }

  public long removeFirstElement() {

    if (this._size == 0)
      throw new IndexOutOfBoundsException("The ArrayList is empty");
    final long oldElement = this._data[this._firstElement];
    this._firstElement++;
    this._size--;

    if (this._size * 3 > this._data.length && this._data.length > 50000)
      trimToSize();

    return oldElement;
  }

  public long removeElementAt(final int index) {
    throw new RuntimeException("Method not implemented: removeElementAt");
  }

  private final void checkRange(final int index) {
    if (index < 0 || index >= this._size)
      throw new IndexOutOfBoundsException("Should be at least 0 and less than "
          + this._size + ", found " + index);
  }

  /**
   * Increases my capacity, if necessary, to ensure that I can hold at least the
   * number of elements specified by the minimum capacity argument without
   * growing.
   */
  public void ensureCapacity(final int mincap) {

    ensureCapacity(mincap, false);
  }

  private void ensureCapacity(final int mincap, final boolean merge) {
    if (mincap > this._data.length - this._firstElement) {

      // int newcap = ((_size) * 3) / 2 + 1;
      int newcap;

      if (merge)
        newcap = mincap;
      else if (this._size < 100000)
        newcap = this._size * 3 / 2 + 1;
      else
        newcap = mincap * 110 / 100;

      final long[] olddata = this._data;
      this._data = new long[newcap < mincap ? mincap : newcap];
      System.arraycopy(olddata, this._firstElement, this._data, 0, this._size);

      /*
       * System.out.println(this.hashCode() + " old data=" + olddata.length + "
       * _firstElement=" + _firstElement + " data=" + _data.length + " _size=" +
       * _size);
       */

      this._firstElement = 0;
    }
  }

  /**
   * Reduce my capacity, if necessary, to match my current {@link #size size}.
   */
  public void trimToSize() {

    if (this._size < this._data.length) {
      // System.out.println("Trim to size");
      // System.out.println("New size: " + _size);
      final long[] olddata = this._data;
      this._data = new long[this._size];
      System.arraycopy(olddata, this._firstElement, this._data, 0, this._size);
      this._firstElement = 0;
    }
  }

  public boolean isEmpty() {
    return 0 == size();
  }

  //
  // Other functions
  //

  public void merge(final ArrayLongList list) {

    if (this.id == list.id)
      return;

    final ArrayLongList current = this;

    while (list._size != 0) {

      final int n = list._size < MERGE_BUFFER_SIZE ? list._size
          : MERGE_BUFFER_SIZE;

      ensureCapacity(this._size + n, n == MERGE_BUFFER_SIZE);

      for (int i = 0; i < n; i++)
        this._data[this._firstElement + this._size + i] = list._data[list._firstElement
            + i];

      current._size += n;

      list._firstElement += n;
      list._size -= n;
      list.trimToSize();
    }

  }

  //
  // Constructors
  //

  public ArrayLongList(final int initialCapacity) {

    this.id = count++;

    if (initialCapacity < 0)
      throw new IllegalArgumentException("capacity " + initialCapacity);
    this._data = new long[initialCapacity];
    this._size = 0;
    this._firstElement = 0;
  }

  /**
   * Constructs a list by copying the specified array.
   * @param array the array to initialize the collection with
   * @throws NullPointerException if the array is <code>null</code>
   */
  public ArrayLongList(final long[] array) {
    this(array.length);
    System.arraycopy(array, 0, this._data, 0, array.length);
    this._size = array.length;
    this._firstElement = 0;
  }

  /**
   * Construct an empty list with the default initial capacity.
   */
  public ArrayLongList() {
    this(8);
  }
}

public final class ArrayLongList {

  public static int count = 0;

  private int id;
  private long[] _data;
  private int _size;
  private int _firstElement;

  private static final int MERGE_BUFFER_SIZE = 500000;

  public long get(int index) {
    checkRange(index);
    return _data[index + _firstElement];
  }

  /**
   * Replaces the element at the specified position in me with the specified
   * element (optional operation).
   * @param index the index of the element to change
   * @param element the value to be stored at the specified position
   * @throws UnsupportedOperationException when this operation is not supported
   * @throws IndexOutOfBoundsException if the specified index is out of range
   */
  public void set(int index, long element) {
    checkRange(index);
    _data[index + _firstElement] = element;

  }

  public int size() {
    return _size;
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

    ensureCapacity(_size + 1);
    _data[_size] = element;
    _size++;
  }

  public long removeFirstElement() {

    if (_size == 0)
      throw new IndexOutOfBoundsException("The ArrayList is empty");
    long oldElement = _data[_firstElement];
    _firstElement++;
    _size--;

    if (_size * 3 > _data.length && _data.length > 50000)
      trimToSize();

    return oldElement;
  }

  public long removeElementAt(final int index) {
    throw new RuntimeException("Method not implemented: removeElementAt");
  }

  private final void checkRange(final int index) {
    if (index < 0 || index >= _size) {
      throw new IndexOutOfBoundsException("Should be at least 0 and less than "
          + _size + ", found " + index);
    }
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
    if (mincap > (_data.length - _firstElement)) {

      // int newcap = ((_size) * 3) / 2 + 1;
      int newcap;

      if (merge)
        newcap = mincap;
      else if (_size < 100000)
        newcap = ((_size) * 3) / 2 + 1;
      else
        newcap = mincap * 110 / 100;

      long[] olddata = _data;
      _data = new long[newcap < mincap ? mincap : newcap];
      System.arraycopy(olddata, _firstElement, _data, 0, _size);

      /*
       * System.out.println(this.hashCode() + " old data=" + olddata.length + "
       * _firstElement=" + _firstElement + " data=" + _data.length + " _size=" +
       * _size);
       */

      _firstElement = 0;
    }
  }

  /**
   * Reduce my capacity, if necessary, to match my current {@link #size size}.
   */
  public void trimToSize() {

    if (_size < _data.length) {
      // System.out.println("Trim to size");
      // System.out.println("New size: " + _size);
      long[] olddata = _data;
      _data = new long[_size];
      System.arraycopy(olddata, _firstElement, _data, 0, _size);
      _firstElement = 0;
    }
  }

  public boolean isEmpty() {
    return (0 == size());
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

      ensureCapacity(_size + n, n == MERGE_BUFFER_SIZE);

      for (int i = 0; i < n; i++) {
        _data[_firstElement + _size + i] = list._data[list._firstElement + i];
      }

      current._size += n;

      list._firstElement += n;
      list._size -= n;
      list.trimToSize();
    }

  }

  //
  // Constructors
  //

  public ArrayLongList(int initialCapacity) {

    this.id = count++;

    if (initialCapacity < 0) {
      throw new IllegalArgumentException("capacity " + initialCapacity);
    }
    _data = new long[initialCapacity];
    _size = 0;
    _firstElement = 0;
  }

  /**
   * Constructs a list by copying the specified array.
   * @param array the array to initialize the collection with
   * @throws NullPointerException if the array is <code>null</code>
   */
  public ArrayLongList(long[] array) {
    this(array.length);
    System.arraycopy(array, 0, _data, 0, array.length);
    _size = array.length;
    _firstElement = 0;
  }

  /**
   * Construct an empty list with the default initial capacity.
   */
  public ArrayLongList() {
    this(8);
  }
}

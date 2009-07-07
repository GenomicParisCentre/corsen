/*
 *                  Corsen development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU General Public Licence version 2 or later. This
 * should be distributed with the code. If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Copyright for this code is held jointly by the microarray platform
 * of the École Normale Supérieure and the individual authors.
 * These should be listed in @author doc comments.
 *
 * For more information on the Corsen project and its aims,
 * or to join the Nividic mailing list, visit the home page
 * at:
 *
 *      http://www.transcriptome.ens.fr/corsen
 *
 */

package fr.ens.transcriptome.corsen.util;

import java.util.AbstractList;

public final class MinMaxList<E extends Comparable> extends AbstractList<E> {

  private E min;
  private E max;
  private int count = 0;

  @Override
  public E get(int index) {

    if (index < 0 || index >= this.count)
      throw new IndexOutOfBoundsException();

    if (this.count == 2)
      return index == 0 ? min : max;

    if (min == null)
      return max;

    return min;
  }

  @Override
  public int size() {

    return this.count;
  }

  @Override
  public boolean add(E o) {

    if (o == null)
      throw new NullPointerException("Object to add is null");

    if (this.count == 2) {

      if (o.compareTo(min) < 0) {
        this.min = o;
        return true;
      }

      if (o.compareTo(max) > 0)
        this.max = o;

      return true;
    }

    if (this.count == 0) {

      this.min = o;
      this.count = 1;
      return true;
    }

    if (this.count == 1) {

      if (o.compareTo(min) < 0) {
        this.max = this.min;
        this.min = o;
      } else
        this.max = o;

      this.count = 2;
      return true;
    }

    throw new IllegalStateException("The object can't have "
        + count + " values");

  }

  /*
   * (non-Javadoc)
   * @see java.util.AbstractList#add(int, java.lang.Object)
   */
  @Override
  public void add(int index, E element) {

    this.add(element);
  }

  /*
   * (non-Javadoc)
   * @see java.util.AbstractList#clear()
   */
  @Override
  public void clear() {

    this.min = null;
    this.max = null;
    this.count = 0;
  }

  /*
   * (non-Javadoc)
   * @see java.util.AbstractList#remove(int)
   */
  @Override
  public E remove(int index) {

    if (index < 0 || index >= this.count)
      throw new IndexOutOfBoundsException();

    if (index == 0) {

      E result = this.min;
      this.min = null;
      this.count--;

      return result;
    }

    E result = this.max;
    this.max = null;
    this.count--;

    return result;
  }

  /*
   * (non-Javadoc)
   * @see java.util.AbstractList#set(int, java.lang.Object)
   */
  @Override
  public E set(int index, E element) {

    if (index < 0 || index >= this.count)
      throw new IndexOutOfBoundsException();

    if (index == 0) {

      if (element.compareTo(min) < 0) {

        E result = this.min;
        this.min = element;

        return result;
      }
      return element;
    }

    if (element.compareTo(max) > 0) {

      E result = this.max;
      this.max = element;

      return result;
    }
    return element;

  }

}

/*
 *                      Nividic development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the microarray platform
 * of the �cole Normale Sup�rieure and the individual authors.
 * These should be listed in @author doc comments.
 *
 * For more information on the Nividic project and its aims,
 * or to join the Nividic mailing list, visit the home page
 * at:
 *
 *      http://www.transcriptome.ens.fr/nividic
 *
 */

package fr.ens.transcriptome.corsen.model;

public final class UnmodifiableListPoint3D extends AbstractListPoint3D{

  private AbstractListPoint3D list;
  
  @Override
  public void add(float x, float y, float z, int i) {
    
    throw new UnsupportedOperationException();
  }

  @Override
  public void applyXFactor(float xFactor) {
    
    this.list.applyXFactor(xFactor);
  }

  @Override
  public void applyYFactor(float yFactor) {
    
    this.list.applyYFactor(yFactor);
  }

  @Override
  public void applyZFactor(float zFactor) {
    
    this.list.applyZFactor(zFactor);
  }

  @Override
  public void ensureCapacity(int minCapacity) {    
  }

  @Override
  public int getIAt(int index) {
    
    return this.list.getIAt(index);
  }

  @Override
  public float getXAt(int index) {
    
    return this.list.getXAt(index);
  }

  @Override
  public float getYAt(int index) {
    
    return this.list.getYAt(index);
  }

  @Override
  public float getZAt(int index) {
    
    return this.list.getZAt(index);
  }

  @Override
  public void trimToSize() {
  }

  @Override
  public Point3D get(int index) {
    
    return this.list.get(index);
  }

  @Override
  public final int size() {
   
    return this.list.size();
  }
  
  //
  // Constructor
  //
  
  public UnmodifiableListPoint3D(final AbstractListPoint3D listPoints) {
    
    this.list = listPoints;
  }

}

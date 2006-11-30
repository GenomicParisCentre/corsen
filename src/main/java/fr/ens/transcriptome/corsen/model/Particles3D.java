package fr.ens.transcriptome.corsen.model;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Particles3D {

  public static final String WIDTH_KEY = "Width";
  public static final String HEIGHT_KEY = "Height";
  public static final String ZSLICES_KEY = "ZSlices";
  public static final String PIXEL_WIDTH_KEY = "PixelWidth";
  public static final String PIXEL_HEIGHT_KEY = "PixelHeight";
  public static final String PIXEL_DEPTH_KEY = "PixelDepth";
  public static final String UNIT_OF_LENGHT_KEY = "UnitOfLength";
  public static final String MIN_THRESHOLD_KEY = "MinThreshold";
  public static final String MAX_THRESHOLD_KEY = "MaxThreshold";

  private int width;
  private int height;
  private int zSlices;

  private float pixelWidth = 1.0f;
  private float pixelHeight = 1.0f;
  private float pixelDepth = 1.0f;

  private String unitOfLength = "";
  private double minThreshold;
  private double maxThreshold;

  Particle3D[] particles;

  //
  // Getters
  //

  /**
   * Get the height in pixel of the image.
   * @return Returns the height
   */
  public int getHeight() {
    return height;
  }

  /**
   * Get the maximun threshold of the image.
   * @return Returns the maxThreshold
   */
  public double getMaxThreshold() {
    return maxThreshold;
  }

  /**
   * Get the minimum threshold of the image.
   * @return Returns the minThreshold
   */
  public double getMinThreshold() {
    return minThreshold;
  }

  /**
   * Set the voxel depth.
   * @return Returns the pixelDepth
   */
  public float getPixelDepth() {
    return pixelDepth;
  }

  /**
   * Get the pixel height.
   * @return Returns the pixelHeight
   */
  public float getPixelHeight() {
    return pixelHeight;
  }

  /**
   * Get the pixel width.
   * @return Returns the pixelWidth
   */
  public float getPixelWidth() {
    return pixelWidth;
  }

  /**
   * Get the unit of length.
   * @return Returns the unitOfLength
   */
  public String getUnitOfLength() {
    return unitOfLength;
  }

  /**
   * Get the width of the image in pixel.
   * @return Returns the width
   */
  public int getWidth() {
    return width;
  }

  /**
   * Get the number of slices.
   * @return Returns the zSlices
   */
  public int getZSlices() {
    return zSlices;
  }

  /**
   * Get the number of particles.
   * @return The number of particles
   */
  public int getParticlesNumber() {

    if (this.particles == null)
      return 0;
    return this.particles.length;
  }

  /**
   * Get a Particle3D Object.
   * @param index Index of the particle3D
   * @return a Particle3D Object
   */
  public Particle3D getParticle(final int index) {

    if (this.particles == null)
      return null;

    return this.particles[index];
  }

  /**
   * Get the partiles.
   * @return an array of Particle3D Object
   */
  public Particle3D[] getParticles() {

    return this.particles;
  }

  //
  // Setters
  //

  /**
   * Set the particles.
   * @param particles Particles to set
   */
  public void setParticles(final Particle3D[] particles) {

    this.particles = particles;
  }

  //
  // Other methods
  //

  /**
   * Test if two particle are in the same dimension space.
   * @param pars Particles3D to test
   * @return true if the two particle are in the same dimension space
   */
  public boolean isSameDimensionSpace(final Particles3D pars) {

    if (pars == null)
      return false;

    return pars.width == this.width && pars.height == this.height
        && pars.zSlices == this.zSlices && pars.pixelWidth == this.pixelWidth
        && pars.pixelHeight == this.pixelHeight
        && pars.pixelDepth == this.pixelDepth;
  }

  /**
   * Read data from the output file of the ImageJ plugin to create a new
   * particle3D object.
   * @param file File to read
   * @throws IOException IOException if an error occurs while readinf data
   */
  public void readParticles(final File file) throws IOException {

    if (file == null)
      throw new NullPointerException("The file is null");

    final ArrayList al = new ArrayList();
    final BufferedReader in = new BufferedReader(new FileReader(file));

    String line;
    boolean header = true;
    while ((line = in.readLine()) != null)
      if (!line.startsWith("#")) {

        if (header) {

          if (line.startsWith("Name\t")) {
            header = false;
            continue;
          } else if (line.startsWith("Dimension")) {

            final String[] ch = line.split("\t");
            if (ch.length != 4)
              throw new RuntimeException(
                  "Invalid input file (Bad dimension field).");

            this.width = (int) Float.parseFloat(ch[1].trim());
            this.height = (int) Float.parseFloat(ch[2].trim());
            this.zSlices = (int) Float.parseFloat(ch[3].trim());
          } else {

            final String[] ch = line.split("=");
            if (ch.length == 2) {

              String key = ch[0].trim();
              String value = ch[1].trim();

              if (WIDTH_KEY.endsWith(key))
                this.width = Integer.parseInt(value.trim());
              else if (HEIGHT_KEY.endsWith(key))
                this.height = Integer.parseInt(value.trim());
              else if (ZSLICES_KEY.endsWith(key))
                this.zSlices = Integer.parseInt(value.trim());
              else if (PIXEL_WIDTH_KEY.endsWith(key))
                this.pixelWidth = Float.parseFloat(value.trim());
              else if (PIXEL_HEIGHT_KEY.endsWith(key))
                this.pixelHeight = Float.parseFloat(value.trim());
              else if (PIXEL_DEPTH_KEY.endsWith(key))
                this.pixelDepth = Float.parseFloat(value.trim());
              else if (UNIT_OF_LENGHT_KEY.endsWith(key))
                this.unitOfLength = value.trim();
              else if (MIN_THRESHOLD_KEY.endsWith(key))
                this.minThreshold = Double.parseDouble(value.trim());
              else if (MAX_THRESHOLD_KEY.endsWith(key))
                this.maxThreshold = Double.parseDouble(value.trim());
            }
          }

        } else
          al.add(new Particle3D(pixelWidth, pixelHeight, pixelDepth, line));

      }

    in.close();

    final Particle3D[] array = new Particle3D[al.size()];
    al.toArray(array);

    this.particles = array;
  }

  /**
   * Update z Coords
   * @param zCoef Z Coef
   */
  public void changeZCoord(final float zCoef) {

    final int n = getParticlesNumber();

    for (int i = 0; i < n; i++)
      changeZCoord(getParticle(i), zCoef);

    this.pixelDepth = this.pixelDepth * zCoef;
  }

  /**
   * Update z Coords
   * @param particle Particle to update
   * @param zCoef Z Coef
   */
  private void changeZCoord(final Particle3D particle, float zCoef) {

    if (particle == null)
      return;

    particle.applyZFactor(zCoef);
  }

  /**
   * Update Coords
   * @param coef Coef
   */
  public void changeAllCoord(final float coef) {

    if (coef == 1.0f)
      return;

    final int n = getParticlesNumber();

    for (int i = 0; i < n; i++)
      changeCoord(getParticle(i), coef);

    this.pixelWidth = this.pixelWidth * coef;
    this.pixelHeight = this.pixelHeight * coef;
    this.pixelDepth = this.pixelDepth * coef;
  }

  private void changeCoord(final Particle3D particle, float coef) {

    if (particle == null)
      return;

    particle.applyXFactor(coef);
    particle.applyYFactor(coef);
    particle.applyZFactor(coef);
  }

  public String toString() {

    return "Particles=" + this.particles;
  }

  //
  // Constructor
  //

  /**
   * Public constructor. Create a new particle with the dimension of another
   * object.
   * @param Particle File to read to create object
   */
  public Particles3D(final Particles3D pars) {

    this.width = pars.width;
    this.height = pars.height;
    this.zSlices = pars.zSlices;

    this.pixelWidth = pars.pixelWidth;
    this.pixelHeight = pars.pixelHeight;
    this.pixelDepth = pars.pixelDepth;

    this.unitOfLength = pars.unitOfLength;
    this.minThreshold = pars.minThreshold;
    this.maxThreshold = pars.maxThreshold;
  }
  
  /**
   * Public constructor. Create a new particle with the dimension of another
   * object.
   * @param Particle File to read to create object
   */
  public Particles3D(final Particles3D pars, final Particle3D [] particles) {

    this(pars);
    setParticles(particles);
  }

  /**
   * Public constructor.
   * @param file File to read to create object
   * @throws IOException
   */
  public Particles3D(final File file) throws IOException {

    readParticles(file);
  }

}

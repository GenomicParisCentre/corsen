package fr.ens.transcriptome.corsen.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import fr.ens.transcriptome.corsen.Globals;
import fr.ens.transcriptome.corsen.calc.ParticleType;

public class Particles3D {

  private static final String PAR_FILE_VERSION = "1.3";

  public static final String PAR_FILE_VERSION_KEY = "ParFileVersion";
  public static final String WIDTH_KEY = "Width";
  public static final String HEIGHT_KEY = "Height";
  public static final String ZSLICES_KEY = "ZSlices";
  public static final String PIXEL_WIDTH_KEY = "PixelWidth";
  public static final String PIXEL_HEIGHT_KEY = "PixelHeight";
  public static final String PIXEL_DEPTH_KEY = "PixelDepth";
  public static final String UNIT_OF_LENGHT_KEY = "UnitOfLength";
  public static final String MIN_THRESHOLD_KEY = "MinThreshold";
  public static final String MAX_THRESHOLD_KEY = "MaxThreshold";
  public static final String IMAGEFILE_KEY = "Image file name";

  private int width;
  private int height;
  private int zSlices;

  private float pixelWidth = 1.0f;
  private float pixelHeight = 1.0f;
  private float pixelDepth = 1.0f;

  private String unitOfLength = "";
  private double minThreshold;
  private double maxThreshold;
  private String name;
  private ParticleType type;
  private String imageFilename;
  private Date imageFilenameDate;

  private List<Particle3D> particles;

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
   * Get the partiles.
   * @return an array of Particle3D Object
   */
  public List<Particle3D> getParticles() {

    return this.particles;
  }

  /**
   * Get the type of the particles.
   * @return Returns the type
   */
  public ParticleType getType() {

    return type;
  }

  /**
   * Get the create date of the image
   * @return Returns the imageFilenameDate
   */
  public Date getImageFilenameDate() {
    return imageFilenameDate == null ? null : (Date) imageFilenameDate.clone();
  }

  /**
   * Get the name of the particle.
   * @return the name of the particle
   */
  public String getName() {

    return this.name;
  }

  //
  // Setters
  //

  /**
   * Set the particles.
   * @param particles Particles to set
   */
  public void setParticles(final Particle3D[] particles) {

    if (particles == null)
      throw new NullPointerException("Particles to set is null");

    setParticles(Arrays.asList(particles));
  }

  /**
   * Set the particles.
   * @param particles Particles to set
   */
  public void setParticles(final List<Particle3D> particles) {

    if (particles == null)
      if (particles == null)
        throw new NullPointerException("Particles to set is null");

    this.particles = Collections.unmodifiableList(particles);
  }

  /**
   * Set the type of the particles.
   * @param type The type to set
   */
  public void setType(final ParticleType type) {

    this.type = type;
  }

  /**
   * Get the image filename.
   * @return Returns the imageFilename
   */
  public String getImageFilename() {
    return imageFilename;
  }

  /**
   * Set the width of the image.
   * @param width The width to set
   */
  public void setWidth(final int width) {
    this.width = width;
  }

  /**
   * Set the height of the image.
   * @param height The height to set
   */
  public void setHeight(final int height) {
    this.height = height;
  }

  /**
   * Set the zSlices of the image.
   * @param slices The zSlices to set
   */
  public void setZSlices(final int slices) {
    zSlices = slices;
  }

  /**
   * Set the pixelwidth of the image.
   * @param pixelWidth The pixelWidth to set
   */
  public void setPixelWidth(final float pixelWidth) {
    this.pixelWidth = pixelWidth;
  }

  /**
   * Set the pixelHeight of the image.
   * @param pixelHeight The pixelHeight to set
   */
  public void setPixelHeight(final float pixelHeight) {
    this.pixelHeight = pixelHeight;
  }

  /**
   * Set the pixelDepth of the image.
   * @param pixelDepth The pixelDepth to set
   */
  public void setPixelDepth(final float pixelDepth) {
    this.pixelDepth = pixelDepth;
  }

  /**
   * Set the unit of length used in the image.
   * @param unitOfLength The unitOfLength to set
   */
  public void setUnitOfLength(final String unitOfLength) {
    this.unitOfLength = unitOfLength;
  }

  /**
   * Set the minimal threshold used in the image.
   * @param minThreshold The minThreshold to set
   */
  public void setMinThreshold(final double minThreshold) {
    this.minThreshold = minThreshold;
  }

  /**
   * Set the maximal threshold used in the image.
   * @param maxThreshold The maxThreshold to set
   */
  public void setMaxThreshold(final double maxThreshold) {
    this.maxThreshold = maxThreshold;
  }

  /**
   * Set the image filename.
   * @param imageFilename The imageFilename to set
   */
  public void setImageFilename(final String imageFilename) {
    this.imageFilename = imageFilename;
  }

  /**
   * Set the creation date of the image
   * @param imageFilenameDate The imageFilenameDate to set
   */
  public void setImageFilenameDate(final Date imageFilenameDate) {
    this.imageFilenameDate =
        imageFilenameDate == null ? null : (Date) imageFilenameDate.clone();
  }

  /**
   * Set the name of the particle.
   * @param name Name of the particle
   */
  public void setName(final String name) {

    if (name == null)
      return;
    this.name = name;
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

    return pars.width == this.width
        && pars.height == this.height && pars.zSlices == this.zSlices
        && pars.pixelWidth == this.pixelWidth
        && pars.pixelHeight == this.pixelHeight
        && pars.pixelDepth == this.pixelDepth;
  }

  /**
   * Read data from the output file of the ImageJ plugin to create a new
   * particle3D object.
   * @param file File to read
   * @throws IOException IOException if an error occurs while readinf data
   */
  private void readParticles(final InputStream is) throws IOException {

    if (is == null)
      throw new NullPointerException("The stream is null");

    final List<Particle3D> particles = new ArrayList<Particle3D>();
    final BufferedReader in = new BufferedReader(new InputStreamReader(is));

    String parVersion = null;

    String line;
    boolean header = true;
    while ((line = in.readLine()) != null)
      if (line.startsWith("# " + IMAGEFILE_KEY))
        this.imageFilename = line;
      else if (!line.startsWith("#")) {

        if (header) {

          if (line.startsWith("Name\t")) {
            header = false;

            if (!PAR_FILE_VERSION.equals(parVersion))
              throw new IOException("Can't read an old Par file format.");

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
              else if (IMAGEFILE_KEY.endsWith(key))
                this.imageFilename = value.trim();
              else if (PAR_FILE_VERSION_KEY.endsWith(key))
                parVersion = value;

            }
          }

        } else
          particles.add(new Particle3DBuilder(pixelWidth, pixelHeight,
              pixelDepth, line).getParticle());

      }

    in.close();

    setParticles(particles);
  }

  /**
   * Update z Coords
   * @param zCoef Z Coef
   */
  public void changeZCoord(final float zCoef) {

    if (zCoef == 1.0f)
      return;

    for (Particle3D par : this.particles)
      changeZCoord(par, zCoef);

    this.pixelDepth = this.pixelDepth * zCoef;
  }

  /**
   * Update z Coords
   * @param particle Particle to update
   * @param zCoef Z Coef
   */
  private void changeZCoord(final Particle3D particle, float zCoef) {

    if (particle == null || zCoef == 1.0f)
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

    for (Particle3D par : this.particles)
      changeCoord(par, coef);

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

  /**
   * Get the number of the innerpoints in all the particles
   * @return the number of the innerpoints in all the particles
   */
  public int countParticlesInnerPoints() {

    if (this.particles == null)
      return 0;

    int result = 0;

    for (Particle3D par : getParticles())
      result += par.getInnerPoints().size();

    return result;
  }

  /**
   * Get the number of the surfacepoints in all the particles
   * @return the number of the innerpoints in all the particles
   */
  public int countParticlesSurfacePoints() {

    if (this.particles == null)
      return 0;

    int result = 0;

    for (Particle3D par : getParticles())
      result += par.getSurfacePoints().size();

    return result;
  }

  /**
   * Save Particles to a Par file.
   * @param outputStream Output stream to use
   * @throws IOException if an error occurs while writing the file
   */
  public void saveParticles(OutputStream outputStream) throws IOException {

    Writer writer = new OutputStreamWriter(outputStream);

    writer.write("# Generated by ");
    writer.write(Globals.APP_NAME);
    writer.write(" version ");
    writer.write(Globals.APP_VERSION);
    writer.write(" (");
    writer.write(Globals.APP_BUILD_NUMBER);
    writer.write(", ");
    writer.write(Globals.APP_BUILD_DATE);
    writer.write(")");
    writer.write("\n# Generated on ");
    writer.write(new Date(System.currentTimeMillis()).toString());

    writer.write("\n# Image file name: ");

    writer.write(this.imageFilename != null
        ? this.imageFilename : "Unknown image file");
    writer.write("\n# Image created on ");

    writer.write(this.imageFilenameDate != null ? this.imageFilenameDate
        .toString() : "Unknown creation date");

    writer.write("\nParFileVersion=" + PAR_FILE_VERSION);

    writer.write("\n" + Particles3D.WIDTH_KEY + "=" + this.width);
    writer.write("\n" + Particles3D.HEIGHT_KEY + "=" + this.height);
    writer.write("\n" + Particles3D.ZSLICES_KEY + "=" + this.zSlices);

    writer.write("\n" + Particles3D.PIXEL_WIDTH_KEY + "=" + this.pixelWidth);
    writer.write("\n" + Particles3D.PIXEL_HEIGHT_KEY + "=" + this.pixelHeight);
    writer.write("\n" + Particles3D.PIXEL_DEPTH_KEY + "=" + this.pixelDepth);
    writer.write("\n"
        + Particles3D.UNIT_OF_LENGHT_KEY + "=" + this.unitOfLength);

    writer
        .write("\n" + Particles3D.MIN_THRESHOLD_KEY + "=" + this.minThreshold);
    writer
        .write("\n" + Particles3D.MAX_THRESHOLD_KEY + "=" + this.maxThreshold);

    writer
        .write("\nName\tCenter\tBarycenter\tArea\tVolume\tSphericity\tIntensity\tSurface points\tInner points\n");

    if (this.particles != null)
      for (Particle3D p : this.particles) {

        if (p.getIntensity() == 0)
          continue;

        writer.write(p.toString());
        writer.write('\n');
      }

    writer.close();
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
  public Particles3D(final Particles3D pars, final Particle3D[] particles) {

    this(pars);
    setParticles(particles);
  }

  /**
   * Public constructor. Create a new particle with the dimension of another
   * object.
   * @param Particle File to read to create object
   */
  public Particles3D(final Particles3D pars, final List<Particle3D> particles) {

    this(pars);
    setParticles(particles);
  }

  /**
   * Public constructor.
   * @param file InputStream to read to create object
   * @throws IOException
   */
  public Particles3D(final InputStream is) throws IOException {

    readParticles(is);
  }

  /**
   * Public constructor.
   * @param file File to read to create object
   * @throws IOException
   */
  public Particles3D(final File file) throws IOException {

    readParticles(new FileInputStream(file));
  }

  /**
   * Public constructor used by ImageJ Plugin.
   */
  public Particles3D() {
  }

}

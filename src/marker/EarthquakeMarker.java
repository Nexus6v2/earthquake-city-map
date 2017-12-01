package marker;

import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PConstants;
import processing.core.PGraphics;

/** Implements a visual marker for earthquakes on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Dmitry Barnukov
 *
 */

public abstract class EarthquakeMarker extends CommonMarker implements Comparable<EarthquakeMarker>
{
	// Did the earthquake occur on land?  This will be set by the subclasses.
	protected boolean isOnLand;

	// The radius of the Earthquake marker
	// You will want to set this in the constructor, either
	// using the thresholds below, or a continuous function
	// based on magnitude. 
	protected float radius;
	
	
	// constants for distance
	protected static final float kmPerMile = 1.6f;
	
	/** Greater than or equal to this threshold is a moderate earthquake */
	public static final float THRESHOLD_MODERATE = 5;
	/** Greater than or equal to this threshold is a light earthquake */
	public static final float THRESHOLD_LIGHT = 4;

	/** Greater than or equal to this threshold is an intermediate depth */
	public static final float THRESHOLD_INTERMEDIATE = 70;
	/** Greater than or equal to this threshold is a deep depth */
	public static final float THRESHOLD_DEEP = 300;

	// ADD constants for colors

	
	// abstract method implemented in derived classes
	public abstract void drawEarthquake(PGraphics pg, float x, float y);
		
	
	// constructor
	public EarthquakeMarker (PointFeature feature) 
	{
		super(feature.getLocation());
		// Add a radius property and then set the properties
		java.util.HashMap<String, Object> properties = feature.getProperties();
		float magnitude = Float.parseFloat(properties.get("magnitude").toString());
		properties.put("radius", 2 * magnitude);
		setProperties(properties);
		this.radius = 1.75f * getMagnitude();
	}
	
	/** Compares two markers depending on their magnitude, 
	 * to sort them in descending order
	 */
	public int compareTo(EarthquakeMarker marker) {
		if (this.getMagnitude() == marker.getMagnitude())
			return 0;
		else if (this.getMagnitude() < marker.getMagnitude())
			return 1;
		else
			return -1;
	}
	
	
	// calls abstract method drawEarthquake and then checks age and draws X if needed
	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		// save previous styling
		pg.pushStyle();
			
		// determine color of marker from depth
		colorDetermine(pg);
		
		// call abstract method implemented in child class to draw marker shape
		drawEarthquake(pg, x, y);
		
		// add X over marker if earthquake occurred within past day		
		markRecentEarthquake(pg, x, y);
		
		// reset to previous styling
		pg.popStyle();
		
	}

	/** Show the title of the earthquake if this marker is selected */
	public void showTitle(PGraphics pg, float x, float y)
	{
		String title = getTitle() + ", depth: " + getDepth() + " km.";
		float defX = x + 18;
		float defY = y + 18;
		
		pg.pushStyle();
		
		pg.stroke(110);
		pg.fill(240);
		pg.rect(defX, defY, pg.textWidth(title) + 6, 18);
		
		pg.textAlign(PConstants.LEFT, PConstants.TOP);
		pg.fill(0);
		pg.text(title, defX, defY);
		
		pg.popStyle();
		
	}

	
	/**
	 * Return the "threat circle" radius, or distance up to 
	 * which this earthquake can affect things, for this earthquake.   
	 * DISCLAIMER: this formula is for illustration purposes
	 *  only and is not intended to be used for safety-critical 
	 *  or predictive applications.
	 */
	public double threatCircle() {	
		double miles = 20.0f * Math.pow(1.8, 2*getMagnitude()-5);
		double km = (miles * kmPerMile);
		return km;
	}
	
	private void markRecentEarthquake(PGraphics pg, float x, float y) 
	{
//		Getting the 'age' property to see 
//		if an earthquake happened in past day
		String day = getAge();
		if (day.contains("Past Hour") || day.contains("Past Day"))
			drawCross(pg, x, y);
	}

	
	private void drawCross(PGraphics pg, float x, float y) 
	{
//		Draws cross on the marker
		pg.pushStyle();
		
		pg.strokeWeight(2);
		int buffer = 2;
		pg.line(x-(radius+buffer), 
				y-(radius+buffer), 
				x+radius+buffer, 
				y+radius+buffer);
		pg.line(x-(radius+buffer), 
				y+(radius+buffer), 
				x+radius+buffer, 
				y-(radius+buffer));
		
		pg.popStyle();
	}
	
	// determine color of marker from depth
	// We use: Deep = red, intermediate = blue, shallow = yellow
	private void colorDetermine(PGraphics pg) {
		float depth = getDepth();
		
		if (depth < THRESHOLD_INTERMEDIATE) {
			pg.fill(255, 255, 0);
		}
		else if (depth < THRESHOLD_DEEP) {
			pg.fill(0, 0, 255);
		}
		else {
			pg.fill(255, 0, 0);
		}
	}
	
	
	/** toString
	 * Returns an earthquake marker's string representation
	 * @return the string representation of an earthquake marker.
	 */
	public String toString() {
		return getTitle();
	}
	/*
	 * getters for earthquake properties
	 */
	
	public float getMagnitude() {
		return Float.parseFloat(getProperty("magnitude").toString());
	}
	
	public float getDepth() {
		return Float.parseFloat(getProperty("depth").toString());	
	}
	
	public String getTitle() {
		return (String) getProperty("title");	
		
	}
	
	public String getAge() {
		return (String) getProperty("age");
	}
	
	public float getRadius() {
		return Float.parseFloat(getProperty("radius").toString());
	}
	
	public boolean isOnLand() {
		return isOnLand;
	}
	
}

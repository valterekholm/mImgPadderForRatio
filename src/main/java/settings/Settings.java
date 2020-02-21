package settings;

import java.awt.Color;

public class Settings {
	
	private boolean replaceOriginalImage;//TODO: a String for suffix, with possible timestamp, or "_padded" if not set
	private String suffix;
	private boolean assertBothAxis;
	private double ratioX;
	private double ratioY;
	private Color backgroundColor;//TODO: implement, make also a method that derivate a background c from a 1-px border made from all image outer pixels / mean, but default = white
	
	public Settings() {
		replaceOriginalImage = false;
		suffix = "_padded";
		assertBothAxis = false;
		ratioX = 40;//a higher x-value will prevent wide images, if assertBothAxis is false
		ratioY = 41;//a higher y-value will prevent tall images, if assertBothAxis is false
		//TODO: test 1:1 ratio, will the assertBothAxis-value make difference?
		backgroundColor = Color.white;
	}

	public boolean isReplaceOriginalImage() {
		return replaceOriginalImage;
	}
	
	public void setReplaceOriginalImage(boolean replaceOriginalImage) {
		this.replaceOriginalImage = replaceOriginalImage;
	}

	public String getSuffix() {
		return suffix;
	}

	public boolean isAssertBothAxis() {
		return assertBothAxis;
	}
	
	public void setAssertBothAxis(boolean assertBothAxis) {
		this.assertBothAxis = assertBothAxis;
	}

	public double getRatioX() {
		return ratioX;
	}

	public double getRatioY() {
		return ratioY;
	}

	public void setRatioX(double ratioX) {
		if(ratioX > 0) this.ratioX = ratioX;
	}

	public void setRatioY(double ratioY) {
		if(ratioY > 0) this.ratioY = ratioY;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	@Override
	public String toString() {
		return "Settings [replaceOriginalImage=" + replaceOriginalImage + ", suffix=" + suffix + ", assertBothAxis="
				+ assertBothAxis + ", ratioX=" + ratioX + ", ratioY=" + ratioY + ", backgroundColor=" + backgroundColor
				+ "]";
	}
	
}
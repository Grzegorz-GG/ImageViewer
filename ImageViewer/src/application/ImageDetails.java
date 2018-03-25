package application;

import javafx.scene.effect.ColorAdjust;

public class ImageDetails {

	private String url;
	private ColorAdjust colorAdjust;
	
	ImageDetails(String url) {
		this.url=url;
		colorAdjust=new ColorAdjust();
	}
	
	protected String getUrl() {
		return url;
	}
	
	protected ColorAdjust getColorAdjust() {
		return colorAdjust;
	}
	
}

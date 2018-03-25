package application;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class MainViewController implements Initializable{

	@FXML
	ImageView picture;
	
	@FXML
	BorderPane mainPane;
	
	@FXML
	Slider hueSlider, brightnessSlider, saturationSlider, contrastSlider;
	
	private Main main;
	private int size;
	private static final int MIN_PIXELS = 10;
    private Rectangle2D primaryRectangle;
    private ObjectProperty<Point2D> initialPoint = new SimpleObjectProperty<>();


    @Override
	public void initialize(URL location, ResourceBundle resources) {
    	main = new Main();
    	size = main.getSize();	
	}

    
	@FXML
	private void loadImages() throws IOException {
	
		FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("View Pictures");
    	fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    	fileChooser.getExtensionFilters().addAll(
    			new FileChooser.ExtensionFilter("All Images",
    					"*.jpg", "*.jpeg", "*.png", "*.bmp", "*.gif"),
    			new FileChooser.ExtensionFilter("JPG", "*.jpg"),
    			new FileChooser.ExtensionFilter("JPEG", "*.jpeg"),
    			new FileChooser.ExtensionFilter("PNG", "*.png"),
    			new FileChooser.ExtensionFilter("BMP", "*.bmp"),
    			new FileChooser.ExtensionFilter("GIF", "*.gif")
    			);
    	
    	List<File> files = fileChooser.showOpenMultipleDialog(main.getStage());
    
		try {
			if(files != null) {
				
				for (File f: files) {	
					String url = f.toURI().toURL().toString();
					main.addImage(url);
				}
				 
				int currentindex = main.getCurrentIndex();
				Image image = new Image(main.getImageDetails(currentindex).getUrl());
				picture.setImage(image);
				primaryViewport();
				updateSliders();
				size = main.getSize();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@FXML
	private void quitApplication() {
		Platform.exit();	
	}
	
	
	@FXML
	private void saveFile() {
		
		FileChooser fileChooser = new FileChooser();
		File fileSave = fileChooser.showSaveDialog(main.getStage());
		
			if (fileSave != null) {
				
				WritableImage image = picture.snapshot(null, null);
				try {
					ImageIO.write(SwingFXUtils.fromFXImage(image, null),"png", fileSave);
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
	}
	
	
	@FXML
	private void goNext() {
		
		int currentindex = main.getCurrentIndex();
		
	        if (size == 0) {
	            main.setCurrentIndex(-1); 
	        } else if (size > 1 && currentindex < size - 1) {
	        	
	            main.setCurrentIndex(++currentindex);
	            Image image = new Image(main.getImageDetails(currentindex).getUrl());
	            picture.setImage(image);
	           
	            primaryViewport();
				updateSliders();
	        }       
	}
	
	
	@FXML
	private void goPrevious() {
		
		int currentindex = main.getCurrentIndex();
		
	        if (size == 0) {
	            main.setCurrentIndex(-1);
	        } else if (size > 1 && currentindex > 0) {
	        	
	            main.setCurrentIndex(--currentindex);
	            Image image = new Image(main.getImageDetails(currentindex).getUrl());
	            picture.setImage(image);
	            
	            primaryViewport();
				updateSliders();
	        }       
	}
	
	
	@FXML
	private void hueSliderProperty() {
		
		hueSlider.valueProperty().addListener( (invalidation) -> {
			
			main.getCurrentColorAdjust().setHue(hueSlider.getValue());
			picture.setEffect(main.getCurrentColorAdjust());
			
		});	
	}
	
	
	@FXML
	private void contrastSliderProperty() {
		
		contrastSlider.valueProperty().addListener( (invalidation) -> {
			
			main.getCurrentColorAdjust().setContrast(contrastSlider.getValue());
			picture.setEffect(main.getCurrentColorAdjust());
			
		});
	}
	
	
	@FXML
	private void brightnessSliderProperty() {
		
		brightnessSlider.valueProperty().addListener( (invalidation) -> {
			
			main.getCurrentColorAdjust().setBrightness(brightnessSlider.getValue());
			picture.setEffect(main.getCurrentColorAdjust());
			
		});
	}
	
	
	@FXML
	private void saturationSliderProperty() {
		
		saturationSlider.valueProperty().addListener( (invalidation) -> {
			
			main.getCurrentColorAdjust().setSaturation(saturationSlider.getValue());
			picture.setEffect(main.getCurrentColorAdjust());
			
		});
	}
	
	
	@FXML
	 private  void dragOverPicture(DragEvent event) {
	       
		Dragboard db = event.getDragboard();
		boolean isAccepted = false;
	       
	    for(File file: db.getFiles()) {
	    	if( file.getName().toLowerCase().endsWith(".png")  ||
	    		file.getName().toLowerCase().endsWith(".jpeg") ||
	    		file.getName().toLowerCase().endsWith(".jpg")  ||
	    		file.getName().toLowerCase().endsWith(".bmp")  ||
	    		file.getName().toLowerCase().endsWith(".gif")
	    	) {
	    		
	    		isAccepted=true;
	    	}
	    }
	 
	        if (db.hasFiles()) {
	            if (isAccepted) {
	                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
	            }
	        } else {
	            event.consume();
	        }
	    }
	
	
	@FXML
	private void dropPicture(DragEvent event) {
		
		Dragboard db = event.getDragboard();
		
		if(db.hasFiles()) {
			for(File file: db.getFiles()) {
				try {
					String url = file.toURI().toURL().toString();
					main.addImage(url);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		}
		
		size=main.getSize();
		Image image = new Image(main.getImageDetails(main.getCurrentIndex()).getUrl());
		picture.setImage(image);
		
		primaryViewport();
		updateSliders();
		
		event.setDropCompleted(true);
	    event.consume();
	}
	
	
	@FXML
	private void zoomPicture(ScrollEvent event) {

		if(main.getCurrentIndex() == -1)
			return;
		
		Rectangle2D viewport = picture.getViewport();
		double delta = event.getDeltaY();
		
		double scale = clamp(Math.pow(1.01, -delta),
            // don't scale so we're zoomed in to fewer than MIN_PIXELS in any direction:
            Math.min(MIN_PIXELS / viewport.getWidth() , MIN_PIXELS / viewport.getHeight()),
            // don't scale so that we're bigger than image dimensions:
            Math.max(primaryRectangle.getWidth() / viewport.getWidth(), primaryRectangle.getHeight() / viewport.getHeight())
        );
        
        Point2D mouse = imageViewToImage(picture, new Point2D(event.getSceneX(), event.getSceneY()));
        double newWidth = viewport.getWidth() * scale;
        double newHeight = viewport.getHeight() * scale;

        // To keep the visual point under the mouse from moving, we need
        // (x - newViewportMinX) / (x - currentViewportMinX) = scale
        // where x is the mouse X coordinate in the image
        // solving this for newViewportMinX gives
        // newViewportMinX = x - (x - currentViewportMinX) * scale 
        // we then clamp this value so the image never scrolls out
        // of the imageview:

        double newMinX = clamp(mouse.getX() - (mouse.getX() - viewport.getMinX()) * scale, 
                0, picture.getImage().getWidth() - newWidth);
        double newMinY = clamp(mouse.getY() - (mouse.getY() - viewport.getMinY()) * scale, 
                0, picture.getImage().getHeight() - newHeight);
        
        picture.setViewport(new Rectangle2D(newMinX, newMinY, newWidth, newHeight));
	}
	
	
	@FXML
	private void doubleClick(MouseEvent event) {
		
		if (event.getClickCount() == 2) {
			picture.setViewport(primaryRectangle);
	    }
		
	}
	
	
	@FXML
	private void getInitialPoint(MouseEvent event) {
		
		Point2D point = imageViewToImage(picture, new Point2D(event.getX(), event.getY()));
        initialPoint.set(point);
        
        picture.setCursor(Cursor.OPEN_HAND);
	}

	
	@FXML
	private void shiftViewport(MouseEvent event) {
		
		 Point2D point = imageViewToImage(picture, new Point2D(event.getX(), event.getY()));
         point = point.subtract(initialPoint.get());
		 
		 Rectangle2D viewport = picture.getViewport();

         double maxX = picture.getImage().getWidth() - viewport.getWidth();
         double maxY = picture.getImage().getHeight() - viewport.getHeight();
         
         double minX = clamp(viewport.getMinX() - point.getX(), 0, maxX);
         double minY = clamp(viewport.getMinY() - point.getY(), 0, maxY);

         picture.setViewport(new Rectangle2D(minX, minY, viewport.getWidth(), viewport.getHeight()));
     
         initialPoint.set(imageViewToImage(picture, new Point2D(event.getX(), event.getY())));
         
	}
	
	@FXML
	private void changeToDefaultCursor() {
		
		picture.setCursor(Cursor.DEFAULT);
		
	}


	private double clamp(double value, double min, double max) {

		if (value < min)
			return min;
		if (value > max)
        	return max;
        	
        return value;
       }
	
	   // convert mouse coordinates in the imageView to coordinates in the actual image:
    private Point2D imageViewToImage(ImageView imageView, Point2D imageViewCoordinates) {
    	
        double xProportion = imageViewCoordinates.getX() / imageView.getBoundsInLocal().getWidth();
        double yProportion = imageViewCoordinates.getY() / imageView.getBoundsInLocal().getHeight();

        Rectangle2D viewport = imageView.getViewport();
        
        return new Point2D(
                viewport.getMinX() + xProportion * viewport.getWidth(), 
                viewport.getMinY() + yProportion * viewport.getHeight());
    }
	
    
    
    private void primaryViewport() {	
    	
		primaryRectangle = new Rectangle2D(0, 0, picture.getImage().getWidth(), picture.getImage().getHeight());
		picture.setViewport(primaryRectangle);
		
	}

    
	private void updateSliders() {
		
		int currentindex = main.getCurrentIndex();
		ImageDetails imageInfo = main.getImageDetails(currentindex);
		
		ColorAdjust colorAdjustInfo = imageInfo.getColorAdjust();
		
		colorAdjustInfo.setHue(colorAdjustInfo.getHue());
		colorAdjustInfo.setContrast(colorAdjustInfo.getContrast());
		colorAdjustInfo.setBrightness(colorAdjustInfo.getBrightness());
		colorAdjustInfo.setSaturation(colorAdjustInfo.getSaturation());
		
		hueSlider.setValue(colorAdjustInfo.getHue());
		contrastSlider.setValue(colorAdjustInfo.getContrast());
		brightnessSlider.setValue(colorAdjustInfo.getBrightness());
		saturationSlider.setValue(colorAdjustInfo.getSaturation());
		
		picture.setEffect(colorAdjustInfo);
	}
	
}
package application;
	
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Pagination;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;


public class Main extends Application {
	
	private BorderPane root;
	private Stage primaryStage;
	private Map<Integer,ImageDetails> imageFiles = new HashMap<>();
	int currentIndex = -1;
	
	@Override
	public void start(Stage primaryStage) {
		
		this.primaryStage=primaryStage;
		this.primaryStage.setTitle("Image Viewer");
		
		try {
			showMainView();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void showMainView() throws IOException {
		
		FXMLLoader loader=new FXMLLoader();
		loader.setLocation(Main.class.getResource("MainView.fxml"));
		root=loader.load();
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	protected void addImage(String url) {
		currentIndex = getLastIndex();
		imageFiles.put(++currentIndex, new ImageDetails(url));
	}
	
	protected ImageDetails getImageDetails(int currentIndex) {
		return imageFiles.get(currentIndex);
	}
	
	protected ColorAdjust getCurrentColorAdjust() {
		return imageFiles.get(currentIndex).getColorAdjust();
	}
	
	protected Integer getSize() {
		return imageFiles.size();
	}
	
	protected void setCurrentIndex(int newindex) {
		currentIndex=newindex;
	}
	
	protected int getCurrentIndex() {
		return currentIndex;
	}
	
	protected int getLastIndex() {
		if(currentIndex == -1)
			return -1;
		else {
			return imageFiles.size() - 1;
		}
	}
		
	protected Stage getStage() {
		return primaryStage;
	}
	
	protected BorderPane getRoot() {
		return root;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
}

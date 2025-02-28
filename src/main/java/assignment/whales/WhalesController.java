package assignment.whales;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ouda
 */
public class WhalesController implements Initializable {

    @FXML
    private MenuBar mainMenu;
    @FXML
    private ImageView image;
    @FXML
    private BorderPane WhalePortal;
    @FXML
    private Label title;
    @FXML
    private Label about;
    @FXML
    private Button play;
    @FXML
    private Button puase;
    @FXML
    private ComboBox size;
    @FXML
    private TextField name;
    Media media;
    MediaPlayer player;
    OrderedDictionary database = null;
    WhaleRecord whale = null;
    int whaleSize = 1;

    @FXML
    public void exit() {
        Stage stage = (Stage) mainMenu.getScene().getWindow();
        stage.close();
    }

    public void find() {
        DataKey key = new DataKey(this.name.getText(), this.size.getSelectionModel().getSelectedIndex());
        try {
            whale = database.find(key);
            showWhale();
        } catch (DictionaryException ex) {
            displayAlert(ex.getMessage());
        }
    }

    public void delete() {
        WhaleRecord previousWhale = null;
        try {
            previousWhale = database.predecessor(whale.getDataKey());
        } catch (DictionaryException ex) {

        }
        WhaleRecord nextWhale = null;
        try {
            nextWhale = database.successor(whale.getDataKey());
        } catch (DictionaryException ex) {

        }
        DataKey key = whale.getDataKey();
        try {
            database.remove(key);
        } catch (DictionaryException ex) {
            System.out.println("Error in delete "+ ex);
        }
        if (database.isEmpty()) {
            this.WhalePortal.setVisible(false);
            displayAlert("No more whales in the database to show");
        } else {
            if (previousWhale != null) {
                whale = previousWhale;
                showWhale();
            } else if (nextWhale != null) {
                whale = nextWhale;
                showWhale();
            }
        }
    }

    private void showWhale() {
        play.setDisable(false);
        puase.setDisable(true);
        if (player != null) {
            player.stop();
        }
        String img = whale.getImage();
        Image whaleImage = new Image("file:src/main/resources/assignment/whales/images/" + img);
        image.setImage(whaleImage);
        title.setText(whale.getDataKey().getWhaleName());
        about.setText(whale.getAbout());
    }

    private void displayAlert(String msg) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Alert.fxml"));
            Parent ERROR = loader.load();
            AlertController controller = (AlertController) loader.getController();

            Scene scene = new Scene(ERROR);
            Stage stage = new Stage();
            stage.setScene(scene);

            stage.getIcons().add(new Image("file:src/main/resources/assignment/whales/images/UMIcon.png"));
            stage.setTitle("Dictionary Exception");
            controller.setAlertText(msg);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException ex1) {

        }
    }

    public void getSize() {
        switch (this.size.getValue().toString()) {
            case "Small":
                this.whaleSize = 1;
                break;
            case "Medium":
                this.whaleSize = 2;
                break;
            case "Large":
                this.whaleSize = 3;
                break;
            default:
                break;
        }
    }

    public void first() {
        // Write this method
        WhaleRecord temp = this.whale;
        try {
            this.whale = database.smallest();
            this.showWhale();
        }
        catch(Exception e){
            this.whale = temp;
            displayAlert("There are no Whales loaded");
        }
    }

    public void last() {
        // Write this method
        WhaleRecord temp = this.whale;
        try {
            this.whale = database.largest();
            this.showWhale();
        }
        catch(Exception e){
            this.whale = temp;
            displayAlert("There are no Whales loaded");
        }
    }

    public void next() {
        WhaleRecord temp = this.whale;
        try {
            this.whale = database.successor(whale.getDataKey());
            this.showWhale();
        }
        catch(Exception e){
            this.whale = temp;
            displayAlert("This is the last Whale");
        }
    }

    public void previous() {
        WhaleRecord temp = this.whale;
        try {

            this.whale = database.predecessor(whale.getDataKey());
            this.showWhale();
        }
        catch(Exception e){
            this.whale = temp;
            displayAlert("This is the first Whale");
        }
    }

    public void play() {
        String filename = "src/main/resources/assignment/whales/sounds/" + whale.getSound();
        media = new Media(new File(filename).toURI().toString());
        player = new MediaPlayer(media);
        play.setDisable(true);
        puase.setDisable(false);
        player.setCycleCount(MediaPlayer.INDEFINITE);
        player.play();
    }

    public void pause() {
        play.setDisable(false);
        puase.setDisable(true);
        if (player != null) {
            player.stop();
            //player.setAutoPlay(false);
        }
    }

    public void loadDictionary() {
        Scanner input;
        int line = 0;
        try {
            String whaleName = "";
            String description;
            int size = 0;
            input = new Scanner(new File("WhalesDatabase.txt"));
            while (input.hasNext()) // read until  end of file
            {
                String data = input.nextLine();
                switch (line % 3) {
                    case 0:
                        size = Integer.parseInt(data);
                        break;
                    case 1:
                        whaleName = data;
                        break;
                    default:
                        description = data;
                        //database.insert(new WhaleRecord(new DataKey(whaleName, size), description, whaleName + ".mp3", whaleName + ".jpg"));
                        DataKey key = new DataKey(whaleName, size);
                        WhaleRecord record = new WhaleRecord(key, description, whaleName + ".mp3", whaleName + ".jpg");
                        database.insert(record);

                        break;
                }
                line++;
            }
        } catch (IOException e) {
            System.out.println("There was an error in reading or opening the file: WhalesDatabase.txt");
            System.out.println(e.getMessage());
        } catch (DictionaryException ex) {
            Logger.getLogger(WhalesController.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.WhalePortal.setVisible(true);
        this.first();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        database = new OrderedDictionary();
        size.setItems(FXCollections.observableArrayList(
                "No specific size","Small", "Medium", "Large"
        ));
        size.setValue("No specific size");
    }

}

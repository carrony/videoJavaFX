package gui;


import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

public class MediaPlayerControls extends BorderPane {
	
	private MediaPlayer mp;
    private MediaView mediaView;
    private final boolean repeat = false;
    private boolean stopRequested = false;
    private boolean atEndOfMedia = false;
    private Duration duration;
    private Slider timeSlider;
    private Label playTime;
    private Slider volumeSlider;
    private HBox mediaBar;
	private boolean startUp;
	
	// imágenes del botón
     private ImageView viewBotonPlay; 
     private ImageView viewBotonPause; 
	
    
    public MediaPlayerControls(MediaPlayer mp) {
    	this.mp = mp;
        setStyle("-fx-background-color: #bfc2c7;");
        mediaView = new MediaView(mp);
        Pane mvPane = new Pane() {                };
        mvPane.getChildren().add(mediaView);
        
        // ajustamos el mediaplayer a la ventana
        DoubleProperty mvw = mediaView.fitWidthProperty();
        DoubleProperty mvh = mediaView.fitHeightProperty();
        mvh.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height").subtract(50));
        mediaView.setPreserveRatio(true);
        
        mvPane.setStyle("-fx-background-color: black;"); 
        setCenter(mvPane);
        
        mediaBar = new HBox();
        mediaBar.setAlignment(Pos.CENTER);
        mediaBar.setPadding(new Insets(5, 10, 5, 10));
        BorderPane.setAlignment(mediaBar, Pos.CENTER);
 

        final Button playButton  = new Button();
        
        viewBotonPlay = new ImageView(new Image("imagenes/play.png"));
        viewBotonPause = new ImageView(new Image("imagenes/pause.png"));
        viewBotonPlay.setFitHeight(30);
        viewBotonPlay.setPreserveRatio(true);
        viewBotonPause.setFitHeight(30);
        viewBotonPause.setPreserveRatio(true);
        playButton.setPrefSize(30, 30);
        playButton.setGraphic(viewBotonPlay);
        mediaBar.getChildren().add(playButton);
        setBottom(mediaBar); 
        
        // Añadir espacio
        Label spacer = new Label("   ");
        mediaBar.getChildren().add(spacer);
         
        // Añadir etiqueta de tiempo
        Label timeLabel = new Label("Time: ");
        mediaBar.getChildren().add(timeLabel);
         
        // Añadir slider para el tiempo
        timeSlider = new Slider();
        HBox.setHgrow(timeSlider,Priority.ALWAYS);
        timeSlider.setMinWidth(50);
        timeSlider.setMaxWidth(Double.MAX_VALUE);
        mediaBar.getChildren().add(timeSlider);

        // Añadir etiqueta del tiempo actual
        playTime = new Label();
        playTime.setPrefWidth(130);
        playTime.setMinWidth(50);
        mediaBar.getChildren().add(playTime);
         
        // Añadir etiqueta del volumenl
        Label volumeLabel = new Label("Vol: ");
        mediaBar.getChildren().add(volumeLabel);
         
        // Añadir slider del volumen
        volumeSlider = new Slider();        
        volumeSlider.setPrefWidth(70);
        volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
        volumeSlider.setMinWidth(30);
         
        mediaBar.getChildren().add(volumeSlider);
        
        mediaView.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	 play();
            }
        });
        
        playButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
            	play();
            }
        });
        
        
        mp.currentTimeProperty().addListener(new InvalidationListener() 
        {
            public void invalidated(Observable ov) {
                updateValues();
            }
        });
 
        mp.setOnPlaying(new Runnable() {
            public void run() {
                if (stopRequested) {
                    mp.pause();
                    stopRequested = false;
                } else {
                	playButton.setGraphic(viewBotonPause);
                }
            }
        });
 
        mp.setOnPaused(new Runnable() {
            public void run() {
                System.out.println("onPaused");
                playButton.setGraphic(viewBotonPlay);
            }
        });
 
        mp.setOnReady(new Runnable() {
            public void run() {
                duration = mp.getMedia().getDuration();
                updateValues();
				if (startUp) {
					mp.play();
				}
            }
        });
 
        mp.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
        mp.setOnEndOfMedia(new Runnable() {
            public void run() {
                if (!repeat) {
                	playButton.setGraphic(viewBotonPlay);
                    stopRequested = true;
                    atEndOfMedia = true;
                }
            }
       });
        
        
        timeSlider.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	 actualizaProgreso();
            }
        });
        
        timeSlider.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	actualizaProgreso();
            }
        });
        
        
        volumeSlider.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                 actualizaVolumen();
            }
        });
        
        volumeSlider.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	actualizaVolumen();
            }
        });
        
        
     }


    /** 
     * Método que se invoca para modificar el progrewso del vídeo. Es invocado por el listener de drag y de click 
     * en la barra de progreso, para que se modifique la posición del vídeo al pulsar o arrastrar en el slier
     * de la barra de progreso del vídeo
     */
    protected void actualizaVolumen() {
    	mp.setVolume(volumeSlider.getValue() / 100.0);
	}


    /** 
     * Método que se invoca para modificar el progreso del volumen. Es invocado por el listener de drag y de click 
     * en la barra de volumen, para que se modifique el nivel de volumen del vídeo al pulsar o arrastrar en el slider
     * de la barra de volumen
     */
	protected void actualizaProgreso() {
    	mp.seek(duration.multiply(timeSlider.getValue() / 100.0));
	}


	/*
     * Método que reproduce el vídeo. Controla los posibles estados por los que puede pasar el vídeo
     * TAmbién reinicia el vídeo si llega al final
     */
	protected void play() {
		Status status = mp.getStatus();
		 
        if (status == Status.UNKNOWN  || status == Status.HALTED)
        {
           // No se hace nada en estos estados
           return;
        }
 
		if ( status == Status.PAUSED
		     || status == Status.READY
		     || status == Status.STOPPED) {
		     // reinicia el vídeo si llega al final
		if (atEndOfMedia) {
		    mp.seek(mp.getStartTime());
		    atEndOfMedia = false;
		 }
		 mp.play();
		 } else {
		   mp.pause();
		 }
	}
	
	/**
	 *  Método encargado de actualizar el tiempo a medida que el vídeo va avanzado y 
	 *  los valores de los slider, tanto del progreso del vídeo, como para el volumen.
	 */
	protected void updateValues() {
		  if (playTime != null && timeSlider != null && volumeSlider != null) {
		     Platform.runLater(new Runnable() {
		        public void run() {
		          Duration currentTime = mp.getCurrentTime();
		          playTime.setText(formatTime(currentTime, duration));
		          timeSlider.setDisable(duration.isUnknown());
		          if (!timeSlider.isDisabled() 
		            && duration.greaterThan(Duration.ZERO) 
		            && !timeSlider.isValueChanging()) {
		              timeSlider.setValue(currentTime.divide(duration).toMillis()
		                  * 100.0);
		          }
		          if (!volumeSlider.isValueChanging()) {
		            volumeSlider.setValue((int)Math.round(mp.getVolume() 
		                  * 100));
		          }
		        }
		     });
		  }
		}
	
	/**
	 * Método utilizado para realizar conversiones de tiempo y devuelve la representanción en formato
	 * String. Se utiliza para obtener la representación que se muestra en la barra de navegación del 
	 * vídeo
	 * @param elapsed instante de tiempo por el que vael vídeo
	 * @param duration duración total del vídeo
	 * @return String con la representación del tipo hh:mm:ss/hh:mm:ss
	 */
	private static String formatTime(Duration elapsed, Duration duration) {
		   int intElapsed = (int)Math.floor(elapsed.toSeconds());
		   int elapsedHours = intElapsed / (60 * 60);
		   if (elapsedHours > 0) {
		       intElapsed -= elapsedHours * 60 * 60;
		   }
		   int elapsedMinutes = intElapsed / 60;
		   int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 
		                           - elapsedMinutes * 60;
		 
		   if (duration.greaterThan(Duration.ZERO)) {
		      int intDuration = (int)Math.floor(duration.toSeconds());
		      int durationHours = intDuration / (60 * 60);
		      if (durationHours > 0) {
		         intDuration -= durationHours * 60 * 60;
		      }
		      int durationMinutes = intDuration / 60;
		      int durationSeconds = intDuration - durationHours * 60 * 60 - 
		          durationMinutes * 60;
		      if (durationHours > 0) {
		         return String.format("%d:%02d:%02d/%d:%02d:%02d", 
		            elapsedHours, elapsedMinutes, elapsedSeconds,
		            durationHours, durationMinutes, durationSeconds);
		      } else {
		          return String.format("%02d:%02d/%02d:%02d",
		            elapsedMinutes, elapsedSeconds,durationMinutes, 
		                durationSeconds);
		      }
		      } else {
		          if (elapsedHours > 0) {
		             return String.format("%d:%02d:%02d", elapsedHours, 
		                    elapsedMinutes, elapsedSeconds);
		            } else {
		                return String.format("%02d:%02d",elapsedMinutes, 
		                    elapsedSeconds);
		            }
		        }
		    }


	/*
	 *  Método que para la reproducción del vídeo
	 */
	public void stop() {
		mp.stop();
	}
	
	/**
	 * Método que establece si la reproducción del vídeo debe ser automática una vez abierto o si el 
	 * reproductor debe empezar parado
	 * @param startUp boolean true si queremos que el vídeo se comience a reproducir, false si queremos el vídeo
	 * permanezca parado hasta que el usuario pulse el botón de play
	 */
	public void setAutoStart(boolean startUp) {
		this.startUp=startUp;
	}
        
}
    

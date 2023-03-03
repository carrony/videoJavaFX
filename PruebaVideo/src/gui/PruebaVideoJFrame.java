package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class PruebaVideoJFrame extends JFrame {

	private JPanel contentPane;
	private final JFXPanel panelVideo;
	private MediaPlayerControls reproductor;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PruebaVideoJFrame frame = new PruebaVideoJFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public PruebaVideoJFrame() {
		panelVideo = new JFXPanel();
		reproductor=null;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		
		JButton btnReproducir = new JButton("reproducir");
		btnReproducir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				playVideo();
			}
		});
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.add(btnReproducir, BorderLayout.NORTH);
		contentPane.add(panelVideo, BorderLayout.CENTER);
	}

	protected void playVideo() {
		
		JFileChooser abrirDialogo = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		URL url=null;
		String fichero=null;
        // invocamos al diálogo de abrir fichero.
        int respuesta = abrirDialogo.showOpenDialog(null);

        // si el usuario selecciona un fichero
        if (respuesta == JFileChooser.APPROVE_OPTION) {
            fichero=abrirDialogo.getSelectedFile().getAbsolutePath();
            File f= new File(fichero);
            System.out.println(f.toURI());
            
            /*
            File f= new File("videos/explicacion1.mp4");
            if (reproductor!=null) {
            	reproductor.stop();
            	reproductor=null;
            } 
        	reproductor = new MediaPlayerControls(new MediaPlayer(new Media( f.toURI().toString())));
        	panelVideo.setScene(new Scene(new Group(reproductor)));
        	
        	reproductor.setAutoStart(false);
        	
        	*/
            
            if (reproductor!=null) {
            	reproductor.stop();
            	reproductor=null;
            } 
        	reproductor = new MediaPlayerControls(new MediaPlayer(new Media( f.toURI().toString())));
        	panelVideo.setScene(new Scene(new Group(reproductor)));
        	reproductor.setAutoStart(true);

        } else {
        	// si el usuario cancela el cuadro de diálogo
        	
        }

        
		
	}

}

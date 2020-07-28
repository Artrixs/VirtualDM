package virtualdm;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import loader.Loader;
import ql.Ql;
import trackinfrastructure.Layout;
import trackinfrastructure.trackelements.Switch;
import trackinfrastructure.trackelements.Track;
import train.Train;
import utils.ID;


public class Game extends Application {
	
	private static long time;
	
	private Train train;
	private Layout layout;
	private Ql ql;
	
	private Label l_trainInfo, l_clock;
	
	private Pane root;
	
	private List<Train> trains = new ArrayList<Train>();

	
	public Game() {
		time = System.nanoTime();
		
        System.out.println("Loading...");
        try {
        	layout = Loader.load("resources/lines/testLine");
        	ql = new Ql(600,200, layout);
        } catch (RuntimeException e) {
        	e.printStackTrace();
        	System.exit(1);
        }
        System.out.println("Done loading");
        
        train = new Train(100);
		Track track = (Track) layout.getTrackElement(ID.getFromString("Tesla:track1"));
		train.setPosition(track.getRoute(track.getPoint('A')), 125);
		trains.add(train);
	}
	
	@Override
	public void init() {	
        l_trainInfo = new Label("Train info goes here");
        l_clock = new Label("Clock: 00:02:03");
        
    	Button startClock = new Button("Start");
        Button stopClock = new Button("Stop");
        Button startTrain = new Button("Start train");
        Button reverseTrain = new Button("Reverse");
        Button switchB = new Button("Switch to B");
        Button switchC = new Button("Switch to C");
        
        startClock.setOnAction(event -> {
        	Clock.start();
        });
        stopClock.setOnAction(event ->{
        	Clock.stop();
        });
        startTrain.setOnAction(event -> {
        	train.start();
        });
        reverseTrain.setOnAction(event ->{
        	train.reverse();
        });
        
        var trackSwitch = (Switch) layout.getTrackElement(ID.getFromLocationName("Tesla", "switch1"));
        switchB.setOnAction(event -> {
        	trackSwitch.setOnB();
        });
        switchC.setOnAction(event -> {
        	trackSwitch.setOnC();
        });
        
        var buttonBox = new HBox(startClock, stopClock, startTrain, reverseTrain, switchB, switchC);
        root = new VBox(l_clock, l_trainInfo, buttonBox, ql);
	}
	
    @Override
    public void start(Stage stage) {	
        Scene scene = new Scene(root, 600, 400);
        
        stage.setTitle("VirtualDM");
        stage.setScene(scene);      
        stage.show();
		
        Clock.set(0, 0, 0);
		Clock.setTimeCompression(1);
		Clock.start();
		
		AnimationTimer animator = new AnimationTimer(){
          	@Override
             public void handle(long arg0) {
            	    update();      	
             }      
         };
         animator.start();  		
    }
    
    private void update() {
    	if ( Clock.isRunning() ) {
	   		Clock.tick();
	   		l_clock.setText("Clock. " + Clock.string());
	   		 
	   		layout.getUpdateList().runUpdates();
	   		ql.update();
	   		          	 
	    	StringBuilder builder = new StringBuilder();
	    	ListIterator<Train> iter = trains.listIterator();
	    	Train t;
	    	while(iter.hasNext()) {
	    		t = iter.next();
	    		t.update();
	    		builder.append(t+"\n");
	    		if(t.isToBeRemoved())
	    			iter.remove();
	    	}
	    	l_trainInfo.setText(builder.toString());  	
    	} 
    }

    public static void main(String[] args) {
    		try {
    			launch(Game.class, args);
    		}catch (RuntimeException e) {
    			System.out.println(e);
    			e.printStackTrace();
    			System.exit(1);
    		}
    }
    
    public static void printTime() {
    	System.out.println((System.nanoTime() - time)/1000000000.0);
    	time = System.nanoTime();
    }

}
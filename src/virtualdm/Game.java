package virtualdm;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import apparatus.TrackCircuit;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import trackinfrastructure.trackelements.EntryExitPoint;
import trackinfrastructure.trackelements.Switch;
import trackinfrastructure.trackelements.Track;
import trackinfrastructure.trackside.Signal;
import train.TargetSpeed;
import train.Train;
import utils.UpdateList;
import train.TargetSpeed.Type;


public class Game extends Application {
	
	private Label label;
	private Label clock;
	private Label circuitL;
	private Train train;
	private TrackCircuit circuit;
	private List<Train> trains = new ArrayList<Train>();
	
	private UpdateList updateList;
	
    @Override
    public void start(Stage stage) {
    	stage.setTitle("Let's try something different!");
        label = new Label("Train info goes here");
        clock = new Label("Clock: 00:02:03");
        circuitL = new Label("Circuit info goes here");
        Button start = new Button("Start");
        Button stop = new Button("Stop");
        Button startTrain = new Button("Start/stop train");
        Button reverseTrain = new Button("Reverse");
        start.setOnAction(event -> {
        	Clock.start();
        });
        stop.setOnAction(event ->{
        	Clock.stop();
        });
        startTrain.setOnAction(event -> {
        	train.start();
        });
        reverseTrain.setOnAction(event ->{
        	train.reverse();
        });
        
        updateList = new UpdateList();
        
        Scene scene = new Scene(new VBox(clock, circuitL,label, new HBox(start,stop,startTrain,reverseTrain)), 600, 200);
        stage.setScene(scene);
        stage.show();
        
        /**
        EntryExitPoint entry = new EntryExitPoint("entry",trains);
        Track track1 = new Track("Track1", 1000);
		Track track2 = new Track("Track2", 600);
		Track track3 = new Track("Track3", 400);
		Switch switch1 = new Switch("Switch1", 10,20,30);
		EntryExitPoint exit = new EntryExitPoint("exit", trains);
		
		entry.setConnectionPoint(track1.getPointA());
		//track1.setConnectionB(switch1.getPointA());
		track2.setConnectionA(switch1.getPointB());
		track3.setConnectionA(switch1.getPointC());
		exit.setConnectionPoint(track2.getPointB());
		
		
		
		circuit = new TrackCircuit("CB1", updateList);
		track1.setParentTrackCircuit(circuit);
		switch1.setParentTrackCircuit(circuit);
		
		Signal signal1 = new Signal("Signal 1");
		track1.addTracksideElement(signal1, track1.getPointA(), 150, 1);
		signal1.setAspect(Signal.Aspect.GREEN);
		
		Signal signal2 = new Signal("Signal 2");
		track1.addTracksideElement(signal2, track1.getPointB(), 800, -1);

		//train = new Train(100);
		//train.setPosition(track2.getRoute(track2.getPointA()), 20, 0/3.6);
		//trains.add(train);
		**/
        Track track1 = new Track("track1",400);
        Track track2 = new Track("track2",50);
        Track track3 = new Track("track3",400);
        
        track1.setConnectionB(track2.getPointA());
        track2.setConnectionB(track3.getPointA());
        
		train = new Train(100);
		train.setPosition(track3.getRoute(track3.getPointA()), 25);
		trains.add(train);
		
		//train.start();
		//train.addTargetSpeed(10/3.6, 0, TargetSpeed.Type.LINE);
		//train = exit.enterTrain(100, 10/3.6);
		//train.addTargetSpeed(10/3.6, 0, TargetSpeed.Type.LINE);
        
        Clock.set(0, 0, 0);
		Clock.setTimeCompression(1);
		Clock.start();
		
		 AnimationTimer animator = new AnimationTimer(){

             @Override
             public void handle(long arg0) {
            	 if(Clock.isRunning()) {
            		 Clock.tick();
            		 clock.setText("Clock. " + Clock.string());
            		 
            		 updateList.runUpdates();
            		          	 
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
             		 label.setText(builder.toString());
             		 //circuitL.setText(circuit.toString());
            	 }          	
             }      
         };
         animator.start();  		
    }

    public static void main(String[] args) {
    		try {
    			launch();
    		}catch (RuntimeException e) {
    			System.out.println(e);
    		}
    }

}
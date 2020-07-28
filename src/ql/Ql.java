package ql;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import trackinfrastructure.Layout;
import trackinfrastructure.TrackCircuit;
import utils.ID;

public class Ql extends Canvas{
	private GraphicsContext gc;
	private TrackCircuit tc1;
	private TrackCircuit tc2;
	private TrackCircuit tc3;
	
	public Ql(int width, int height, Layout layout) {
		super(width, height);
		gc = this.getGraphicsContext2D();
		tc1 = layout.getTrackCircuit(ID.getFromLocationName("Tesla", "ct1"));
		tc2 = layout.getTrackCircuit(ID.getFromLocationName("Tesla", "ct2"));
		tc3 = layout.getTrackCircuit(ID.getFromLocationName("Tesla", "ct3"));
	}
	
	private void draw() {
		//Circuit 1
		setColorFromTrackCircuitState(tc1);
		gc.fillRect(50, 150, 100, 30);
		
		//circuit 2
		setColorFromTrackCircuitState(tc2);
		gc.fillRect(200, 150, 100, 30);
		
		//circuit 3
		setColorFromTrackCircuitState(tc3);
		gc.fillRect(200, 80, 100, 30);
	}
	
	private void setColorFromTrackCircuitState(TrackCircuit trackCircuit) {
		if( trackCircuit.isOccupied() ) 
			gc.setFill(Color.RED);
		else
			gc.setFill(Color.BLACK);
	}
	
	public void update() {
		draw();
	}
}

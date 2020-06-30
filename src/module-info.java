module virtualdm {
	exports virtualdm;
	
	requires javafx.controls;
	requires javafx.fxml;
	requires transitive javafx.graphics;
	
	opens virtualdm to javafx.fxml;
}
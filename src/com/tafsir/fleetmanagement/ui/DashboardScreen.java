package com.tafsir.fleetmanagement.ui;

import java.io.File;

import com.tafsir.fleetmanagement.ui.components.PrimaryButton;

import javafx.application.HostServices;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

//>> The Dashboard screen: explains the four Tableau dashboards and opens the live workbook in Tableau Desktop.
//>> The workbook reads the fleet_management database directly, so it always shows the current data.
public class DashboardScreen extends VBox {

	// The workbook sits in the project folder, which is the working folder when the app runs from Eclipse
	private static final String WORKBOOK = "FMAS_Dashboard.twb";

	private final HostServices host;
	private final MessageBar messageBar = new MessageBar();

	public DashboardScreen(HostServices host) {
		this.host = host;
		setSpacing(12);
		setPadding(new Insets(24));

		Label title = new Label("FLEET DASHBOARDS");
		title.getStyleClass().add("section-title");

		Label intro = new Label("Four Tableau dashboards built on the live fleet database. "
				+ "Open them in Tableau Desktop to filter by time window, top N and vehicle.");
		intro.getStyleClass().add("muted");
		intro.setWrapText(true);

		PrimaryButton open = new PrimaryButton("Open live dashboard in Tableau");
		open.setOnAction(e -> openWorkbook());

		Label list = new Label(
				"Fleet Pulse: KPIs, spend, MPG and the weekly rhythm\n"
				+ "Leaderboard: driver podium, vehicle medals, top fuel and maintenance\n"
				+ "Cost Control: cost against usage, cost per mile, maintenance mix\n"
				+ "Anomaly Watch: weekly MPG heatmap and the alert list");
		list.setLineSpacing(6);

		Label note = new Label("Tableau Desktop must be installed. If it asks you to sign in to MySQL, "
				+ "use the same database login as FMAS. The dashboards update when you press F5 in Tableau.");
		note.getStyleClass().add("muted");
		note.setWrapText(true);

		getChildren().addAll(title, intro, open, messageBar, list, note);
	}

	private void openWorkbook() {
		File file = new File(WORKBOOK);
		if (!file.exists()) {
			messageBar.error("Could not find " + WORKBOOK + " in the project folder");
			return;
		}
		try {
			host.showDocument(file.toURI().toString());
			messageBar.success("Opening the dashboard in Tableau. This can take a few seconds.");
		} catch (Exception e) {
			e.printStackTrace();
			messageBar.error("Could not open the dashboard. Open " + WORKBOOK + " in Tableau Desktop.");
		}
	}
}
package com.example.proyectosmov;

import android.app.Activity;
import android.util.Log;

public class OrganizationChart {
    private Activity activity;
    private static OrganizationChart instance;
    public String htmlCode = "";
    private OrganizationChart(Activity activity) {
        this.activity = activity;
    }
    public static OrganizationChart getInstance(Activity activity) {
        if (instance == null) {
            instance = new OrganizationChart(activity);

        }
        return instance;
    }
    public void addChildToParent(String Child,String Parent){
        htmlCode += "['"+Child+"', '"+Parent+"', ''],";
    }
    public void addChildToParent(String Child,String ChildFunction,String Parent){
        htmlCode += "[{'v':'"+Child+"', 'f':'Child"+ChildFunction+"'}, '"+Parent+"', ''],";
    }
    public void clearData(String Parent,String Child){
        htmlCode = "";
    }
    public String getChart(){
        Log.d("OrganizationChart","HTML: "+ htmlCode);
        Log.d("OrganizationChart","HTML1: "+ removeLastChar(htmlCode));
        return getTopCode() + removeLastChar(htmlCode) +getBottomCode();
    }
    private String getTopCode(){
        String topCode = "";
        topCode += "<html>";
        topCode += "<head>";
        topCode += "<script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>";
        topCode += "<script type=\"text/javascript\">";
        topCode += "google.charts.load('current', {packages:[\"orgchart\"]});";
        topCode += "google.charts.setOnLoadCallback(drawChart);";
        topCode += "function drawChart() {";
        topCode += "var data = new google.visualization.DataTable();";
        topCode += "data.addColumn('string', 'Name');";
        topCode += "data.addColumn('string', 'Manager');";
        topCode += "data.addColumn('string', 'ToolTip');";
        topCode += "data.addRows([";
        return topCode;
    }
    private String getBottomCode(){
        String bottomCode = "";
        bottomCode += "]);";
        bottomCode += "var chart = new google.visualization.OrgChart(document.getElementById('chart_div'));";
        bottomCode += " chart.draw(data, {'allowHtml':true});";
        bottomCode += " }";
        bottomCode += " </script>";
        bottomCode += "</head>";
        bottomCode += "<body>";
        bottomCode += "<div id=\"chart_div\"></div>";
        bottomCode += " </html>";
        return bottomCode;
    }
    private  String removeLastChar(String str) {
        return removeLastChars(str, 1);
    }
    private String removeLastChars(String str, int chars) {
        return str.substring(0, str.length() - chars);
    }

}

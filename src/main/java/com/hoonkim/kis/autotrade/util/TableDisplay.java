package com.hoonkim.kis.autotrade.util;

public class TableDisplay {

    // Method to display the formatted table
    public static void displayTable(StringBuilder input) {
     
    	System.out.println (input.toString());
    	System.out.println (generateHtmlTable(input));
    }
    
    // Method to generate HTML table from StringBuilder input
    public static String generateHtmlTable(StringBuilder input) {
        // Split the input into rows
        String[] rows = input.toString().split("\n");

        // Initialize a StringBuilder to hold the HTML table
        StringBuilder html = new StringBuilder();

        // Start the HTML table
        html.append("<!DOCTYPE html>\n<html>\n<head>\n<title>Table</title>\n")
            .append("<style>table {border-collapse: collapse;} th, td {border: 1px solid black; padding: 8px;}</style>\n")
            .append("</head>\n<body>\n")
            .append("<table>\n");

        // Loop through each row to generate table rows
        boolean isHeader = true; // Flag for header row
        for (String row : rows) {
            String[] cells = row.split(";");
            
            // Create a table row
            html.append("<tr>");
            
            // Loop through each cell in the row
            for (String cell : cells) {
                // If it's the first row, treat it as a header
                if (isHeader) {
                    html.append("<th>").append(cell).append("</th>");
                } else {
                    html.append("<td>").append(cell).append("</td>");
                }
            }

            // Close the table row
            html.append("</tr>\n");

            // After the first row, set the flag to false (no more headers)
            isHeader = false;
        }

        // End the HTML table
        html.append("</table>\n</body>\n</html>");

        return html.toString();
    }
 }
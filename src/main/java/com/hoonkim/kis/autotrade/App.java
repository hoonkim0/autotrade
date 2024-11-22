package com.hoonkim.kis.autotrade;

import com.hoonkim.kis.autotrade.auth.AccessToken;
import com.hoonkim.kis.autotrade.auth.AppSecKey;
import com.hoonkim.kis.autotrade.sql.MasterDataReader;
import com.hoonkim.kis.autotrade.util.LocalTime;
import org.apache.commons.cli.*;
import com.hoonkim.kis.autotrade.order.Scheduler;

public class App {

	public static void main(String[] args) throws Exception {

		AppSecKey key = AppSecKey.readFromFile();
		AccessToken aToken = new AccessToken(key);
		MasterDataReader hdb = new MasterDataReader();

		Scheduler scheduler = new Scheduler(key, aToken, hdb);
		
		Options options = new Options();

		// Define parameters and options

		options.addOption("c", "command", true, "Action to perform");
		options.addOption("p", "pdno", true, "product number");
		options.addOption("q", "qty", true, "Quantity");
		options.addOption("r", "repeat", true, "Repeat");
		options.addOption("d", "delay", true, "Delay");
		options.addOption("l", "limit", true, "Limit");
		options.addOption("s", "start timestamp", true, "Starting Date and Time");
		//options.addOption("c", "command", true, "Action to perform");
		
		String command = new String("");
		String pdno = new String("");
		String qty = new String("");
		String repeat = new String("");
		String delay = new String("");
		String limit = new String("");
		String start = new String("");
		
		int dly = 0;
		int rpt = 0;
		int lmt = 0;
		int iqty = 0;
		
        CommandLineParser parser = new DefaultParser();
		
		try {
			CommandLine cmd = parser.parse(options, args);
			if (cmd.hasOption("c")) {
				command = cmd.getOptionValue("c");
			}
			if (cmd.hasOption("p")) {
				pdno = cmd.getOptionValue("p");
			}
			if (cmd.hasOption("q")) {
				qty = cmd.getOptionValue("q");
			}
			if (cmd.hasOption("r")) {
				repeat = cmd.getOptionValue("r");
			}
			if (cmd.hasOption("d")) {
				delay = cmd.getOptionValue("d");
			}
			if (cmd.hasOption("l")) {
				limit = cmd.getOptionValue("l");
			}
			if (cmd.hasOption("s")) {
				start = cmd.getOptionValue("s");
			} else start = LocalTime.getLocalDateTime14();
		} catch (ParseException e) {
			System.out.println("Error parsing arguments: " + e.getMessage());
			System.exit(1);
		}
		if (command.equals("cron")) {
			// Perform the main cron job
			scheduler.cron();
		} else if (verifyCmd(command, pdno, qty, repeat, delay, limit, start)) {
			rpt = repeat.equals("") ? 1 : Integer.parseInt(repeat);
			dly = delay.equals("") ? 0 : Integer.parseInt(delay);
			lmt = limit.equals("") ? 0 : Integer.parseInt(limit);
			iqty = qty.equals("") ? 1 : Integer.parseInt(qty);
			//System.out.println(command + "/" + pdno + "/" + qty + "/" + rpt + "/" + dly + "/" + lmt);
		} else System.exit(1); 
		
		
		if (command.equals("buy")) {
			
			scheduler.scheduleNewOrder(start,pdno,rpt, dly, lmt, iqty);

		}
	}

	static boolean verifyCmd(String c, String p, String q, String r, String d, String l, String s) {

		if (c.equals("") || p.equals("") || q.equals("") || s.equals("")) {
			System.out.println("Error parsing arguments: the required argument(s) missing : -c, -p, -q");
			return false;
		}
		if (!c.equals("buy") && !c.equals("sell")) {
			System.out.println("Error parsing arguments: the command must be either buy or sell");
			return false;
		}
		if (p.length() != 6) {
			System.out.println("Error parsing arguments: the product code must be 6 characters");
			return false;
		} else {
			if (!numberCheck(p)) {
				System.out.println("Error parsing arguments: the product code must contain only numeric characters");
				return false;
			}
		}
		if (!numberCheck(q)) {
			System.out.println("Error parsing arguments: the qty must contain only numeric characters");
			return false;
		}
		if (!r.equals("") && !numberCheck(r)) {
			System.out.println("Error parsing arguments: the repeat option must contain only numeric characters");
			return false;
		}
		if (!d.equals("") && !numberCheck(d)) {
			System.out.println("Error parsing arguments: the delay option must contain only numeric characters");
			return false;
		}

		if (!l.equals("") && !numberCheck(l)) {
			System.out.println("Error parsing arguments: the limit option must contain only numeric characters");
			return false;
		}
		if (!s.equals("") && !numberCheck(s)) {
			System.out.println("Error parsing arguments: the start date time option must contain only numeric characters (YYYYMMDDHHMMSS)");
			return false;
		} else if (s.length() != 14) {
			System.out.println("Error parsing arguments: the start date time option must be 14 characters (YYYYMMDDHHMMSS)");
			return false;
		}
		return true;
	}

	static boolean numberCheck(String s) {
		try {
			Long.parseLong(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}



}
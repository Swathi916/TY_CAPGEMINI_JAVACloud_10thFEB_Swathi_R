package com.tyss.capgemini.lps.controller;

import java.util.Scanner;

import org.apache.log4j.Logger;

import com.tyss.capgemini.lps.validation.Validations;

public class HomePage {
	static Logger log = Logger.getLogger(HomePage.class);

	public static void getStarted() {
		int userChoice = 0;
		try (Scanner scanner = new Scanner(System.in)) {
			while (true) {
				while (true) {
					log.info("====================================================");
					log.info("||        Welcome to Loan Processing System       ||");
					log.info("====================================================");
					log.info("----------------------------------------------------");
					log.info("-                Choose the User type              -");
					log.info("----------------------------------------------------");
					log.info("	1 - Admin          : ");
					log.info("	2 - Customer       : ");
					log.info("	3 - Approval Team  : ");
					log.info("----------------------------------------------------");
					log.info("Enter your choice    : ");
					String us = scanner.nextLine();
					if (Validations.isNumber(us)) {
						userChoice = Integer.parseInt(us);
						break;
					} else {
						log.info("Enter Number Only!!");
					}
				}

				switch (userChoice) {
				case 1:
					AdminController admin = new AdminController();
					while (true) {
						log.info("------------------------------------------------");
						log.info("||          Welcome to Admin Section          ||");
						log.info("------------------------------------------------");
						if (admin.adminLogin()) {
							break;
						} else {
							log.info("Login Unsuccessful!!!");
							admin.adminLogin();
						}
					}
					break;
				case 2:
					CustomerController customerController = new CustomerController();
					while (true) {
						log.info("------------------------------------------------");
						log.info("||        Welcome to Customer Section         ||");
						log.info("------------------------------------------------");
						log.info("Press 1 to logIn       :");
//						log.info("Press 2 to Register    :");
						log.info("Enter your choice      :");
						int choice = scanner.nextInt();
						switch (choice) {
						case 1:
							customerController.logIn();
							if (customerController.logIn()) {
								break;
							} else {
								log.info("Login Unsuccessful!!!");
								customerController.logIn();
							}
							break;
//						case 2:
//							customerController.register();
//							break;
						default:
							log.info("Invalid choice!");
							break;
						}
					}
				case 3:
					ApprovalTeamController approvalTeamController = new ApprovalTeamController();
					while (true) {
						log.info("------------------------------------------------");
						log.info("||     Welcome to Approval Team Section       ||");
						log.info("------------------------------------------------");
						if (approvalTeamController.approvalLogin()) {
							break;
						} else {
							log.info("Login Unsuccessful!!!");
							approvalTeamController.approvalLogin();
						}
					}
					break;
				default:
					log.info("Enter Proper option!!");
				}
			}
		}
	}
}

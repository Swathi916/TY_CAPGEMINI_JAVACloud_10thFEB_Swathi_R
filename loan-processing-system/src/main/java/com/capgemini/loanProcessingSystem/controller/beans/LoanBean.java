package com.capgemini.loanProcessingSystem.beans;

import lombok.Data;

@Data
public class LoanBean {
 private  String bankName;
 private  String loanType;
 private String emi;
 private String  rateOfInterest;
 private String loanAmount;
}

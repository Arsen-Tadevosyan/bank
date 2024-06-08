package com.example.bankcommon.util;


import com.example.bankcommon.entity.Transaction;
import com.example.bankcommon.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class FileUtil {

    @Value("${contract.upload.directory}")
    private String uploadDirectory;


    public File contractFile(User user, Transaction transaction) {

        String filePath = System.currentTimeMillis() + user.getEmail() + "txt";

        File file = new File(uploadDirectory, filePath);
        try {

            FileWriter writer = new FileWriter(file);

            // Header
            writer.write("Loan Bank\n\n");

            // Parties Information
            writer.write("This Banking Services Agreement (\"Agreement\") is entered into on " +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " between:\n\n");
            writer.write("BANK:\n");
            writer.write("    Name: [Loan Bank]\n");
            writer.write("    Address: [ITSpace]\n\n");
            writer.write("CLIENT:\n");
            writer.write("    Name: " + user.getName() + " " + user.getSurname() + "\n");
            writer.write("    Email: " + user.getEmail() + "\n");
            writer.write("    Phone: " + user.getPhone() + "\n\n");

            // Transaction Details
            writer.write("TRANSACTION DETAILS\n\n");
            writer.write("Transaction Type: " + transaction.getTransactionType().name() + "\n");
            writer.write("Transaction Size: " + transaction.getSize() + "\n");
            writer.write("Transaction Rate: " + transaction.getPercentage() + "\n");
            writer.write("Transaction Start Date: " + transaction.getIssueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "\n");
            writer.write("Transaction Finish Date: " + transaction.getFinishDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "\n\n");

            // Client Responsibilities
            writer.write("CLIENT RESPONSIBILITIES\n\n");
            writer.write("The Client agrees to:\n");
            writer.write("1. Provide accurate and complete personal information required for account opening and maintenance.\n");
            writer.write("2. Comply with the Bank's rules, regulations, policies, and procedures governing the use of banking services.\n\n");

            // Fees and Charges
            writer.write("FEES AND CHARGES\n\n");
            writer.write("The Client acknowledges and agrees to pay the following fees and charges associated with the banking services:\n");
            writer.write("1. Monthly maintenance fee: $X.XX\n");
            writer.write("2. Overdraft fee: $X.XX per occurrence\n\n");

            // Term and Termination
            writer.write("TERM AND TERMINATION\n\n");
            writer.write("This Agreement shall remain in effect until terminated by either party upon written notice to the other party.\n");
            writer.write("Upon termination, the Client shall settle any outstanding balances and return any bank-issued materials, such as debit cards, to the Bank.\n\n");

            // Governing Law
            writer.write("GOVERNING LAW\n\n");
            writer.write("This Agreement shall be governed by and construed in accordance with the laws of [Jurisdiction].\n");
            writer.write("Any disputes arising under or in connection with this Agreement shall be resolved through arbitration.\n\n");

            // Signature Section
            writer.write("IN WITNESS WHEREOF\n\n");
            writer.write("[Loan Bank]\n\n");
            writer.write("By: ____________________________\n\n");
            writer.write("[" + user.getName() + "]");
            writer.write("By: ____________________________\n\n");

            // Close the FileWriter
            writer.close();
            System.out.println("Text has been written to the file successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
        return file;
    }
}


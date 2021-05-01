import java.util.Scanner;

public class ATM {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Bank theBank = new Bank("Bank of Drausin");

        // add a user, which also creates a savings account
        User aUser = theBank.addUser("John", "Doe", "1234");

        // add a checking account for our user
        Account newAccount = new Account("Checking", aUser, theBank);
        aUser.addAccount(newAccount);
        theBank.addAccount(newAccount);

        User curUser;
        while (true) {

            // stay in the login prompt until successful login
            curUser = ATM.mainMenuPrompt(theBank, sc);

            // stay in main menu until user quits
            ATM.printUserMenu(curUser, sc);
        }
    }

    /**
     * Print the ATM's login menu
     *
     * @param theBank the Bank object whose accounts to use
     * @param sc      the Scanner object to use for user input
     * @return the authenticated User object
     */
    public static User mainMenuPrompt(Bank theBank, Scanner sc) {

        // inits
        String userID;
        String pin;
        User authUser;

        // prompt the user for userID and pin combo until correct one is reached
        do {
            System.out.printf("\n\nWelcome to %s\n\n", theBank.getName());
            System.out.println("Enter user ID: ");
            userID = sc.nextLine();
            System.out.println("Enter pin: ");
            pin = sc.nextLine();

            // try to get the user object corresponding to ID and the pin combo
            authUser = theBank.userLogin(userID, pin);
            if (authUser == null) {
                System.out.println("Incorrect userID/pin combination. Please try again");
            }

        } while (authUser == null); // continue until successful login

        return authUser;
    }

    public static void printUserMenu(User theUser, Scanner sc) {

        //print summary of the user's accounts
        theUser.printAccountsSummary();

        // init
        int choice;

        //user menu
        do {
            System.out.printf("Welcome %s, what would you like to do?\n", theUser.getFirstName());
            System.out.println("    1) Show accounts transaction history");
            System.out.println("    2) Withdrawal");
            System.out.println("    3) Deposit");
            System.out.println("    4) Transfer");
            System.out.println("    5) Quit");
            System.out.println();
            System.out.println("Enter choice: ");
            choice = sc.nextInt();

            if (choice < 1 || choice > 5) {
                System.out.println("Invalid choice. Please choose 1-5");
            }

        } while (choice < 1 || choice > 5);

        // process the choice

        // gobble up the rest of previous input line
        switch (choice) {
            case 1 -> ATM.showTransHistory(theUser, sc);
            case 2 -> ATM.withdrawFunds(theUser, sc);
            case 3 -> ATM.depositFunds(theUser, sc);
            case 4 -> ATM.transferFunds(theUser, sc);
            case 5 -> sc.nextLine();
        }

        // redisplay this menu unless user wants to quit
        if (choice != 5) {
            ATM.printUserMenu(theUser, sc);
        }
    }


    public static void showTransHistory(User theUser, Scanner sc) {

        int theAcct;

        do {
            System.out.printf("Enter the number (1-%d) of the account\n" +
                    "whose transaction you want to see: ", theUser.numAccounts());
            theAcct = sc.nextInt() - 1;
            if (theAcct < 0 || theAcct >= theUser.numAccounts()) {
                System.out.println("Invalid account. Please try again");
            }

        } while (theAcct < 0 || theAcct >= theUser.numAccounts());

        // print the transaction history
        theUser.printAcctTransHistory(theAcct);
    }

    public static void transferFunds(User theUser, Scanner sc) {

        // inits
        int fromAcct;
        int toAcct;
        double amount;
        double acctBal;


        fromAcct = getTheAccount(theUser,sc, "transfer from");

        acctBal = theUser.getAcctBalance(fromAcct);

        toAcct = getTheAccount(theUser,sc, "transfer to");

        // get the amount to transfer
        do {
            System.out.printf("Enter the amount to transfer (max $%.02f): $", acctBal);
            amount = sc.nextDouble();
            if (amount < 0) {
                System.out.println("Amount must be greater than zero.");
            } else if (amount > acctBal) {
                System.out.printf("Amount must not be greater than\nbalance of $%.02f.", acctBal);
            }
        } while (amount < 0 || amount > acctBal);

        // finally do the transfer
        theUser.addAccountTransaction(fromAcct, -1 * amount, String.format("Transfer from account %s",
                theUser.getAcctUUID(fromAcct)));
        theUser.addAccountTransaction(toAcct, amount, String.format("Transfer to account %s",
                theUser.getAcctUUID(toAcct)));
    }


    /**
     * Process a fund withdraw from an account
     * @param theUser the logged-in User object
     * @param sc      the Scanner object used for input
     */
    public static void withdrawFunds(User theUser, Scanner sc) {

        // inits
        int fromAcct;
        double amount;
        double acctBal;
        String memo;

        fromAcct = getTheAccount(theUser,sc, "withdraw");
        acctBal = theUser.getAcctBalance(fromAcct);

        // get the amount to transfer
        do {
            System.out.printf("Enter the amount to withdraw (max $%.02f): $", acctBal);
            amount = sc.nextDouble();
            if (amount < 0) {
                System.out.println("Amount must be greater than zero.");
            } else if (amount > acctBal) {
                System.out.printf("Amount must not be greater than\nbalance of $%.02f.\n", acctBal);
            }
        } while (amount < 0 || amount > acctBal);

        // gobble up the rest of previous input line
        sc.nextLine();

        // get a memo
        System.out.print("Enter a memo: ");
        memo = sc.nextLine();

        // do the withdraw
        theUser.addAccountTransaction(fromAcct, -1 * amount, memo);
    }


    /**
     * Process a fund deposit to an account
     * @param theUser the logged-in User object
     * @param sc      the Scanner object used for input
     */
    private static void depositFunds(User theUser, Scanner sc) {

        // inits
        int toAcct;
        double amount;
        double acctBal;
        String memo;

        toAcct = getTheAccount(theUser,sc, "deposit");
        acctBal = theUser.getAcctBalance(toAcct);


        // get the amount to transfer
        do {
            System.out.printf("Enter the amount to deposit (max $%.02f): $", acctBal);
            amount = sc.nextDouble();
            if (amount < 0) {
                System.out.println("Amount must be greater than zero.");
            }
        } while (amount < 0);

        // gobble up the rest of previous input line
        sc.nextLine();

        // get a memo
        System.out.print("Enter a memo: ");
        memo = sc.nextLine();

        // do the deposit
        theUser.addAccountTransaction(toAcct, amount, memo);
    }

    private static int getTheAccount(User theUser, Scanner sc, String actionName) {

        int acc;

        do {
            System.out.printf("Enter the number (1-%d) of the account\nto %s: ", theUser.numAccounts(), actionName);
            acc = sc.nextInt() - 1;
            if (acc < 0 || acc >= theUser.numAccounts()) {
                System.out.println("Invalid account. Please try again");
            }
        } while (acc < 0 || acc >= theUser.numAccounts());

        return acc;
    }

}

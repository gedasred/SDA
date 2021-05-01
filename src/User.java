import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.security.MessageDigest;

public class User extends AbstractUUID {

    private final String firstName;

    private final String lastName;

    private final String uuid;

    /**
     * The MD5 hash of the user's pin number.
     */
    private byte pinHash[];

    private ArrayList<Account> accounts;


    /**
     * Create a new user...
     *
     * @param firstName the user's fist name
     * @param lastName  the user's fist name
     * @param pin       the user's account pin number
     * @param theBank   the bank object the user is customer of
     */
    public User(String firstName, String lastName, String pin, Bank theBank) {
        this.firstName = firstName;
        this.lastName = lastName;

        // store the pin's MD5 hash, rather than original value for security
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            this.pinHash = md.digest(pin.getBytes());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("error, caught NoSuchAlgorithmException");
            e.printStackTrace();
            System.exit(1);
        }

        // get a new, unique universal ID for the user
        this.uuid = theBank.getNewUserUUID();

        // create empty list of accounts
        this.accounts = new ArrayList<>();

        //print log message
        System.out.printf("New user %s, %s with ID %s created.\n", lastName, firstName, this.uuid);
    }


    public void addAccount(Account account) {
        this.accounts.add(account);
    }


    public String getUUID() {
        return this.uuid;
    }


    /**
     * Check whether a given pin matches the true User pin
     *
     * @param aPin the pin to check
     * @return whether the pin is valid or not
     */
    public boolean validatePin(String aPin) {

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return MessageDigest.isEqual(md.digest(aPin.getBytes()), this.pinHash);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("error, caught NoSuchAlgorithmException");
            e.printStackTrace();
            System.exit(1);
        }

        return false;
    }


    public String getFirstName() {
        return this.firstName;
    }


    /**
     * Print summaries for the accounts of this user
     */
    public void printAccountsSummary() {

        System.out.printf("\n\n%s's accounts summary\n", this.firstName);
        for (int a = 0; a < this.accounts.size(); a++) {
            System.out.printf("    %d) %s\n", a+1, accounts.get(a).getSummaryLine());
        }
        System.out.println();

    }


    public int numAccounts() {
        return this.accounts.size();
    }


    public void printAcctTransHistory(int acctIdx) {
        this.accounts.get(acctIdx).printTransHistory();
    }


    public double getAcctBalance(int acctIdx) {
        return this.accounts.get(acctIdx).getBalance();
    }

    /**
     * Get the UUID of a particular account
     * @param acctIdx   the index of account to use
     * @return          the UUID of the account
     */
    public String getAcctUUID(int acctIdx) {
        return this.accounts.get(acctIdx).getUUID();
    }

    public void addAccountTransaction(int acctIdx, double amount, String memo) {
        this.accounts.get(acctIdx).addTransaction(amount, memo);
    }

}

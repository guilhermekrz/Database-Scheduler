import java.util.ArrayList;
import java.util.List;


public class TransactionList {
	private static TransactionList instance;
	private List<Transaction> transactionList;
	
	public static TransactionList getInstance(){
		if(instance == null){
			instance = new TransactionList();
		}
		return instance;
	}
	
	private TransactionList(){
		transactionList = new ArrayList<>();
	}	
	
	public Transaction addTransaction(int transactionNumber){
		Transaction t = getTransaction(transactionNumber);
		if(t == null){
			t = new Transaction(transactionNumber);
			if(transactionList.add(t)){
				return t;
			}
			else{
				return null;
			}
		}
		return t;
	}
	
	public List<Transaction> getTransactionList(){
		return transactionList;
	}
	
	public Transaction getTransaction(int transactionNumber){
		for(Transaction t:transactionList){
			if(t.getTransactionNumber() == transactionNumber){
				return t;
			}
		}
		return null;
	}
}

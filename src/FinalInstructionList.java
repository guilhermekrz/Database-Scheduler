import java.util.ArrayList;
import java.util.List;


public class FinalInstructionList {
	private static FinalInstructionList instance;
	private List<Instruction> instructionList;
	private List<Transaction> excludedTransactions;
	private List<String> reasonsExcludedTransactions;
	
	private FinalInstructionList(){
		instructionList = new ArrayList<>();
		excludedTransactions = new ArrayList<>();
		reasonsExcludedTransactions = new ArrayList<>();
	}
	
	public static FinalInstructionList getInstance(){
		if(instance == null){
			instance = new FinalInstructionList();
		}
		return instance;
	}
	
	public boolean addInstruction(Instruction i){
		return instructionList.add(i);
	}
	
	public void addExcludedTransaction(Transaction t,String reason){
		excludedTransactions.add(t);
		reasonsExcludedTransactions.add(reason);
	}
	
	public String getFinalScheduling(){
		String s = "";
		
		for(int i=0;i<excludedTransactions.size();i++){
			s += "Transaction " + excludedTransactions.get(i).getTransactionNumber() + " - " + reasonsExcludedTransactions.get(i) + "\n";
		}
		
		s += "\nEscala:\n";
		
		for(int i=0;i<instructionList.size();i++){
			boolean add = true;
			for(Transaction t:excludedTransactions){
				if(instructionList.get(i).getTransaction() == t){
					add = false;
					break;
				}
			}
			if(add){
				s += instructionList.get(i).toString() + "\n";
			}
			
		}
		
		return s;
	}
}

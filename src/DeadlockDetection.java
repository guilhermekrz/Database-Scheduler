import java.util.ArrayList;
import java.util.List;


public class DeadlockDetection {
	public static List<Transaction> isDeadlocked(){
		List<Transaction> deadlockCycle = new ArrayList<Transaction>();
		
		List<Transaction> transactionList = TransactionList.getInstance().getTransactionList();
		List<Dado> dadosList = DadosList.getInstance().getDadosList();
		
		for(Transaction t1:transactionList){
			for(Transaction t2:transactionList){
				if(t1.getTransactionNumber() != t2.getTransactionNumber()){
					for(Dado d1:dadosList){
						for(Dado d2:dadosList){
							if(d1.getDado()!=d2.getDado()){
								if(d1.isNaListaWait(t1) && d1.isNoControle(t2) && d2.isNoControle(t1) && d2.isNaListaWait(t2)){
									deadlockCycle.add(t1);
									deadlockCycle.add(t2);
									return deadlockCycle;
								}
							}
						}
					}
				}
			}
		}		
		return deadlockCycle;
	}
}

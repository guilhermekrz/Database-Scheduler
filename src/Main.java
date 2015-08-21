import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main {
	public static void main(String args[]){
		
		TransactionList transactionList = TransactionList.getInstance();
		DadosList dadosList = DadosList.getInstance();
		List<Instruction> instructionList = new ArrayList<>();
		
		BufferedReader br = null;
	    try {
	    	String pathToFile = "./Escalas/EscalaCorreta.txt";
	    	//String pathToFile = "./Escalas/EscalaErroLockT1.txt";
	    	//String pathToFile = "./Escalas/EscalaErro2PLT1.txt";
	    	//String pathToFile = "./Escalas/EscalaDeadlockT1T4.txt";
	    	br = new BufferedReader(new FileReader(pathToFile));
	        String line = br.readLine();

	        while (line != null) {
	        	String[] result = line.split(":");
	        	
	        	if(result.length==3){
	        		int transactionNumber = Integer.parseInt(result[0]);
	        		
	        		InstructionType instructionType = null;
	        		if(result[1].equals("LOCK-S")){
	        			instructionType = InstructionType.LOCK_S;
	        		}
	        		else if(result[1].equals("LOCK-X")){
	        			instructionType = InstructionType.LOCK_X;
	        		}
	        		else if(result[1].equals("READ")){
	        			instructionType = InstructionType.READ;
	        		}
	        		else if(result[1].equals("WRITE")){
	        			instructionType = InstructionType.WRITE;
	        		}
	        		else if(result[1].equals("UNLOCK")){
	        			instructionType = InstructionType.UNLOCK;
	        		}
	        		else{
	        			System.out.println("ERRO! InstructionType == null" + result[1]);
	        		}
	        		
	        		char dado = result[2].charAt(0);
	        		
	        		Dado d = dadosList.addDado(dado);
	        		Transaction t = transactionList.addTransaction(transactionNumber);
	        		Instruction i = new Instruction(instructionType,d,t);
	        		instructionList.add(i);
	        		t.addInstruction(i);
	        	}
	        	else{
	        		System.out.println("ERRO!!! Cada linha deve conter somente 3 tokens.");
	        	}
	            line = br.readLine();
	        }
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
	        try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    
	    
	    //Comeca a execucao
	    for(Instruction i:instructionList){
	    	i.preExecuta();
	    }
	    
	    System.out.println(FinalInstructionList.getInstance().getFinalScheduling());
	}
}

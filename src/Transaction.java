import java.util.ArrayList;
import java.util.List;


public class Transaction {
	private int transactionNumber;
	private List<Instruction> listInstruction;
	private boolean bloqueada;
	
	private int proximoIndiceAExecutar;
	private int indicePossoExecutar;
	private int indiceBloqueado;
	
	private boolean emExpansao;
	
	private boolean utilizarEstaTransacao;
	
	public Transaction(int transactionNumber) {
		super();
		this.transactionNumber = transactionNumber;
		listInstruction = new ArrayList<>();
		bloqueada = false;
		proximoIndiceAExecutar = 0;
		indicePossoExecutar = -1;
		indiceBloqueado = -1;
		emExpansao = true;
		utilizarEstaTransacao = true;
	}

	public int getTransactionNumber() {
		return transactionNumber;
	}
	
	public Instruction getInstruction(int index){
		return listInstruction.get(index);
	}

	public List<Instruction> getListInstruction() {
		return listInstruction;
	}

	public boolean addInstruction(Instruction i) {
		return this.listInstruction.add(i);
	}
	
	public void lock(){
		bloqueada = true;
	}
	
	public void unlock(){
		bloqueada = false;
	}
	
	public void setNaoUtilizarEstaTransacao(){
		utilizarEstaTransacao = false;
	}
	
	public boolean utilizarEstaTransacao(){
		return utilizarEstaTransacao;
	}
	
	public String toString(){
		String s = "Transaction number: " + transactionNumber + "\n";
		
		for(Instruction i:listInstruction){
			s += i.toString() + "\n";
		}
		return s;
	}

	public boolean isEmExpansao() {
		return emExpansao;
	}

	public void setEmDecadencia() {
		this.emExpansao = false;
	}

	public void addToExecutionOneMoreInstruction(Instruction i) {
		indicePossoExecutar++;
	}
	
	public void processExecutionList(){
		if(utilizarEstaTransacao && !bloqueada && indicePossoExecutar >=0 && indicePossoExecutar < listInstruction.size()){
			for(int i=proximoIndiceAExecutar;i<=indicePossoExecutar;i++){
				boolean b = listInstruction.get(i).executa();
						
				//Detectar deadlock
				List<Transaction> deadlockCycle = null;
				if((deadlockCycle = DeadlockDetection.isDeadlocked()).size() != 0){
					String s = "Esta transacao entrou em deadlock com as seguintes transacoes:";
					for(int j=1;j<deadlockCycle.size();j++){
						s += " " + deadlockCycle.get(j).getTransactionNumber();
					}
					s += ". E por isto ela será abortada, pois estamos usando o criterio de abortar a transacao que pediu o lock por ultimo. \n";
					FinalInstructionList.getInstance().addExcludedTransaction(this, s);
					this.setNaoUtilizarEstaTransacao();
					break;
				}
				
				if(b){
					listInstruction.get(i).posExecuta();
					proximoIndiceAExecutar++;
				}
				else{
					break;
				}
			}
		}
		
		if(!utilizarEstaTransacao){
			//retirar esta transacao de todas as listas
			for(int i=0;i<listInstruction.size();i++){
				listInstruction.get(i).getDado().removerTransacao(this);
			}
		}
	}
} 

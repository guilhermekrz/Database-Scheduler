import java.util.List;

public class Instruction {
	private InstructionType instructionType;
	private Dado dado;
	private int ordemDeExecucao;
	private Transaction transaction;
	
	public Instruction(InstructionType instructionType, Dado dado, Transaction transaction) {
		super();
		this.instructionType = instructionType;
		this.dado = dado;
		this.ordemDeExecucao = -1;
		this.transaction = transaction;
	}
	
	public InstructionType getInstructionType() {
		return instructionType;
	}
	
	public Dado getDado() {
		return dado;
	}
	
	public int getOrdemDeExecucao() {
		return ordemDeExecucao;
	}
	
	public void setOrdemDeExecucao(int ordemDeExecucao) {
		this.ordemDeExecucao = ordemDeExecucao;
	}
	
	public Transaction getTransaction(){
		return transaction;
	}
	
	public String toString(){
		return instructionType.name() + transaction.getTransactionNumber() + "(" + dado.toString() + ")"; 
	}
	
	public void preExecuta(){
		transaction.addToExecutionOneMoreInstruction(this);
		transaction.processExecutionList();
	}
	
	public boolean executa(){
		if(instructionType == InstructionType.LOCK_S){			
			if(!transaction.isEmExpansao()){
				FinalInstructionList.getInstance().addExcludedTransaction(transaction, "Esta transacao quis realizar um bloqueio, porem ela nao esta mais na fase de expansao.");
				transaction.setNaoUtilizarEstaTransacao();
				return false;
			}
			if(dado.solicitacaoDeBloqueioCompartilhado(transaction)){
				return true;
			}
			return false;
		}
		else if(instructionType == InstructionType.LOCK_X){
			if(!transaction.isEmExpansao()){
				FinalInstructionList.getInstance().addExcludedTransaction(transaction, "Esta transacao quis realizar um bloqueio, porem ela nao esta mais na fase de expansao.");
				transaction.setNaoUtilizarEstaTransacao();
				return false;
			}			
			if(dado.solicitacaoDeBloqueioExclusivo(transaction)){
				return true;
			}
			return false;
		}
		else if(instructionType == InstructionType.READ){
			if(dado.read(transaction)){
				//posExecuta();
				return true;
			}
			else{
				//System.out.println(toString() + "ERRO!! Esta transacao nao possui lock de leitura ou de escrita");
				FinalInstructionList.getInstance().addExcludedTransaction(transaction, "Esta transacao quis realizar uma operacao de leitura, porem nao possuia o lock de leitura ou de escrita.");
				transaction.setNaoUtilizarEstaTransacao();
				return false;
			}
		}
		else if(instructionType == InstructionType.WRITE){
			if(dado.write(transaction)){
				//posExecuta();
				return true;
			}
			else{
				//System.out.println(toString() + "ERRO!! Esta transacao nao possui lock de escrita");
				FinalInstructionList.getInstance().addExcludedTransaction(transaction, "Esta transacao quis realizar uma operacao de escrita, porem nao possuia o lock de de escrita.");
				transaction.setNaoUtilizarEstaTransacao();
				return false;
			}
		}
		else if(instructionType == InstructionType.UNLOCK){
			dado.solicitacaoDeDesbloqueio(transaction);
			transaction.setEmDecadencia();
			return true;
			//System.out.println(toString());
			
			//dado.processaFilaWait();
			//posExecuta();
		}
		return false;
	}
	
	public void posExecuta(){
		FinalInstructionList.getInstance().addInstruction(this);
		//System.out.println(toString());
		if(instructionType == InstructionType.UNLOCK){
			dado.processaFilaWait();
		}
		
	}
}

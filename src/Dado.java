import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class Dado {
	private int dado;
	private char letraDado;
	
	private StatusBloqueio statusBloqueio;
	private List<Transaction> listaRead;
	private Transaction write;
	
	private Queue<Transaction> listaWait;
	private Queue<InstructionType> listLockType;
	
	public Dado(int dado, char letraDado){
		this.dado = dado;
		this.letraDado = letraDado;
		statusBloqueio = StatusBloqueio.UNLOCKED;
		listaRead = new ArrayList<>();
		write = null;
		listaWait = new LinkedList<>();
		listLockType = new LinkedList<>();
	}

	public int getDado() {
		return dado;
	}

	public char getLetraDado() {
		return letraDado;
	}
	
	public boolean isNaListaWait(Transaction t) {		
		return listaWait.contains(t);
	}
	
	public boolean isNoControle(Transaction t) {		
		return listaRead.contains(t) || write == t;
	}
	
	public List<Transaction> getListaTransactionsUsingThisData() {
		List<Transaction> temp = new ArrayList<Transaction>(listaRead);
		if(write != null){
			temp.add(write);
		}
		return temp;
	}

	public boolean solicitacaoDeBloqueioCompartilhado(Transaction transactionRequerinte){
		if(statusBloqueio.compareTo(StatusBloqueio.UNLOCKED) == 0){
			listaRead.add(transactionRequerinte);
			statusBloqueio = StatusBloqueio.SHARED_LOCK;
			return true;
		}
		else if(statusBloqueio.compareTo(StatusBloqueio.SHARED_LOCK) == 0){
			listaRead.add(transactionRequerinte);
			return true;
		}
		else{ //if(statusBloqueio.compareTo(StatusBloqueio.EXCLUSIVE_LOCK) == 0)
			listaWait.add(transactionRequerinte);
			listLockType.add(InstructionType.LOCK_S);
			transactionRequerinte.lock();
			return false;
		}
	}
	
	public boolean solicitacaoDeBloqueioExclusivo(Transaction transactionRequerinte){
		if(statusBloqueio.compareTo(StatusBloqueio.UNLOCKED) == 0){
			write = transactionRequerinte;
			statusBloqueio = StatusBloqueio.EXCLUSIVE_LOCK;
			return true;
		}
		else if(listaRead.size() == 1 && listaRead.get(0) == transactionRequerinte && statusBloqueio.compareTo(StatusBloqueio.SHARED_LOCK) == 0){
			write = transactionRequerinte;
			statusBloqueio = StatusBloqueio.EXCLUSIVE_LOCK;
			listaRead.remove(0);
			return true;
		}
		else{ //if(statusBloqueio.compareTo(StatusBloqueio.EXCLUSIVE_LOCK) == 0 || statusBloqueio.compareTo(StatusBloqueio.SHARED_LOCK) == 0)
			listaWait.add(transactionRequerinte);
			listLockType.add(InstructionType.LOCK_X);
			transactionRequerinte.lock();
			return false;
		}
	}
	
	public void solicitacaoDeDesbloqueio(Transaction transactionRequerinte){
		if(statusBloqueio.compareTo(StatusBloqueio.SHARED_LOCK) == 0){
			if(retiraDaListaRead(transactionRequerinte)){
				
			}
			else{
				System.out.println("ERRO!!!!!! Nao havia esta transacao na lista read.");
			}
			if(listaRead.isEmpty()){
				statusBloqueio = StatusBloqueio.UNLOCKED;
			}
		}
		else{ //if(statusBloqueio.compareTo(StatusBloqueio.EXCLUSIVE_LOCK) == 0)
			write = null;
			statusBloqueio = StatusBloqueio.UNLOCKED;
		}
	}
	
	public boolean retiraDaListaRead(Transaction t){
		return listaRead.remove(t);
	}
	
	public String toString(){
		return letraDado+"";
	}
	
	public void processaFilaWait(){
		for(int i=0;i<listaWait.size();i++){
			if(!listaWait.peek().utilizarEstaTransacao()){
				listaWait.poll();
				listLockType.poll();
				continue;
			}			
			
			if(listLockType.peek()==InstructionType.LOCK_X){
				if(listaRead.isEmpty()){
					write = listaWait.poll();
					listLockType.poll();
					write.unlock();
					write.processExecutionList();
					break;
				}
				else{
					break;
				}
			}
			else if(listLockType.peek()==InstructionType.LOCK_S){
				if(write == null){
					Transaction t = listaWait.poll();
					listaRead.add(t);
					t.unlock();
					t.processExecutionList();					
				}
				else{
					break;
				}
			}
		}
	}
	
	public boolean read(Transaction t){
		if(listaRead.contains(t) || write == t){
			return true;
		}
		return false;
	}
	
	public boolean write(Transaction t){
		if(write == t){
			return true;
		}
		return false;
	}	
	
	public void removerTransacao(Transaction t){
		
		
		if(statusBloqueio.compareTo(StatusBloqueio.UNLOCKED) == 0){
			
		}
		else if(statusBloqueio.compareTo(StatusBloqueio.SHARED_LOCK) == 0){
			if(retiraDaListaRead(t)){
				if(listaRead.isEmpty()){
					statusBloqueio = StatusBloqueio.UNLOCKED;
					processaFilaWait();
				}
			}
		}
		else{ //if(statusBloqueio.compareTo(StatusBloqueio.EXCLUSIVE_LOCK) == 0 )
			if(write == t){
				statusBloqueio = StatusBloqueio.UNLOCKED;
				write = null;
				processaFilaWait();
			}
		}
	}
}

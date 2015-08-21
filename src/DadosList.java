import java.util.ArrayList;
import java.util.List;


public class DadosList {
	private List<Dado> dadosList;
	private int ultimoIntDado = 0;
	private static DadosList instance;
	
	public static DadosList getInstance(){
		if(instance == null){
			instance = new DadosList();
		}
		return instance;
	}
	
	private DadosList(){
		dadosList = new ArrayList<>();
	}	
	
	public Dado addDado(char letraDado){
		Dado d = getDado(letraDado);
		if(d == null){
			d = new Dado(ultimoIntDado,letraDado);
			ultimoIntDado++;
			if(dadosList.add(d)){
				return d;
			}
			else{
				return null;
			}
		}
		return d;
	}
	
	public Dado getDado(int dado){
		for(Dado d:dadosList){
			if(d.getDado() == dado){
				return d;
			}
		}
		return null;
	}
	
	public Dado getDado(char letraDado){
		for(Dado d:dadosList){
			if(d.getLetraDado() == letraDado){
				return d;
			}
		}
		return null;
	}

	public List<Dado> getDadosList() {
		return dadosList;		
	}
}

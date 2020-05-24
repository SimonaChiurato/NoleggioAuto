package polito.it.noleggio.model;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.PriorityQueue;

import polito.it.noleggio.model.Event.EventType;

public class Simulator {
	// coda degli eventi
	PriorityQueue<Event> queue= new PriorityQueue<Event>();
	
	// parametri di simulazione

	private int NC=10;
	private Duration T_IN=Duration.of(10, ChronoUnit.MINUTES); // intervallo arrivo tra clienti
	
	private final LocalTime oraApertura= LocalTime.of(8, 00);
	private final LocalTime oraChiusura= LocalTime.of(17, 00);
	// modello del mondo
	private int nAuto; // auto disponibili 0<nAuto<NC
	
	
	//valori da calcolare
	private int clienti;
	private int insoddisfatti;
	
	
	// Restituire risultati
	public int getClienti() {
		return clienti;
	}

	public int getInsoddisfatti() {
		return insoddisfatti;
	}

	// impostare parametri
	public void setNumCars(int N) {
		this.NC=N;
		
	}
	
	public void setClientFrequency(Duration d) {
		this.T_IN=d;
	}
	
	
	public void run() {
		this.nAuto= this.NC;
		this.clienti=0;
		this.insoddisfatti=0;
		
		this.queue.clear();
		LocalTime oraArrivoCliente= this.oraApertura;
		do {
			Event e= new Event(oraArrivoCliente, EventType.NEW_CLIENT);
			this.queue.add(e);
			oraArrivoCliente= oraArrivoCliente.plus(this.T_IN);
			
		}while(oraArrivoCliente.isBefore(this.oraChiusura));
		
		while(!this.queue.isEmpty()){
			Event e= this.queue.poll();
			processEvent(e);
			
		}
	}
	
	private void processEvent(Event e) {
		switch(e.getType()) {
			case CAR_RETURNED:
				this.nAuto++;
				
				break;
				
			case NEW_CLIENT:
				
				if(this.nAuto>0) {
					this.clienti++; // aggiorna risultati
					this.nAuto--; // aggiorna modello auto
					double num= Math.random();
					Duration travel;
					if(num<(1.0/3.0)) {
						travel= Duration.of(1, ChronoUnit.HOURS);
					}else if(num<(2.0/3.0)) {
						travel= Duration.of(2, ChronoUnit.HOURS);
					}else {
						travel= Duration.of(3, ChronoUnit.HOURS);
					}
					Event nuovo= new Event(e.getTime().plus(travel), EventType.CAR_RETURNED);// crea nuovi eventi
					this.queue.add(nuovo);
				}else {
					this.clienti++;
					this.insoddisfatti++;
				}
				break;
			}
	}
}

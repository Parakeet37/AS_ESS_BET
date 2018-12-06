package state;

import eventObserver.Evento;

public class AbertoState implements EventoState {

	@Override
	public void changeState(Evento e) {

	}

	@Override
	public String toString() {
		return "ABERTO";
	}
	
	
}

package state;

import eventObserver.Evento;

public class FechadoState implements EventoState {

	@Override
	public void changeState(Evento e) {
		e.setEstado(this);
		e.notifyObservers();
	}

	@Override
	public String toString() {
		return "FECHADO";
	}

}

package com.example.multidb.procedures;

public class Migracao {
	
	private String placa;
	private Long ticket;
	
	public Migracao(String placa, Long ticket) {
		this.placa = placa;
		this.ticket = ticket;
	}
	
	public String getPlaca() {
		return placa;
	}
	public void setPlaca(String placa) {
		this.placa = placa;
	}
	public Long getTicket() {
		return ticket;
	}
	public void setTicket(Long ticket) {
		this.ticket = ticket;
	}

	@Override
	public String toString() {
		return "Migracao [placa=" + placa + ", ticket=" + ticket + "]";
	}
	
}

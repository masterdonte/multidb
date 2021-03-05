package com.example.multidb.model.banco3;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(schema = "EMAP", name = "SALA")
public class Sala {

	@Id
	@SequenceGenerator(name = "default_gen", sequenceName = "sala_seq", schema = "EMAP", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_gen")
	private Long id;

	private String nome;
	
	public Sala(String nome) {
		this.nome = nome;
	}
	
	public Sala() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sala other = (Sala) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Sala [id=" + id + ", nome=" + nome + "]";
	}


}
package com.example.multidb.repository.postgres;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.multidb.model.postgres.Livro;

public interface LivroRepository extends JpaRepository<Livro, Long> {

}


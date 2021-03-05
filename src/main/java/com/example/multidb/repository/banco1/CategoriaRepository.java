package com.example.multidb.repository.banco1;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.multidb.model.banco1.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

}

